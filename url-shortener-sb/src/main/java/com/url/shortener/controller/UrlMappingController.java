package com.url.shortener.controller;


import com.url.shortener.dtos.ClickEventDTO;
import com.url.shortener.dtos.UrlMappingDTO;
import com.url.shortener.models.UrlMapping;
import com.url.shortener.models.User;
import com.url.shortener.service.UrlMappingService;
import com.url.shortener.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/urls")
public class UrlMappingController {

	private final UserService userService;
	private final UrlMappingService urlMappingService;

	@Autowired
	public UrlMappingController(UserService userService, UrlMappingService urlMappingService) {
		this.userService = userService;
		this.urlMappingService = urlMappingService;
	}

	@PostMapping("/shorten")
	public UrlMappingDTO shortenUrl(@RequestBody Map<String, String> request, Principal principal) {
		String originalUrl = request.get("originalUrl");
		String username = principal.getName(); // Get the authenticated user's username

		// Find the user by username
		User user = userService.findByUsername(username);

		// Generate the short URL using the UrlMappingService
		UrlMapping urlMapping = urlMappingService.shortenUrl(originalUrl, user);

		// Map the UrlMapping entity to UrlMappingResponse DTO
		UrlMappingDTO response = new UrlMappingDTO();
		response.setId(urlMapping.getId());
		response.setLongUrl(urlMapping.getOriginalUrl());
		response.setShortUrl(urlMapping.getShortUrl());
		response.setClickCount(urlMapping.getClickCount());
		response.setCreatedDate(urlMapping.getCreatedDate());
		response.setUsername(user.getUsername());

		// Return the response
		return response;
	}

	@GetMapping("/myurls")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<List<UrlMapping>> getUrlsByUser(Principal principal) {
		String username = principal.getName(); // Get the authenticated user's username
		// Find the user by username
		User user = userService.findByUsername(username);
		// Get the list of UrlMappings for the user
		List<UrlMapping> urlMappings = urlMappingService.getUrlsByUser(user);
		// Return the response
		return ResponseEntity.ok(urlMappings);
	}

	@GetMapping("/analytics/{shortUrl}")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<List<ClickEventDTO>> getUrlAnalytics(@PathVariable String shortUrl,
															   @RequestParam("startDate") String startDate,
															   @RequestParam("endDate") String endDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
		LocalDateTime startDateTime = LocalDateTime.parse(startDate, formatter);
		LocalDateTime endDateTime = LocalDateTime.parse(endDate, formatter);
		List<ClickEventDTO> clickEventDTOS = urlMappingService.getClickEventByDate(shortUrl, LocalDate.from(startDateTime), endDateTime);
		return ResponseEntity.ok(clickEventDTOS);
	}

	@GetMapping("/totalClicks")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<Map<LocalDate, Long>> getTotalClicksByDate(Principal principal,
																	 @RequestParam("startDate") String startDate,
																	 @RequestParam("endDate") String endDate){
		DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
		User user = userService.findByUsername(principal.getName());
		LocalDate start = LocalDate.parse(startDate, formatter);
		LocalDate end = LocalDate.parse(endDate, formatter);
		Map<LocalDate, Long> totalClicks = urlMappingService.getTotalClicksByUserAndDate(user, start, end);
		return ResponseEntity.ok(totalClicks);
	}
}