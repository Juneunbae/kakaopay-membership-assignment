package com.assignment.kakaopay.kakaopaymembershipassignment.domain.point;

import lombok.Getter;

@Getter
public enum Action {
	USE("사용"),
	REWARD("적립");

	private final String description;

	Action(String description) {
		this.description = description;
	}
}