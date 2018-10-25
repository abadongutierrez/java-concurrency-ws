package com.jabaddon.nearsoft.javaconcurrencyws_solutions.hotel.price.service.impl;

import com.jabaddon.nearsoft.javaconcurrencyws.hotel.Hotel;
import com.jabaddon.nearsoft.javaconcurrencyws.hotel.price.Pricing;
import com.jabaddon.nearsoft.javaconcurrencyws.hotel.price.service.HotelPricingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class HotelPricingServiceImpl implements HotelPricingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HotelPricingServiceImpl.class);

    @Override
    public void markupPrices(Hotel hotel) {
        LOGGER.debug("Running pricing for hotel {}", hotel.getId());
        hotel.setPricing(new Pricing());
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
