package com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.cache;

public record BarcodeRedisValueDto(
	String barcode,
	Long userId
) {
}