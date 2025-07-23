package com.assignment.kakaopay.kakaopaymembershipassignment.application.event;

import com.assignment.kakaopay.kakaopaymembershipassignment.domain.point.Point;

public record UsePointEvent(
	Point point,
	Integer pointAmount,
	Long storeId
) {
}