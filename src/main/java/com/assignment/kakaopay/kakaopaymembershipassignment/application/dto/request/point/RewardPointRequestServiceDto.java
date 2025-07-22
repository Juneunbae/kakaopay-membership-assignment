package com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.point;

public record RewardPointRequestServiceDto(
	Long storeId,
	String barcode,
	Integer rewardPoint
) {
}