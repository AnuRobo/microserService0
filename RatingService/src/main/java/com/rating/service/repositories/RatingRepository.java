package com.rating.service.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.*;
import com.rating.service.entities.Rating;

public interface RatingRepository extends MongoRepository<Rating, String> {
	// custom finder method
	List<Rating> findByUserId(String userId);
	List<Rating> findByHotelId(String hotelId);
}
