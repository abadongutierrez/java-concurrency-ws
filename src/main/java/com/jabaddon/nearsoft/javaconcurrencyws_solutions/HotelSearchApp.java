package com.jabaddon.nearsoft.javaconcurrencyws_solutions;

import com.jabaddon.nearsoft.javaconcurrencyws.hotel.Hotel;
import com.jabaddon.nearsoft.javaconcurrencyws.hotel.search.HotelSearch;
import com.jabaddon.nearsoft.javaconcurrencyws.hotel.service.HotelService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ComponentScan(basePackages = "com.jabaddon.nearsoft.javaconcurrencyws.hotel")
public class HotelSearchApp {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(HotelSearchApp.class);
        String[] serviceNames = new String[]{
                "hotelService", // 0
                "hotelServiceThreads", // 1
                "hotelServiceMultipleThreads", // 2
                "hotelServiceExecutorService", // 3
                "hotelServiceExecutorServiceV2", // 4
                "hotelServiceExecutorServiceV3", // 5
                "hotelServiceCompletableFutureService" // 6
        };
        HotelService hotelService = applicationContext.getBean(serviceNames[6], HotelService.class);
        long startTime = System.currentTimeMillis();
        List<Hotel> search = hotelService.search(new HotelSearch());
        long endTime = System.currentTimeMillis();
        System.out.println("Execution time: " + ((endTime - startTime) / 1000.00) + " secs.");

        verifyHotels(search);

        applicationContext.close();
    }

    private static void verifyHotels(List<Hotel> hotels) {
        hotels.forEach(hotel -> {
            if (hotel.getEnrichment() == null || hotel.getPricing() == null) {
                throw new RuntimeException("Enrichment and pricing failed.");
            }
        });
    }
}
