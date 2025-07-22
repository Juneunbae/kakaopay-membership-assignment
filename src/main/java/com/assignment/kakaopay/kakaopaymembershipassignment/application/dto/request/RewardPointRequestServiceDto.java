package com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request;

public record RewardPointRequestServiceDto(
	Long storeId,
	String barcode,
	Integer rewardPoint
) {
}