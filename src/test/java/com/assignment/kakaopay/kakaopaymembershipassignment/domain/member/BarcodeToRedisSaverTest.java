package com.assignment.kakaopay.kakaopaymembershipassignment.domain.member;

import static org.mockito.Mockito.*;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.cache.BarcodeRedisValueDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.member.service.BarcodeToRedisSaver;

@ExtendWith(MockitoExtension.class)
public class BarcodeToRedisSaverTest {
	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@Mock
	private ValueOperations<String, Object> valueOperations;

	@InjectMocks
	private BarcodeToRedisSaver barcodeToRedisSaver;

	@BeforeEach
	void setUp() {
		this.barcodeToRedisSaver = new BarcodeToRedisSaver(redisTemplate);
	}

	@Test
	@DisplayName("Redis 바코드 저장 성공 테스트")
	void successSave() {
		// given
		String barcode = "123456789";
		Long userId = 1L;
		BarcodeRedisValueDto expectedValue = new BarcodeRedisValueDto(barcode, userId);

		// when
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);

		barcodeToRedisSaver.save(barcode, userId);

		// then
		verify(redisTemplate).opsForValue();
		verify(valueOperations).set(eq("barcode:" + barcode), eq(expectedValue), eq(5L), eq(TimeUnit.MINUTES));
	}
}