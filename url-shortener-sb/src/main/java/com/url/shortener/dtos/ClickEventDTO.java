package com.url.shortener.dtos;

import java.time.LocalDate;

public class ClickEventDTO {
	private LocalDate clickDate;
	private int clickCount;

	// Getters and Setters
	public LocalDate getClickDate() {
		return clickDate;
	}

	public void setClickDate(LocalDate clickDate) {
		this.clickDate = clickDate;
	}

	public int getClickCount() {
		return clickCount;
	}

	public void setClickCount(int clickCount) {
		this.clickCount = clickCount;
	}
}