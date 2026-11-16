package com.queless.web.servlet;

import com.queless.web.model.Ticket;
import com.queless.web.model.Queue;
import com.queless.web.service.AppInitListener;
import com.queless.web.service.QueueManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Live dashboard. Statistics always come from QueLess tickets, never hardcoded
 * screenshot values. History resets when the in-memory demo process restarts.
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private static final long DEMO_QUEUE_ID = 1L;
    private static final ZoneId ZONE = QueueManager.CLINIC_TIME_ZONE;
    private QueueManager queueManager;

    public static final class DayBar {
        private final String label;
        private final String fullLabel;
        private final int count;
        private final int height;

        public DayBar(String label, String fullLabel, int count, int height) {
            this.label = label;
            this.fullLabel = fullLabel;
            this.count = count;
            this.height = height;
        }
        public String getLabel() { return label; }
        public String getFullLabel() { return fullLabel; }
        public int getCount() { return count; }
        public int getHeight() { return height; }
    }

    @Override
    public void init() {
        queueManager = (QueueManager) getServletContext()
                .getAttribute(AppInitListener.QUEUE_MANAGER_ATTR);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Queue queue = queueManager.getQueue(DEMO_QUEUE_ID).orElse(null);
        if (queue == null) {
            resp.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Demo queue unavailable");
            return;
        }

        var business = queueManager.getAllBusinesses().stream().findFirst().orElse(null);
        List<Ticket> allTickets = queueManager.getAllTickets().stream()
                .filter(t -> t.getQueueId() == DEMO_QUEUE_ID)
                .collect(Collectors.toList());
        List<Ticket> waitingTickets = queueManager.getQueueStatus(DEMO_QUEUE_ID);
        Ticket servingTicket = queueManager.getServingTicket(DEMO_QUEUE_ID).orElse(null);

        LocalDate today = LocalDate.now(ZONE);
        long servedToday = allTickets.stream()
                .filter(t -> t.getServedAt() != null
                        && LocalDate.ofInstant(t.getServedAt(), ZONE).equals(today))
                .count();
        long servedThisMonth = allTickets.stream()
                .filter(t -> t.getServedAt() != null
                        && LocalDate.ofInstant(t.getServedAt(), ZONE).getYear() == today.getYear()
                        && LocalDate.ofInstant(t.getServedAt(), ZONE).getMonth() == today.getMonth())
                .count();

        // Waiting time is measured from joining until the patient was called.
        List<Long> observedWaits = allTickets.stream()
                .filter(t -> t.getCalledAt() != null)
                .map(t -> Math.max(0, Duration.between(t.getJoinedAt(), t.getCalledAt()).toMinutes()))
                .collect(Collectors.toList());
        long avgWaitMinutes = observedWaits.isEmpty() ? 0
                : Math.round(observedWaits.stream().mapToLong(Long::longValue).average().orElse(0));

        Map<Long, Long> elapsedWaitMinutes = new HashMap<>();
        waitingTickets.forEach(t -> elapsedWaitMinutes.put(t.getTicketId(),
                Math.max(0, Duration.between(t.getJoinedAt(), java.time.Instant.now()).toMinutes())));

        List<DayBar> days = new ArrayList<>();
        int maxPeak = 1;
        for (int offset = 6; offset >= 0; offset--) {
            maxPeak = Math.max(maxPeak, queueManager.getDailyPeakWaiting(today.minusDays(offset)));
        }
        for (int offset = 6; offset >= 0; offset--) {
            LocalDate day = today.minusDays(offset);
            int count = queueManager.getDailyPeakWaiting(day);
            int height = count == 0 ? 2 : Math.max(8, (int) Math.round(count * 100.0 / maxPeak));
            days.add(new DayBar(day.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                    day.toString(), count, height));
        }

        // Most common hourly arrival period based on actual recorded joins.
        Map<String, Long> bucketCounts = allTickets.stream()
                .collect(Collectors.groupingBy(t -> {
                    LocalDateTime local = LocalDateTime.ofInstant(t.getJoinedAt(), ZONE);
                    boolean weekend = local.getDayOfWeek().getValue() >= 6;
                    return (weekend ? "Weekends" : "Weekdays") + "|" + local.getHour();
                }, Collectors.counting()));
        String peakPeriod = "";
        if (!bucketCounts.isEmpty()) {
            String best = bucketCounts.entrySet().stream()
                    .max(Comparator.<Map.Entry<String, Long>>comparingLong(Map.Entry::getValue)
                            .thenComparing(Map.Entry::getKey))
                    .orElseThrow().getKey();
            String[] parts = best.split("\\|");
            int hour = Integer.parseInt(parts[1]);
            peakPeriod = parts[0] + ", "
                    + String.format("%02d:00 - %02d:00", hour, hour + 1);
        }

        req.setAttribute("queue", queue);
        req.setAttribute("business", business);
        req.setAttribute("waitingTickets", waitingTickets);
        req.setAttribute("servingTicket", servingTicket);
        req.setAttribute("elapsedWaitMinutes", elapsedWaitMinutes);
        req.setAttribute("activeQueues", queueManager.getQueuesForBusiness(queue.getBusinessId())
                .stream().filter(Queue::isOpen).count());
        req.setAttribute("waitingCount", waitingTickets.size());
        req.setAttribute("avgWaitMinutes", avgWaitMinutes);
        req.setAttribute("servedToday", servedToday);
        req.setAttribute("servedThisMonth", servedThisMonth);
        req.setAttribute("peakPeriod", peakPeriod);
        req.setAttribute("weekBars", days);
        resp.setHeader("Cache-Control", "no-store");
        req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);
    }
}
