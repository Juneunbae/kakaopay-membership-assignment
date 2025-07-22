package com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.member;

public record IssueBarcodeResponseServiceDto(
	Long id,
	String userId,
	String username,
	String barcode
) {
}