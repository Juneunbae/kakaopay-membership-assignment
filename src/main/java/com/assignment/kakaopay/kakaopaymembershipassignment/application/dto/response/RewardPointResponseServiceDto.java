package com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response;

public record RewardPointResponseServiceDto(
	String barcode,
	Integer rewardPoint,
	Integer totalPoint
) {
}