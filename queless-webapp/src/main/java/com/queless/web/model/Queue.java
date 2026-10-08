package com.queless.web.model;

/**
 * A single queue belonging to a business (e.g. "General Consultation").
 * Corresponds to the "Queue" entity in Figure 3 (ERD).
 * Supports RQ01 (join remotely) and RQ03 (business manages queues).
 */
public class Queue {

    private final long queueId;
    private final long businessId;
    private final String name;
    private volatile int currentServingPosition;
    private volatile boolean open;

    public Queue(long queueId, long businessId, String name) {
        this.queueId = queueId;
        this.businessId = businessId;
        this.name = name;
        this.currentServingPosition = 0;
        this.open = true;
    }

    public long getQueueId() { return queueId; }
    public long getBusinessId() { return businessId; }
    public String getName() { return name; }
    public int getCurrentServingPosition() { return currentServingPosition; }
    public boolean isOpen() { return open; }

    public void advanceServingPosition() { this.currentServingPosition++; }
    public void setOpen(boolean open) { this.open = open; }
}
