package com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.cache;

import java.util.Map;

public record BarcodeRedisValueDto(
	String barcode,
	Long userId,
	Map<String, Integer> categoryPoints
) {
}