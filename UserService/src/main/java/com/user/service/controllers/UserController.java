package com.user.service.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.service.entities.User;
import com.user.service.services.UserService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	UserService userService;

	private Logger logger = LoggerFactory.getLogger(UserController.class);
	
	// create .... JSON deserialize into User object
	@PostMapping
	public ResponseEntity<User> createUser(@RequestBody User user){
		User user1 = userService.saveUser(user);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(user1);
	}
	
	// single user get
	@CircuitBreaker(name="ratingHotelBreaker", fallbackMethod="ratingHotelFallback")
	@GetMapping("/{userId}")
	public ResponseEntity<User> getSingleUser(@PathVariable String userId){
		User user = userService.getUser(userId);
		
		return ResponseEntity.ok(user);
	}

	// creating fall back method for circuit breaker
	public ResponseEntity<User> ratingHotelFallback(String userId, Exception ex){

		ex.printStackTrace();
		
		logger.info("Fallback is executed because some service is down: " + ex.getMessage());
		
		
		User user = User.builder()
						.email("dummy@gmail.com")
						.name("dummy")
						.about("User is created dummy because service is down")
						.build();
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
	
	
	// all user get
	@GetMapping
	public ResponseEntity<List<User>> getAllUser(){
		List<User> allUser = userService.getAllUser();
		return ResponseEntity.ok(allUser);
	}
}
