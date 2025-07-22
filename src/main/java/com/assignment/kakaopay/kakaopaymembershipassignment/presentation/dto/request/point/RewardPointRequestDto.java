package com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.request.point;

public record RewardPointRequestDto(
	Long storeId,
	String barcode,
	Integer rewardPoint
) {
}