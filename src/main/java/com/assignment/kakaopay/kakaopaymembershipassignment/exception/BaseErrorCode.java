package com.assignment.kakaopay.kakaopaymembershipassignment.exception;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {
	String getErrorCode();

	String getMessage();

	HttpStatus getStatus();
}