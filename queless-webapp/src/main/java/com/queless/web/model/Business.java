package com.queless.web.model;

/**
 * A service provider subscribed to QueLess (RQ03).
 * Corresponds to the "Business" entity in Figure 3 (ERD).
 */
public class Business {

    public enum Tier { STARTER, PRO, ENTERPRISE }

    private final long businessId;
    private final String name;
    private final String category;
    private final Tier tier;

    public Business(long businessId, String name, String category, Tier tier) {
        this.businessId = businessId;
        this.name = name;
        this.category = category;
        this.tier = tier;
    }

    public long getBusinessId() { return businessId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public Tier getTier() { return tier; }
}
