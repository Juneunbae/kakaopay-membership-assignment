package com.assignment.kakaopay.kakaopaymembershipassignment.application.service.point;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.cache.BarcodeRedisValueDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.mapper.point.PointApplicationMapper;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.point.RewardPointRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.point.UsePointRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.point.RewardPointResponseServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.point.UsePointResponseServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.event.RewardPointEvent;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.event.UsePointEvent;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.service.member.MemberService;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.service.store.StoreService;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.category.Category;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.member.Member;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.member.service.BarcodeToRedisSaver;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.point.Point;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.point.service.PointConsumer;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.point.service.PointSaver;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.store.Store;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.GlobalException;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.point.PointErrorCode;
import com.assignment.kakaopay.kakaopaymembershipassignment.infrastructure.point.PointRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {
	private final PointSaver pointSaver;
	private final StoreService storeService;
	private final ObjectMapper objectMapper;
	private final MemberService memberService;
	private final PointConsumer pointConsumer;
	private final RedissonClient redissonClient;
	private final PointApplicationMapper mapper;
	private final PointRepository pointRepository;
	private final ApplicationEventPublisher publisher;
	private final BarcodeToRedisSaver barcodeToRedisSaver;
	private final RedisTemplate<String, Object> redisTemplate;

	@Transactional
	public RewardPointResponseServiceDto rewardPoint(RewardPointRequestServiceDto request) {
		this.getBarcodeFromRedis(request.barcode());

		Store store = storeService.findById(request.storeId());

		Point point = this.findByBarcodeAndCategory(request.barcode(), store.getCategory());

		Point rewardPoint = pointSaver.reward(request, point, store);
		pointRepository.save(rewardPoint);

		publisher.publishEvent(new RewardPointEvent(rewardPoint, request.rewardPoint(), store.getId()));

		return mapper.toRewardPointResponseServiceDto(
			request.barcode(), request.rewardPoint(), rewardPoint.getPoint()
		);
	}

	@Transactional
	public UsePointResponseServiceDto usePoint(UsePointRequestServiceDto request) {
		RLock lock = redissonClient.getLock("usePointBarcode:" + request.barcode());

		try {
			boolean isLocked = lock.tryLock(3, 5, TimeUnit.SECONDS);
			if (!isLocked) {
				throw new GlobalException(PointErrorCode.IN_USE_BARCODE);
			}

			this.getBarcodeFromRedis(request.barcode());

			Store store = storeService.findById(request.storeId());

			Point point = this.findByBarcodeAndCategory(request.barcode(), store.getCategory());

			if (point == null)
				throw new GlobalException(PointErrorCode.NOT_ENOUGH_POINT);
			else if (point.getPoint() < request.usePoint())
				throw new GlobalException(PointErrorCode.NOT_ENOUGH_POINT);

			Point updatePoint = pointConsumer.use(point, request.usePoint());
			pointRepository.save(updatePoint);

			publisher.publishEvent(new UsePointEvent(updatePoint, request.usePoint(), store.getId()));

			return mapper.toUsePointResponseServiceDto(
				store.getId(), request.barcode(), request.usePoint(), updatePoint.getPoint()
			);
		} catch (InterruptedException e) {
			throw new RuntimeException("Lock 획득 중 인터럽트 에러 발생 : {}", e);
		} finally {
			if (lock.isLocked() && lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
	}

	private Point findByBarcodeAndCategory(String barcode, Category category) {
		return pointRepository.findByBarcodeAndCategory(barcode, category);
	}

	private void getBarcodeFromRedis(String barcode) {
		Object redisValue = redisTemplate.opsForValue().get("barcode:" + barcode);

		if (redisValue == null) {
			Member getMemberFromDB = memberService.findByBarcode(barcode);
			barcodeToRedisSaver.save(getMemberFromDB.getBarcode(), getMemberFromDB.getId());
			log.debug("바코드 : {} 캐싱 성공", getMemberFromDB.getBarcode());
		} else {
			BarcodeRedisValueDto barcodeRedisValueDto = objectMapper.convertValue(
				redisValue, BarcodeRedisValueDto.class
			);
			log.debug("캐싱된 바코드 정보 : {} 가져오기 성공", barcodeRedisValueDto.barcode());
		}
	}
}