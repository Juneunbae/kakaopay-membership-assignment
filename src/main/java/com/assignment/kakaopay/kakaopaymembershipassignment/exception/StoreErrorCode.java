package com.assignment.kakaopay.kakaopaymembershipassignment.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StoreErrorCode implements BaseErrorCode {
	NOT_FOUND("S-001", "존재하지 않는 가게입니다.", HttpStatus.NOT_FOUND),
	;

	private final String errorCode;
	private final String message;
	private final HttpStatus status;
}