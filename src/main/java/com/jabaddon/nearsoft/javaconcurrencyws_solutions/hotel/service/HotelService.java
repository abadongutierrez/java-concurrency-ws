package com.jabaddon.nearsoft.javaconcurrencyws_solutions.hotel.service;

import com.jabaddon.nearsoft.javaconcurrencyws.hotel.Hotel;
import com.jabaddon.nearsoft.javaconcurrencyws.hotel.search.HotelSearch;

import java.util.List;

public interface HotelService {
    List<Hotel> search(HotelSearch hotelSearch);
}
