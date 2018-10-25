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
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service("hotelServiceCompletableFutureService")
public class HoteServiceWithCompletableFuturesServiceImpl implements HotelService {
    private final HotelSearchService hotelSearchService;
    private final HotelEnrichmentService hotelEnrichmentService;
    private final HotelPricingService hotelPricingService;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @Autowired
    public HoteServiceWithCompletableFuturesServiceImpl(HotelSearchService hotelSearchService, HotelEnrichmentService hotelEnrichmentService,
                            HotelPricingService hotelPricingService) {
        this.hotelSearchService = hotelSearchService;
        this.hotelEnrichmentService = hotelEnrichmentService;
        this.hotelPricingService = hotelPricingService;
    }

    @Override
    public List<Hotel> search(HotelSearch hotelSearch) {
        CompletableFuture<List<Hotel>> hotelsFuture =
                CompletableFuture.supplyAsync(() -> hotelSearchService.search(hotelSearch), executorService);

        CompletableFuture<Void> eachHotelFuture = hotelsFuture.thenAccept(
                hotels -> {
                    CompletableFuture[] completableFutures = hotels.stream().map(hotel -> List.of(
                            CompletableFuture.runAsync(() -> hotelEnrichmentService.enrich(hotel), executorService),
                            CompletableFuture.runAsync(() -> hotelPricingService.markupPrices(hotel), executorService)))
                            .flatMap(Collection::stream).toArray(CompletableFuture[]::new);
                    CompletableFuture.allOf(completableFutures).join();
                });
        eachHotelFuture.join();

        return hotelsFuture.join();
    }

    @PreDestroy
    public void onDestroy() {
        executorService.shutdown();
    }
}
