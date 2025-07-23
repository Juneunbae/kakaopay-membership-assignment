package com.assignment.kakaopay.kakaopaymembershipassignment.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(GlobalException.class)
	public ResponseEntity<?> globalExceptionHandle(GlobalException ex) {
		log.error("[GlobalException] {} - {}", ex.getBaseErrorCode().getStatus().value(), ex.getMessage());

		return ResponseEntity
			.status(ex.getBaseErrorCode().getStatus())
			.body(ErrorMessage.of(
					ex.getBaseErrorCode().getErrorCode(),
					ex.getBaseErrorCode().getStatus().value(),
					ex.getBaseErrorCode().getMessage()
				)
			);
	}

	@Getter
	@AllArgsConstructor
	public static class ErrorMessage {
		String errorCode;
		Integer status;
		String message;

		public static ErrorMessage of(String errorCode, Integer statusCode, String message) {
			return new ErrorMessage(errorCode, statusCode, message);
		}
	}
}