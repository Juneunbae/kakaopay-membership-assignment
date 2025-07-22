package com.assignment.kakaopay.kakaopaymembershipassignment.exception.member;

import org.springframework.http.HttpStatus;

import com.assignment.kakaopay.kakaopaymembershipassignment.exception.BaseErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {
	ALREADY_EXISTS("M-001", "이미 존재하는 아이디입니다.", HttpStatus.ALREADY_REPORTED),
	NO_SUCH_SHA_256("M-002", "SHA-256 알고리즘을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	NOT_FOUND("M-003", "존재하지 않는 유저입니다.", HttpStatus.NOT_FOUND);

	private final String errorCode;
	private final String message;
	private final HttpStatus status;
}