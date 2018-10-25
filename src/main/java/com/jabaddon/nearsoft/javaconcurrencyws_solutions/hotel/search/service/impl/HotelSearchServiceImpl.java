package com.jabaddon.nearsoft.javaconcurrencyws_solutions.hotel.search.service.impl;

import com.jabaddon.nearsoft.javaconcurrencyws.hotel.Hotel;
import com.jabaddon.nearsoft.javaconcurrencyws.hotel.search.HotelSearch;
import com.jabaddon.nearsoft.javaconcurrencyws.hotel.search.service.HotelSearchService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class HotelSearchServiceImpl implements HotelSearchService {
    @Override
    public List<Hotel> search(HotelSearch hotelSearch) {
        List<Hotel> hotels = new ArrayList<>();
        IntStream.range(1, 200 + 1).forEach(num -> {
            hotels.add(createHotel(num));
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // ignore
        }
        return hotels;
    }

    private Hotel createHotel(int num) {
        return new Hotel(num);
    }
}
