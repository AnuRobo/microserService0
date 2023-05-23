package com.rating.service.services;

import java.util.List;

import com.rating.service.entities.Rating;

public interface RatingService {
	// create
	Rating create(Rating rating);
	
	// get all ratings
	List<Rating> getRatings();
	
	// get all by UserId
	List<Rating> getRatingByUserId(String userID);
	
	// get all by hotel
	List<Rating> getRatingByHotelId(String hotelId);
}
