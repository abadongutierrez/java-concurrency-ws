package com.jabaddon.nearsoft.javaconcurrencyws_solutions.hotel.search.service;

import com.jabaddon.nearsoft.javaconcurrencyws.hotel.Hotel;
import com.jabaddon.nearsoft.javaconcurrencyws.hotel.search.HotelSearch;

import java.util.List;

public interface HotelSearchService {
    List<Hotel> search(HotelSearch hotelSearch);
}
