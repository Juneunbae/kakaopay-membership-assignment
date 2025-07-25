package com.assignment.kakaopay.kakaopaymembershipassignment.exception.pointhistory;

import org.springframework.http.HttpStatus;

import com.assignment.kakaopay.kakaopaymembershipassignment.exception.BaseErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PointHistoryErrorCode implements BaseErrorCode {
	NOT_AFTER_START_DATE("PH-001", "시작일은 종료일을 넘을 수 없습니다.", HttpStatus.BAD_REQUEST);

	private final String errorCode;
	private final String message;
	private final HttpStatus status;
}