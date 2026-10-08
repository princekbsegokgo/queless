package com.queless.web.service;

import com.queless.web.model.Ticket;

/**
 * Handles turn-based notifications (RQ02). In this web MVP, "sending" a
 * notification means writing a message onto the ticket, which status.jsp
 * then displays on refresh/poll. A production version would replace
 * sendPush() with a real push channel (e.g. WebSocket/SSE or Firebase)
 * for true real-time delivery without a page refresh.
 */
public class NotificationService {

    public static final int NOTIFY_FAR_THRESHOLD = 10;
    public static final int NOTIFY_NEAR_THRESHOLD = 2;

    /** Called when a waiting ticket crosses a threshold (10 or 2 away). */
    public void sendPush(Ticket ticket, int positionsAway) {
        ticket.setStatus(Ticket.Status.NOTIFIED);
        ticket.setLastNotificationMessage(
            "Your turn is approaching: " + positionsAway + " people ahead of you."
        );
    }

    /** Called when it becomes exactly the ticket's turn. */
    public void notifyTurn(Ticket ticket) {
        ticket.setLastNotificationMessage(
            "It is now your turn. Please proceed to the counter."
        );
    }

    /**
     * RQ06 (minor requirement, not yet implemented): fall back to SMS when
     * push delivery is unavailable. Left as a stub so the class matches the
     * architecture described in Section 4.1 of the project report; wiring
     * this to a real SMS gateway is future work.
     */
    public void sendSMSFallback(Ticket ticket, String message) {
        throw new UnsupportedOperationException(
            "SMS fallback (RQ06) is planned for a later phase and is not implemented in this MVP."
        );
    }
}
