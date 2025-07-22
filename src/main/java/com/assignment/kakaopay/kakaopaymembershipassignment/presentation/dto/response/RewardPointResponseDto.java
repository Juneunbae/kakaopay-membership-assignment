package com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.response;

public record RewardPointResponseDto(
	String barcode,
	Integer rewardPoint,
	Integer totalPoint
) {
}