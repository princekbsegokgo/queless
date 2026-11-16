package com.queless.web.service;

import com.queless.web.model.Business;
import com.queless.web.model.Queue;
import com.queless.web.model.Ticket;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Shared in-memory demo queue. Mutations are synchronized to prevent two patients
 * receiving the same position or two operators calling the same patient.
 * IMPORTANT: no persistent database is configured; records reset on restart.
 */
public class QueueManager {
    public static final ZoneId CLINIC_TIME_ZONE = ZoneId.of("Africa/Gaborone");
    private static final int AVERAGE_SERVICE_MINUTES = 5;

    private final Map<Long, Business> businesses = new ConcurrentHashMap<>();
    private final Map<Long, Queue> queues = new ConcurrentHashMap<>();
    private final Map<Long, Ticket> tickets = new ConcurrentHashMap<>();
    private final Map<LocalDate, Integer> dailyPeakWaiting = new ConcurrentHashMap<>();
    private final AtomicLong businessIdSeq = new AtomicLong(1);
    private final AtomicLong queueIdSeq = new AtomicLong(1);
    private final AtomicLong ticketIdSeq = new AtomicLong(1);
    private final NotificationService notificationService = new NotificationService();

    public QueueManager() {
        long businessId = businessIdSeq.getAndIncrement();
        businesses.put(businessId, new Business(
                businessId, "Princess Marina Clinic", "Healthcare", Business.Tier.PRO));
        long queueId = queueIdSeq.getAndIncrement();
        queues.put(queueId, new Queue(queueId, businessId, "General Consultation"));
        long pharmacyQueueId = queueIdSeq.getAndIncrement();
        queues.put(pharmacyQueueId, new Queue(pharmacyQueueId, businessId, "Pharmacy"));
        long labQueueId = queueIdSeq.getAndIncrement();
        queues.put(labQueueId, new Queue(labQueueId, businessId, "Laboratory"));
    }

    public Collection<Business> getAllBusinesses() {
        return List.copyOf(businesses.values());
    }

    public List<Queue> getQueuesForBusiness(long businessId) {
        return queues.values().stream()
                .filter(q -> q.getBusinessId() == businessId)
                .sorted(Comparator.comparingLong(Queue::getQueueId))
                .collect(Collectors.toList());
    }

    public Optional<Queue> getQueue(long queueId) {
        return Optional.ofNullable(queues.get(queueId));
    }

    public Optional<Ticket> getTicket(long ticketId) {
        return Optional.ofNullable(tickets.get(ticketId));
    }

    public Optional<Ticket> getTicketByAccessToken(String accessToken) {
        if (accessToken == null || accessToken.length() != 32) return Optional.empty();
        return tickets.values().stream()
                .filter(t -> accessToken.equals(t.getAccessToken()))
                .findFirst();
    }

    public int getPeopleAhead(Ticket ticket) {
        if (ticket.getStatus() != Ticket.Status.WAITING
                && ticket.getStatus() != Ticket.Status.NOTIFIED) return 0;
        int waitingBefore = (int) getQueueStatus(ticket.getQueueId()).stream()
                .filter(t -> t.getPosition() < ticket.getPosition())
                .count();
        int servingBefore = (int) tickets.values().stream()
                .filter(t -> t.getQueueId() == ticket.getQueueId()
                        && t.getStatus() == Ticket.Status.SERVING
                        && t.getPosition() < ticket.getPosition())
                .count();
        return waitingBefore + servingBefore;
    }

    public int estimateWaitMinutes(Ticket ticket) {
        return getPeopleAhead(ticket) * AVERAGE_SERVICE_MINUTES;
    }

    public synchronized boolean cancelTicketByAccessToken(String accessToken) {
        Optional<Ticket> ticket = getTicketByAccessToken(accessToken);
        if (ticket.isEmpty()) return false;
        Ticket t = ticket.get();
        if (t.getStatus() != Ticket.Status.WAITING
                && t.getStatus() != Ticket.Status.NOTIFIED) return false;
        t.setStatus(Ticket.Status.CANCELLED);
        return true;
    }

    public List<Ticket> getAllTickets() {
        return new ArrayList<>(tickets.values());
    }

    public List<Ticket> getQueueStatus(long queueId) {
        return tickets.values().stream()
                .filter(t -> t.getQueueId() == queueId)
                .filter(t -> t.getStatus() == Ticket.Status.WAITING
                          || t.getStatus() == Ticket.Status.NOTIFIED)
                .sorted(Comparator.comparingInt(Ticket::getPosition))
                .collect(Collectors.toList());
    }

