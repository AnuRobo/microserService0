package com.user.service.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.user.service.entities.Hotel;
import com.user.service.entities.Rating;
import com.user.service.entities.User;
import com.user.service.exceptions.ResourceNotFoundException;
import com.user.service.external.services.HotelService;
import com.user.service.repositories.UserRepository;
import com.user.service.services.UserService;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private HotelService hotelService;
	
	private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Override
	public User saveUser(User user) {
		// generate unique userid
		String randomUserId = UUID.randomUUID().toString();
		user.setUserId(randomUserId);
		
		return userRepository.save(user);
	}

	@Override
	public List<User> getAllUser() {
		List<User> users = userRepository.findAll();
		
		List<User> userList = users.stream().map(user -> {
			Rating[] ratingOfUsers = restTemplate.getForObject("http://RATING-SERVICE/ratings/users/"+user.getUserId(), Rating[].class);
			List<Rating> ratings = Arrays.asList(ratingOfUsers);
			
			List<Rating> ratingList =  ratings.stream().map(rating->{
				ResponseEntity<Hotel> forEntity = restTemplate.getForEntity("http://HOTEL-SERVICE/hotels/"+rating.getHotelId(), Hotel.class);
				Hotel hotel = forEntity.getBody();
				
				// set the hotel to rating
				rating.setHotel(hotel);
				
				// return rating
				return rating;
			}).collect(Collectors.toList());
			user.setRating(ratingList);			
			return user;
		}).collect(Collectors.toList());
		return userList;
	}

	@Override
	public User getUser(String userId) {
		// get user from database
		User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User with given id: "+userId+" is not found on server !!"));
		
		// fetch rating of the above user from RATING SERVICE
		// http://localhost:8083/ratings/users/b97f321d-033d-41ed-82cc-68b047e77244
		Rating[] ratingsOfUser = restTemplate.getForObject("http://RATING-SERVICE/ratings/users/"+user.getUserId(), Rating[].class);
		logger.info("{}",ratingsOfUser);
		
		List<Rating> ratings = Arrays.asList(ratingsOfUser);
		
		List<Rating> ratingList =  ratings.stream().map(rating -> {
			// api call to hotel
			// ResponseEntity<Hotel> forEntity = restTemplate.getForEntity("http://HOTEL-SERVICE/hotels/"+rating.getHotelId(), Hotel.class);
//			Hotel hotel = forEntity.getBody();
			
			Hotel hotel = hotelService.getHotel(rating.getHotelId());
			
			// set the hotel to rating
			rating.setHotel(hotel);
			
			// return rating
			return rating;
		}).collect(Collectors.toList());
		
		user.setRating(ratingList);
		return user;
	}

}
