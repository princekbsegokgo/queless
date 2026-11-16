package com.queless.web.model;

/** One queue operated by a clinic or other business. */
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
        currentServingPosition = 0;
        open = true;
    }

    public long getQueueId() { return queueId; }
    public long getBusinessId() { return businessId; }
    public String getName() { return name; }
    public int getCurrentServingPosition() { return currentServingPosition; }
    public boolean isOpen() { return open; }

    public void setCurrentServingPosition(int position) {
        currentServingPosition = position;
    }

    public void advanceServingPosition() {
        currentServingPosition++;
    }

    public void setOpen(boolean open) { this.open = open; }
}
