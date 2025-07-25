package com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.response.pointhistory;

import java.time.LocalDateTime;

import com.assignment.kakaopay.kakaopaymembershipassignment.domain.category.Category;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.point.Action;

public record GetPointHistoryResponseDto(
	Long id,
	String barcode,
	Integer point,
	Action action,
	Integer totalPoint,
	Long storeId,
	String storeName,
	Category category,
	LocalDateTime createdAt
) {
}