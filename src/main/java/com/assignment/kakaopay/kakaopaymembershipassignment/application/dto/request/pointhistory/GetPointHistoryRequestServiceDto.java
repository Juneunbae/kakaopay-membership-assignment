package com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.pointhistory;

import java.util.Date;

public record GetPointHistoryRequestServiceDto(
	Date startDate,
	Date endDate,
	String barcode
) {
}