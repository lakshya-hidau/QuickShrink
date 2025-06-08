// src/main/java/com/url/shortener/controller/AuthController.java
package com.url.shortener.controller;

import com.url.shortener.dtos.LoginRequest;
import com.url.shortener.dtos.RegisterRequest;
import com.url.shortener.models.User;
import com.url.shortener.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "${FRONTEND_URL}") // Allow frontend
public class AuthController {

	private final UserService userService;

	public AuthController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
		return ResponseEntity.ok(userService.authenticateUser(loginRequest));
	}

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
		if (userService.existsByUsername(registerRequest.getUsername())) {
			return ResponseEntity.badRequest().body("Error: Username is already taken!");
		}
		if (userService.existsByEmail(registerRequest.getEmail())) {
			return ResponseEntity.badRequest().body("Error: Email is already in use!");
		}

		User user = new User();
		user.setUsername(registerRequest.getUsername());
		user.setPassword(registerRequest.getPassword());
		user.setEmail(registerRequest.getEmail());
		user.setRole("ROLE_USER");
		userService.registerUser(user);
		return ResponseEntity.ok("User registered successfully");
	}

	@GetMapping("/health-check")
	public String healthCheck() {
		return "All Ok!";
	}
}