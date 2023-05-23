package com.hotel.service.services;

import java.util.List;

import com.hotel.service.entity.Hotel;

public interface HotelServices {
	// create
	Hotel create(Hotel hotel);
	
	// getall
	List<Hotel> getAll();
	
	// get single
	Hotel getHotel(String id);
}
