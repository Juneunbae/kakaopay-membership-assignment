package com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response;

public record IssueBarcodeResponseServiceDto(
	Long id,
	String userId,
	String username,
	String barcode
) {
}