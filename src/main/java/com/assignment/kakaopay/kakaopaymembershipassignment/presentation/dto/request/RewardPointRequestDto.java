package com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.request;

public record RewardPointRequestDto(
	Long storeId,
	String barcode,
	Integer rewardPoint
) {
}