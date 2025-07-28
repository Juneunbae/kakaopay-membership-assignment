package com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.member;

public record BarcodeRedisValueDto(
	String barcode,
	Long userId,
	Integer point
) {
}