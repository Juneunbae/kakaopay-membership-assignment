package com.assignment.kakaopay.kakaopaymembershipassignment.domain.member.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.assignment.kakaopay.kakaopaymembershipassignment.domain.member.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberCreator {
	public Member create(String userId, String username) {
		return Member.builder()
			.userId(userId)
			.username(username)
			.createdAt(LocalDateTime.now())
			.build();
	}
}