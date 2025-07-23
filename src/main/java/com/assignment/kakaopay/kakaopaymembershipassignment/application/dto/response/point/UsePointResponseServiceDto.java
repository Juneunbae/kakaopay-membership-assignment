package com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.point;

public record UsePointResponseServiceDto(
	Long storeId,
	String barcode,
	Integer usePoint,
	Integer remainPoint
) {
}