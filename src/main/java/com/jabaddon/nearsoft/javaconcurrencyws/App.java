package com.jabaddon.nearsoft.javaconcurrencyws;

import com.jabaddon.nearsoft.javaconcurrencyws.hotel.Hotel;
import com.jabaddon.nearsoft.javaconcurrencyws.hotel.search.HotelSearch;
import com.jabaddon.nearsoft.javaconcurrencyws.hotel.service.HotelService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ComponentScan(basePackages = "com.jabaddon.nearsoft.javaconcurrencyws.hotel")
public class App {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(App.class);
        String[] serviceNames = new String[]{"hotelService", "hotelServiceThreads", "hotelServiceMultipleThreads"};
        HotelService hotelService = applicationContext.getBean(serviceNames[2], HotelService.class);
        long startTime = System.currentTimeMillis();
        List<Hotel> search = hotelService.search(new HotelSearch());
        long endTime = System.currentTimeMillis();
        System.out.println("Execution time: " + ((endTime - startTime) / 1000.00) + " secs.");

        verifyHotels(search);
    }

    private static void verifyHotels(List<Hotel> hotels) {
        hotels.forEach(hotel -> {
            if (hotel.getEnrichment() == null || hotel.getPricing() == null) {
                throw new RuntimeException("Enrichment and pricing failed.");
            }
        });
    }
}
