package com.url.shortener.service;

import com.url.shortener.dtos.ClickEventDTO;
import com.url.shortener.models.ClickEvent;
import com.url.shortener.models.UrlMapping;
import com.url.shortener.models.User;
import com.url.shortener.repository.ClickEventRepository;
import com.url.shortener.repository.UrlMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class UrlMappingService {

	private final UrlMappingRepository urlMappingRepository;
	private final ClickEventRepository clickEventRepository;

	@Autowired
	public UrlMappingService(UrlMappingRepository urlMappingRepository, ClickEventRepository clickEventRepository) {
		this.urlMappingRepository = urlMappingRepository;
		this.clickEventRepository = clickEventRepository;
	}

	/**
	 * Shortens a given URL and associates it with a user.
	 *
	 * @param originalUrl The original URL to be shortened.
	 * @param user        The user who is shortening the URL.
	 * @return The saved UrlMapping entity.
	 */
	public UrlMapping shortenUrl(String originalUrl, User user) {
		String shortUrl = generateShortUrl();
		UrlMapping urlMapping = new UrlMapping();
		urlMapping.setOriginalUrl(originalUrl);
		urlMapping.setShortUrl(shortUrl);
		urlMapping.setUser(user);
		urlMapping.setCreatedDate(LocalDateTime.now());
		return urlMappingRepository.save(urlMapping);
	}

	/**
	 * Generates a random short URL.
	 *
	 * @return A randomly generated short URL.
	 */
	private String generateShortUrl() {
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuilder shortUrl = new StringBuilder(8);

		for (int i = 0; i < 8; i++) {
			shortUrl.append(characters.charAt(random.nextInt(characters.length())));
		}
		return shortUrl.toString();
	}

	/**
	 * Retrieves all URLs associated with a specific user.
	 *
	 * @param user The user whose URLs are to be retrieved.
	 * @return A list of UrlMapping entities.
	 */
	public List<UrlMapping> getUrlsByUser(User user) {
		return urlMappingRepository.findByUser(user).stream().map(urlMapping -> {
			UrlMapping urlMappingResponse = new UrlMapping();
			urlMappingResponse.setId(urlMapping.getId());
			urlMappingResponse.setOriginalUrl(urlMapping.getOriginalUrl());
			urlMappingResponse.setShortUrl(urlMapping.getShortUrl());
			urlMappingResponse.setClickCount(urlMapping.getClickCount());
			urlMappingResponse.setCreatedDate(urlMapping.getCreatedDate());
			urlMappingResponse.setUser(urlMapping.getUser());
			return urlMappingResponse;
		}).toList();
	}

	/**
	 * Retrieves click events for a specific short URL within a date range.
	 *
	 * @param shortUrl      The short URL to filter click events.
	 * @param startDateTime The start date and time of the range.
	 * @param endDateTime   The end date and time of the range.
	 * @return A list of ClickEventDTO objects.
	 */
	public List<ClickEventDTO> getClickEventByDate(String shortUrl, LocalDate startDateTime, LocalDateTime endDateTime) {
		UrlMapping urlMapping = urlMappingRepository.findByShortUrl(shortUrl);
		if (urlMapping != null) {
			return clickEventRepository.findByUrlMappingAndClickDateBetween(urlMapping, startDateTime.atStartOfDay(), endDateTime).stream()
					.collect(Collectors.groupingBy(click -> click.getClickDate().toLocalDate(), Collectors.counting()))
					.entrySet().stream().map(entry -> {
						ClickEventDTO clickEventDTO = new ClickEventDTO();
						clickEventDTO.setClickDate(entry.getKey());
						clickEventDTO.setClickCount(entry.getValue().intValue()); // Convert Long to int
						return clickEventDTO;
					}).toList();
		}
		return null;
	}


	public Map<LocalDate, Long> getTotalClicksByUserAndDate(User user, LocalDate start, LocalDate end) {
		List<UrlMapping> urlMappings = urlMappingRepository.findByUser(user);
		List<ClickEvent> clickEvents = clickEventRepository.findByUrlMappingInAndClickDateBetween(urlMappings, start.atStartOfDay(), end.atStartOfDay());
		return clickEvents.stream()
				.collect(Collectors.groupingBy(click -> click.getClickDate().toLocalDate(), Collectors.counting()));
	}

	public UrlMapping getOriginalUrl(String shortUrl) {
		UrlMapping urlMapping = urlMappingRepository.findByShortUrl(shortUrl);
		if (urlMapping != null) {
			// Increment click count
			urlMapping.setClickCount(urlMapping.getClickCount() + 1);
			urlMappingRepository.save(urlMapping);

			// Record Click Event
			ClickEvent clickEvent = new ClickEvent();
			clickEvent.setClickDate(LocalDateTime.now());
			clickEvent.setUrlMapping(urlMapping);
			clickEventRepository.save(clickEvent);
		}
		return urlMapping; // Return null if short URL is not found
	}
}