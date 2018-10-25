package com.jabaddon.nearsoft.javaconcurrencyws_solutions.hotel.service.impl;

import com.jabaddon.nearsoft.javaconcurrencyws.hotel.Hotel;
import com.jabaddon.nearsoft.javaconcurrencyws.hotel.enrichment.service.HotelEnrichmentService;
import com.jabaddon.nearsoft.javaconcurrencyws.hotel.price.service.HotelPricingService;
import com.jabaddon.nearsoft.javaconcurrencyws.hotel.search.HotelSearch;
import com.jabaddon.nearsoft.javaconcurrencyws.hotel.search.service.HotelSearchService;
import com.jabaddon.nearsoft.javaconcurrencyws.hotel.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service("hotelServiceExecutorServiceV2")
public class HotelServiceWithExecutorServiceV2Impl implements HotelService {
    private final HotelSearchService hotelSearchService;
    private final HotelEnrichmentService hotelEnrichmentService;
    private final HotelPricingService hotelPricingService;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @Autowired
    public HotelServiceWithExecutorServiceV2Impl(HotelSearchService hotelSearchService, HotelEnrichmentService hotelEnrichmentService,
                                               HotelPricingService hotelPricingService) {
        this.hotelSearchService = hotelSearchService;
        this.hotelEnrichmentService = hotelEnrichmentService;
        this.hotelPricingService = hotelPricingService;
    }

    @Override
    public List<Hotel> search(HotelSearch hotelSearch) {
        List<Hotel> hotels = hotelSearchService.search(hotelSearch);
        CountDownLatch countDownLatch = new CountDownLatch(hotels.size());
        // a thread to run enrichment and markup prices and using countdownlatch
        hotels.stream().map(hotel -> (Runnable) () -> {
            hotelEnrichmentService.enrich(hotel);
            hotelPricingService.markupPrices(hotel);
            countDownLatch.countDown();
        }).forEach(executorService::submit);

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return hotels;
    }

    @PreDestroy
    public void onDestroy() {
        executorService.shutdown();
    }
}
