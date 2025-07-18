package com.assignment.kakaopay.kakaopaymembershipassignment.domain.member.generator;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserIdGenerator {
	private static final String USER_ID_KEY = "user:id:seq";
	private final RedisTemplate<String, Object> redisTemplate;

	public String generateUserId() {
		Long nextId = redisTemplate.opsForValue().increment(USER_ID_KEY);
		return String.format("%09d", nextId);
	}
}