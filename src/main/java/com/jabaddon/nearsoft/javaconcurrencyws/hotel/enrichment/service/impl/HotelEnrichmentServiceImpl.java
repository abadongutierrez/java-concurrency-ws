package com.jabaddon.nearsoft.javaconcurrencyws.hotel.enrichment.service.impl;

import com.jabaddon.nearsoft.javaconcurrencyws.hotel.Hotel;
import com.jabaddon.nearsoft.javaconcurrencyws.hotel.enrichment.Enrichment;
import com.jabaddon.nearsoft.javaconcurrencyws.hotel.enrichment.service.HotelEnrichmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HotelEnrichmentServiceImpl implements HotelEnrichmentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HotelEnrichmentServiceImpl.class);

    @Override
    public void enrich(Hotel hotel) {
        LOGGER.debug("Running enrichment for hotel {}", hotel.getId());
        hotel.setEnrichment(new Enrichment());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