    public Optional<Ticket> getServingTicket(long queueId) {
        return tickets.values().stream()
                .filter(t -> t.getQueueId() == queueId
                          && t.getStatus() == Ticket.Status.SERVING)
                .findFirst();
    }

    /** Highest number waiting at once today, used for the weekly analytics chart. */
    public int getDailyPeakWaiting(LocalDate day) {
        return dailyPeakWaiting.getOrDefault(day, 0);
    }

    private void recordPeak(long queueId) {
        int count = getQueueStatus(queueId).size();
        dailyPeakWaiting.merge(LocalDate.now(CLINIC_TIME_ZONE), count, Math::max);
    }

    /** Throws IllegalStateException if a queue is paused or does not exist. */
    public synchronized Ticket joinQueue(long queueId, String userName) {
        Queue queue = queues.get(queueId);
        if (queue == null || !queue.isOpen()) {
            throw new IllegalStateException("Queue unavailable or paused");
        }
        String safeName = userName == null || userName.isBlank()
                ? "Guest" : userName.trim();
        if (safeName.length() > 80) safeName = safeName.substring(0, 80);
        int nextPosition = tickets.values().stream()
                .filter(t -> t.getQueueId() == queueId)
                .mapToInt(Ticket::getPosition)
                .max().orElse(0) + 1;
        Ticket ticket = new Ticket(ticketIdSeq.getAndIncrement(), queueId,
                safeName, nextPosition);
        tickets.put(ticket.getTicketId(), ticket);
        recordPeak(queueId);
        return ticket;
    }

    public synchronized void leaveQueue(long ticketId) {
        Ticket ticket = tickets.get(ticketId);
        if (ticket != null && (ticket.getStatus() == Ticket.Status.WAITING
                             || ticket.getStatus() == Ticket.Status.NOTIFIED)) {
            ticket.setStatus(Ticket.Status.CANCELLED);
        }
    }

    /**
     * Finish the currently serving ticket, then call the next waiting ticket.
     * Returns false if the queue is paused or nobody can be advanced.
     */
    public synchronized boolean callNextTicket(long queueId) {
        Queue queue = queues.get(queueId);
        if (queue == null || !queue.isOpen()) return false;

        Optional<Ticket> current = getServingTicket(queueId);
        Optional<Ticket> next = getQueueStatus(queueId).stream().findFirst();
        if (current.isEmpty() && next.isEmpty()) return false;

        current.ifPresent(ticket -> ticket.setStatus(Ticket.Status.SERVED));
        next.ifPresent(ticket -> {
            ticket.setStatus(Ticket.Status.SERVING);
            queue.setCurrentServingPosition(ticket.getPosition());
            notificationService.notifyTurn(ticket);
        });
        recordPeak(queueId);
        evaluateNotificationThresholds(queueId, queue.getCurrentServingPosition());
        return true;
    }

    /**
     * Showcase-only helper. Add four recognizable sample names to an empty
     * department and call the first ticket so the dashboard can be demonstrated
     * without preparing four browsers. Never use this with real patients.
     */
    public synchronized boolean loadSampleTickets(long queueId) {
        Queue queue = queues.get(queueId);
        if (queue == null || !queue.isOpen()
                || !getQueueStatus(queueId).isEmpty()
                || getServingTicket(queueId).isPresent()) {
            return false;
        }
        for (String name : new String[] {"T. Kgosi", "B. Mmusi", "K. Seleka", "N. Ditlhake"}) {
            joinQueue(queueId, name);
        }
        callNextTicket(queueId);
        return true;
    }

    public synchronized boolean completeServingTicket(long queueId) {
        Optional<Ticket> active = getServingTicket(queueId);
        if (active.isEmpty()) return false;
        active.get().setStatus(Ticket.Status.SERVED);
        return true;
    }

    public synchronized boolean setQueueOpen(long queueId, boolean open) {
        Queue queue = queues.get(queueId);
        if (queue == null) return false;
        queue.setOpen(open);
        return true;
    }

    public int estimateWaitTimeMinutes(long queueId, int position) {
        Queue queue = queues.get(queueId);
        if (queue == null) return 0;
        int positionsAhead = Math.max(0, position - queue.getCurrentServingPosition() - 1);
        return positionsAhead * AVERAGE_SERVICE_MINUTES;
    }

    private void evaluateNotificationThresholds(long queueId, int servingPosition) {
        getQueueStatus(queueId).stream().findFirst().ifPresent(next -> {
            int positionsAway = next.getPosition() - servingPosition;
            if (positionsAway == NotificationService.NOTIFY_FAR_THRESHOLD
                    || positionsAway == NotificationService.NOTIFY_NEAR_THRESHOLD) {
                notificationService.sendPush(next, positionsAway);
            }
        });
    }
}
