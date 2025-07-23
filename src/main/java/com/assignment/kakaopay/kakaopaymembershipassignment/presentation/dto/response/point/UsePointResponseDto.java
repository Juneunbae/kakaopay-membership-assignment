package com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.response.point;

public record UsePointResponseDto(
	Long storeId,
	String barcode,
	Integer usePoint,
	Integer remainPoint
) {
}