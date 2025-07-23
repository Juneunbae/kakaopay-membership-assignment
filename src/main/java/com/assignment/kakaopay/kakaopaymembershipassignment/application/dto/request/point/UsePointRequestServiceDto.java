package com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.point;

public record UsePointRequestServiceDto(
	Long storeId,
	String barcode,
	Integer usePoint
) {
}