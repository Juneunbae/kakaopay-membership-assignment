package com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.response.point;

public record RewardPointResponseDto(
	String barcode,
	Integer rewardPoint,
	Integer totalPoint
) {
}