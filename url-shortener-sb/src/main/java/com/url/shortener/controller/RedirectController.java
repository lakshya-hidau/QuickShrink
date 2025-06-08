package com.url.shortener.controller;

import com.url.shortener.models.UrlMapping;
import com.url.shortener.service.UrlMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class RedirectController {

	private final UrlMappingService urlMappingService;

	@Autowired
	public RedirectController(UrlMappingService urlMappingService) {
		this.urlMappingService = urlMappingService;
	}

	@GetMapping("/{shortUrl}")
	public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortUrl) {
		UrlMapping urlMapping = urlMappingService.getOriginalUrl(shortUrl);

		if (urlMapping == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		// Return a redirect response
		return ResponseEntity.status(HttpStatus.FOUND)
				.location(URI.create(urlMapping.getOriginalUrl()))
				.build();
	}
}
