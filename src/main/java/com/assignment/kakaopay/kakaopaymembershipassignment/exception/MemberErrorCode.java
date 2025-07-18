package com.assignment.kakaopay.kakaopaymembershipassignment.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {
	ALREADY_EXISTS("M-001", "이미 존재하는 아이디입니다.", HttpStatus.ALREADY_REPORTED);

	private final String errorCode;
	private final String message;
	private final HttpStatus status;
}