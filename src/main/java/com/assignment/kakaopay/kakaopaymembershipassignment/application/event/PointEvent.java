package com.assignment.kakaopay.kakaopaymembershipassignment.application.event;

import com.assignment.kakaopay.kakaopaymembershipassignment.domain.point.Point;

public record PointEvent(
	Point point,
	Integer pointAmount,
	Long storeId
) {
}