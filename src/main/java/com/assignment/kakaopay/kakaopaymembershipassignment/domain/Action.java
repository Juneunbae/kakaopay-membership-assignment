package com.assignment.kakaopay.kakaopaymembershipassignment.domain;

import lombok.Getter;

@Getter
public enum Action {
	ADD("적립"),
	USE("사용");

	private final String description;

	Action(String description) {
		this.description = description;
	}
}