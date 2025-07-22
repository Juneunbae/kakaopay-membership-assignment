package com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.point;

public record RewardPointResponseServiceDto(
	String barcode,
	Integer rewardPoint,
	Integer totalPoint
) {
}