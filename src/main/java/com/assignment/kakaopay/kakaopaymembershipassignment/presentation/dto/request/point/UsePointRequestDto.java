package com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.request.point;

public record UsePointRequestDto(
	Long storeId,
	String barcode,
	Integer usePoint
) {
}