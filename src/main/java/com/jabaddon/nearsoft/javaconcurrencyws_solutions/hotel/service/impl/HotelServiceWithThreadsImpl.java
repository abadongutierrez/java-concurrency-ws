package com.jabaddon.nearsoft.javaconcurrencyws_solutions.hotel.service.impl;

import com.jabaddon.nearsoft.javaconcurrencyws.hotel.Hotel;
import com.jabaddon.nearsoft.javaconcurrencyws.hotel.enrichment.service.HotelEnrichmentService;
import com.jabaddon.nearsoft.javaconcurrencyws.hotel.price.service.HotelPricingService;
import com.jabaddon.nearsoft.javaconcurrencyws.hotel.search.HotelSearch;
import com.jabaddon.nearsoft.javaconcurrencyws.hotel.search.service.HotelSearchService;
import com.jabaddon.nearsoft.javaconcurrencyws.hotel.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("hotelServiceThreads")
public class HotelServiceWithThreadsImpl implements HotelService {
    private final HotelSearchService hotelSearchService;
    private final HotelEnrichmentService hotelEnrichmentService;
    private final HotelPricingService hotelPricingService;

    @Autowired
    public HotelServiceWithThreadsImpl(HotelSearchService hotelSearchService, HotelEnrichmentService hotelEnrichmentService,
                            HotelPricingService hotelPricingService) {
        this.hotelSearchService = hotelSearchService;
        this.hotelEnrichmentService = hotelEnrichmentService;
        this.hotelPricingService = hotelPricingService;
    }

    @Override
    public List<Hotel> search(HotelSearch hotelSearch) {
        List<Hotel> hotels = hotelSearchService.search(hotelSearch);
        Thread thread1 = new Thread(() -> hotels.forEach(hotelEnrichmentService::enrich));
        Thread thread2 = new Thread(() -> hotels.forEach(hotelPricingService::markupPrices));
        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return hotels;
    }
}
