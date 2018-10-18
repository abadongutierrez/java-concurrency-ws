package com.jabaddon.nearsoft.javaconcurrencyws.hotel.service.impl;

import com.jabaddon.nearsoft.javaconcurrencyws.hotel.Hotel;
import com.jabaddon.nearsoft.javaconcurrencyws.hotel.enrichment.service.HotelEnrichmentService;
import com.jabaddon.nearsoft.javaconcurrencyws.hotel.price.service.HotelPricingService;
import com.jabaddon.nearsoft.javaconcurrencyws.hotel.search.HotelSearch;
import com.jabaddon.nearsoft.javaconcurrencyws.hotel.search.service.HotelSearchService;
import com.jabaddon.nearsoft.javaconcurrencyws.hotel.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("hotelServiceMultipleThreads")
public class HotelServiceWithMultipleThreadsImpl implements HotelService {
    private final HotelSearchService hotelSearchService;
    private final HotelEnrichmentService hotelEnrichmentService;
    private final HotelPricingService hotelPricingService;

    @Autowired
    public HotelServiceWithMultipleThreadsImpl(HotelSearchService hotelSearchService, HotelEnrichmentService hotelEnrichmentService,
                                       HotelPricingService hotelPricingService) {
        this.hotelSearchService = hotelSearchService;
        this.hotelEnrichmentService = hotelEnrichmentService;
        this.hotelPricingService = hotelPricingService;
    }

    @Override
    public List<Hotel> search(HotelSearch hotelSearch) {
        List<Hotel> hotels = hotelSearchService.search(hotelSearch);
        Thread thread1 = new Thread(new EnrichmentRunnable(hotels));
        Thread thread2 = new Thread(new PricingRunnable(hotels));
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

    private class PricingRunnable extends SubmitThreads {
        private final List<Hotel> hotels;

        PricingRunnable(List<Hotel> hotels) {
            this.hotels = hotels;
        }

        @Override
        public void run() {
            hotels.forEach(hotel -> {
                summitThread(() -> hotelPricingService.markupPrices(hotel));
            });
            joinAllThreads();
        }

    }

    private class EnrichmentRunnable extends SubmitThreads implements Runnable {
        private final List<Hotel> hotels;

        EnrichmentRunnable(List<Hotel> hotels) {
            this.hotels = hotels;
        }

        @Override
        public void run() {
            hotels.forEach(hotel -> {
                summitThread(() -> hotelEnrichmentService.enrich(hotel));
            });
            joinAllThreads();
        }
    }

    private abstract class SubmitThreads implements Runnable {
        private final List<Thread> threads = new ArrayList<>();

        void joinAllThreads() {
            threads.forEach(t -> {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        void summitThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.start();
            threads.add(thread);
        }

        @Override
        public abstract void run();
    }
}