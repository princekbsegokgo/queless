package com.queless.web.service;

import com.queless.web.model.Business;
import com.queless.web.model.Queue;
import com.queless.web.model.Ticket;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Central queue operations and state management (RQ01, RQ02, RQ03), as
 * named in Section 4.1 of the project report. Uses an in-memory store for
 * this MVP; a production build would replace the maps below with a real
 * database (matching the ERD in Figure 3), consistent with RQ04's
 * requirement to protect stored data.
 *
 * This class is a singleton, shared across servlet requests via
 * ServletContext, so all users see the same live queue state.
 */
public class QueueManager {

    private final Map<Long, Business> businesses = new ConcurrentHashMap<>();
    private final Map<Long, Queue> queues = new ConcurrentHashMap<>();
    private final Map<Long, Ticket> tickets = new ConcurrentHashMap<>();

    private final AtomicLong businessIdSeq = new AtomicLong(1);
    private final AtomicLong queueIdSeq = new AtomicLong(1);
    private final AtomicLong ticketIdSeq = new AtomicLong(1);

    private final NotificationService notificationService = new NotificationService();

    public QueueManager() {
        seedDemoData();
    }

    private void seedDemoData() {
        long businessId = businessIdSeq.getAndIncrement();
        businesses.put(businessId, new Business(
            businessId, "Princess Marina Hospital - Outpatients", "Healthcare", Business.Tier.PRO
        ));
        long queueId = queueIdSeq.getAndIncrement();
        queues.put(queueId, new Queue(queueId, businessId, "General Consultation"));
    }

    // ---- Read operations (RQ01, RQ03) ----

    public Collection<Business> getAllBusinesses() {
        return businesses.values();
    }

    public List<Queue> getQueuesForBusiness(long businessId) {
        return queues.values().stream()
            .filter(q -> q.getBusinessId() == businessId)
            .collect(Collectors.toList());
    }

    public Optional<Queue> getQueue(long queueId) {
        return Optional.ofNullable(queues.get(queueId));
    }

    public Optional<Ticket> getTicket(long ticketId) {
        return Optional.ofNullable(tickets.get(ticketId));
    }

    /** RQ03: everyone still waiting or notified in a queue, ordered by position. */
    public List<Ticket> getQueueStatus(long queueId) {
        return tickets.values().stream()
            .filter(t -> t.getQueueId() == queueId)
            .filter(t -> t.getStatus() == Ticket.Status.WAITING || t.getStatus() == Ticket.Status.NOTIFIED)
            .sorted((a, b) -> Integer.compare(a.getPosition(), b.getPosition()))
            .collect(Collectors.toList());
    }

    // ---- Write operations ----

    /** RQ01: reserve the next position in a queue for a user, remotely. */
    public Ticket joinQueue(long queueId, String userName) {
        int nextPosition = tickets.values().stream()
            .filter(t -> t.getQueueId() == queueId)
            .mapToInt(Ticket::getPosition)
            .max()
            .orElse(0) + 1;

        Ticket ticket = new Ticket(ticketIdSeq.getAndIncrement(), queueId, userName, nextPosition);
        tickets.put(ticket.getTicketId(), ticket);
        return ticket;
    }

    /** Allows a user to leave the queue before being served. */
    public void leaveQueue(long ticketId) {
        Ticket ticket = tickets.get(ticketId);
        if (ticket != null) {
            ticket.setStatus(Ticket.Status.CANCELLED);
        }
    }

    /**
     * RQ03: business advances the queue by one. Marks the current front
     * ticket as served, then evaluates RQ02 notification thresholds for
     * whoever is now next.
     */
    public void callNextTicket(long queueId) {
        Queue queue = queues.get(queueId);
        if (queue == null) return;

        getQueueStatus(queueId).stream().findFirst().ifPresent(front -> {
            front.setStatus(Ticket.Status.SERVED);
            queue.advanceServingPosition();
        });

        evaluateNotificationThresholds(queueId, queue.getCurrentServingPosition());
    }

    /** RQ01 (estimated wait): positions ahead multiplied by an assumed average service time. */
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
            } else if (positionsAway <= 0) {
                notificationService.notifyTurn(next);
            }
        });
    }

    private static final int AVERAGE_SERVICE_MINUTES = 5;
}
