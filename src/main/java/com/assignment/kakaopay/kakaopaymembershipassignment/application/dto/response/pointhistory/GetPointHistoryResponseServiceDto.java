package com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.pointhistory;

import java.time.LocalDateTime;

import com.assignment.kakaopay.kakaopaymembershipassignment.domain.category.Category;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.point.Action;

public record GetPointHistoryResponseServiceDto(
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