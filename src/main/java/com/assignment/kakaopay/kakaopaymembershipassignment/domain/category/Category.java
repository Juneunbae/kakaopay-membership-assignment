package com.assignment.kakaopay.kakaopaymembershipassignment.domain.category;

import lombok.Getter;

@Getter
public enum Category {
	FOOD("식품"),
	COSMETIC("화장품"),
	RESTAURANT("식당");

	private final String description;

	Category(String description) {
		this.description = description;
	}
}