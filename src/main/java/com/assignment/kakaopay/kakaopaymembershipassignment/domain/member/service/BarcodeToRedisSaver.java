package com.assignment.kakaopay.kakaopaymembershipassignment.domain.member.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.cache.BarcodeRedisValueDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BarcodeToRedisSaver {
	private final RedisTemplate<String, Object> redisTemplate;

	public void save(String barcode, Long userId) {
		String key = "barcode:" + barcode;
		Map<String, Integer> categoryPoints = new HashMap<>();
		categoryPoints.put("Food", 0);
		categoryPoints.put("Restaurant", 0);
		categoryPoints.put("Cosmetic", 0);
		BarcodeRedisValueDto value = new BarcodeRedisValueDto(barcode, userId, categoryPoints);
		redisTemplate.opsForValue().set(key, value, 5, TimeUnit.MINUTES);
	}
}