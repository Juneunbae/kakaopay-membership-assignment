package com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.response;

public record IssueBarcodeResponseDto(
	Long id,
	String userId,
	String username,
	String barcode
) {
}