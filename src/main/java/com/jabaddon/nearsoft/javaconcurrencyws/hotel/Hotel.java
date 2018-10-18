package com.jabaddon.nearsoft.javaconcurrencyws.hotel;

import com.jabaddon.nearsoft.javaconcurrencyws.hotel.enrichment.Enrichment;
import com.jabaddon.nearsoft.javaconcurrencyws.hotel.price.Pricing;

public class Hotel {
    private Enrichment enrichment;
    private Pricing pricing;
    private final int id;

    public Hotel(int id) {
        this.id = id;
    }

    public void setEnrichment(Enrichment enrichment) {
        this.enrichment = enrichment;
    }

    public Enrichment getEnrichment() {
        return enrichment;
    }

    public void setPricing(Pricing pricing) {
        this.pricing = pricing;
    }

    public Pricing getPricing() {
        return pricing;
    }

    public int getId() {
        return id;
    }
}
