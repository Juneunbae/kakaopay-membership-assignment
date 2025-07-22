package com.assignment.kakaopay.kakaopaymembershipassignment.exception.point;

import org.springframework.http.HttpStatus;

import com.assignment.kakaopay.kakaopaymembershipassignment.exception.BaseErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PointErrorCode implements BaseErrorCode {
	;

	private final String errorCode;
	private final String message;
	private final HttpStatus status;
}