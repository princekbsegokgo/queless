package com.queless.web.model;

import java.time.Instant;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * An individual queue reservation, with timestamps for truthful dashboard metrics.
 * Data is currently in-memory and resets when the application restarts.
 */
public class Ticket {
    public enum Status { WAITING, NOTIFIED, SERVING, SERVED, CANCELLED }

    private static final SecureRandom RANDOM = new SecureRandom();
    private final String accessToken;
    private final long ticketId;
    private final long queueId;
    private final String userName;
    private final int position;
    private final Instant joinedAt;
    private volatile Instant calledAt;
    private volatile Instant servedAt;
    private volatile Status status;
    private volatile String lastNotificationMessage;

    public Ticket(long ticketId, long queueId, String userName, int position) {
        byte[] tokenBytes = new byte[24];
        RANDOM.nextBytes(tokenBytes);
        this.accessToken = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        this.ticketId = ticketId;
        this.queueId = queueId;
        this.userName = userName;
        this.position = position;
        this.joinedAt = Instant.now();
        this.status = Status.WAITING;
    }

    public String getAccessToken() { return accessToken; }
    public long getTicketId() { return ticketId; }
    public long getQueueId() { return queueId; }
    public String getUserName() { return userName; }
    public int getPosition() { return position; }
    public Instant getJoinedAt() { return joinedAt; }
    public Instant getCalledAt() { return calledAt; }
    public Instant getServedAt() { return servedAt; }
    public Status getStatus() { return status; }
    public String getLastNotificationMessage() { return lastNotificationMessage; }

    public void setStatus(Status nextStatus) {
        if (nextStatus == Status.SERVING && calledAt == null) {
            calledAt = Instant.now();
        }
        if (nextStatus == Status.SERVED && servedAt == null) {
            servedAt = Instant.now();
        }
        status = nextStatus;
    }

    public void setLastNotificationMessage(String message) {
        lastNotificationMessage = message;
    }
}
