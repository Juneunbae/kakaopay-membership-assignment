package com.assignment.kakaopay.kakaopaymembershipassignment.exception.point;

import org.springframework.http.HttpStatus;

import com.assignment.kakaopay.kakaopaymembershipassignment.exception.BaseErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PointErrorCode implements BaseErrorCode {
	NOT_ENOUGH_POINT("P-001", "사용 가능한 포인트가 부족합니다.", HttpStatus.BAD_REQUEST);

	private final String errorCode;
	private final String message;
	private final HttpStatus status;
}