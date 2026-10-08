package com.queless.web.model;

/**
 * A user's reserved position in a queue.
 * Corresponds to the "Ticket" entity in Figure 3 (ERD).
 * Supports RQ01 (join remotely) and RQ02 (turn-based notification).
 */
public class Ticket {

    public enum Status { WAITING, NOTIFIED, SERVED, CANCELLED }

    private final long ticketId;
    private final long queueId;
    private final String userName;
    private final int position;
    private volatile Status status;
    private volatile String lastNotificationMessage; // supports RQ02 display in status.jsp

    public Ticket(long ticketId, long queueId, String userName, int position) {
        this.ticketId = ticketId;
        this.queueId = queueId;
        this.userName = userName;
        this.position = position;
        this.status = Status.WAITING;
        this.lastNotificationMessage = null;
    }

    public long getTicketId() { return ticketId; }
    public long getQueueId() { return queueId; }
    public String getUserName() { return userName; }
    public int getPosition() { return position; }
    public Status getStatus() { return status; }
    public String getLastNotificationMessage() { return lastNotificationMessage; }

    public void setStatus(Status status) { this.status = status; }
    public void setLastNotificationMessage(String message) { this.lastNotificationMessage = message; }
}
