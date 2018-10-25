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
import java.util.concurrent.TimeUnit;

@Service("hotelServiceExecutorServiceV3")
public class HotelServiceWithExecutorServiceV3Impl implements HotelService {
    private final HotelSearchService hotelSearchService;
    private final HotelEnrichmentService hotelEnrichmentService;
    private final HotelPricingService hotelPricingService;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @Autowired
    public HotelServiceWithExecutorServiceV3Impl(HotelSearchService hotelSearchService, HotelEnrichmentService hotelEnrichmentService,
                                                 HotelPricingService hotelPricingService) {
        this.hotelSearchService = hotelSearchService;
        this.hotelEnrichmentService = hotelEnrichmentService;
        this.hotelPricingService = hotelPricingService;
    }

    @Override
    public List<Hotel> search(HotelSearch hotelSearch) {
        List<Hotel> hotels = hotelSearchService.search(hotelSearch);
        CountDownLatch countDownLatch = new CountDownLatch((hotels.size()*2) + 2);

        executorService.submit(() -> {
            hotels.stream().map(hotel -> (Runnable) () -> {
                hotelEnrichmentService.enrich(hotel);
                countDownLatch.countDown();
            }).forEach(executorService::submit);
            countDownLatch.countDown();
        });
        executorService.submit(() -> {
            hotels.stream().map(hotel -> (Runnable) () -> {
                hotelPricingService.markupPrices(hotel);
                countDownLatch.countDown();
            }).forEach(executorService::submit);
            countDownLatch.countDown();
        });

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
