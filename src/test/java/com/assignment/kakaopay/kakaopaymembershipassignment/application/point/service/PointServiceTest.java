package com.assignment.kakaopay.kakaopaymembershipassignment.application.point.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.cache.BarcodeRedisValueDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.mapper.point.PointApplicationMapper;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.point.RewardPointRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.point.UsePointRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.point.RewardPointResponseServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.point.UsePointResponseServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.event.RewardPointEvent;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.event.UsePointEvent;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.service.member.MemberService;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.service.point.PointService;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.service.store.StoreService;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.category.Category;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.member.Member;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.member.service.BarcodeToRedisSaver;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.point.Point;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.point.service.PointConsumer;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.point.service.PointSaver;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.store.Store;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.GlobalException;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.member.MemberErrorCode;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.point.PointErrorCode;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.store.StoreErrorCode;
import com.assignment.kakaopay.kakaopaymembershipassignment.infrastructure.point.PointRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {
	@Mock
	private PointSaver pointSaver;

	@Mock
	private StoreService storeService;

	@Mock
	private ObjectMapper objectMapper;

	@Mock
	private MemberService memberService;

	@Mock
	private PointConsumer pointConsumer;

	@Mock
	private PointApplicationMapper mapper;

	@Mock
	private PointRepository pointRepository;

	@Mock
	private ApplicationEventPublisher publisher;

	@Mock
	private BarcodeToRedisSaver barcodeToRedisSaver;

	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@Mock
	private ValueOperations<String, Object> valueOperations;

	@InjectMocks
	private PointService pointService;

	@BeforeEach
	void setUp() {
		pointService = new PointService(
			pointSaver, storeService, objectMapper, memberService, pointConsumer,
			mapper, pointRepository, publisher, barcodeToRedisSaver, redisTemplate
		);

		given(redisTemplate.opsForValue()).willReturn(valueOperations);
	}

	@Test
	@DisplayName("포인트 적립 성공 테스트 - Redis 캐시 미스")
	void successRewardPointCacheMiss() {
		Long storeId = 1L;
		String barcode = "1234567890";
		int reward = 100;
		Category category = Category.COSMETIC;

		RewardPointRequestServiceDto request = new RewardPointRequestServiceDto(storeId, barcode, reward);

		Store store = Store.builder()
			.id(storeId)
			.name("테스트 화장품")
			.category(category)
			.build();

		Member member = Member.builder()
			.id(1L)
			.userId("testUser")
			.barcode(barcode)
			.build();

		Point existingPoint = null;
		Point updatedPoint = Point.builder()
			.barcode(barcode)
			.category(category)
			.point(100)
			.build();

		RewardPointResponseServiceDto expectedResponse = new RewardPointResponseServiceDto(barcode, reward, 100);

		// given
		// Redis에 바코드가 없음
		given(valueOperations.get("barcode:" + request.barcode())).willReturn(null);
		given(memberService.findByBarcode(request.barcode())).willReturn(member);
		doNothing().when(barcodeToRedisSaver).save(member.getBarcode(), member.getId());
		given(storeService.findById(request.storeId())).willReturn(store);
		given(pointRepository.findByBarcodeAndCategory(request.barcode(), store.getCategory()))
			.willReturn(existingPoint);
		given(pointSaver.reward(request, existingPoint, store)).willReturn(updatedPoint);
		given(mapper.toRewardPointResponseServiceDto(request.barcode(), request.rewardPoint(), updatedPoint.getPoint()))
			.willReturn(expectedResponse);

		// when
		RewardPointResponseServiceDto response = pointService.rewardPoint(request);

		// then
		assertThat(response.barcode()).isEqualTo(expectedResponse.barcode());
		assertThat(response.rewardPoint()).isEqualTo(expectedResponse.rewardPoint());
		assertThat(response.totalPoint()).isEqualTo(expectedResponse.totalPoint());

		then(valueOperations).should(times(1)).get("barcode:" + request.barcode());
		then(memberService).should(times(1)).findByBarcode(request.barcode());
		then(barcodeToRedisSaver).should(times(1)).save(member.getBarcode(), member.getId());
		then(storeService).should(times(1)).findById(request.storeId());
		then(pointRepository).should(times(1)).findByBarcodeAndCategory(request.barcode(), store.getCategory());
		then(pointSaver).should(times(1)).reward(request, existingPoint, store);
		then(pointRepository).should(times(1)).save(updatedPoint);
		then(publisher).should(times(1)).publishEvent(
			new RewardPointEvent(updatedPoint, request.rewardPoint(), store.getId())
		);
		then(mapper).should(times(1)).toRewardPointResponseServiceDto(
			request.barcode(), request.rewardPoint(), updatedPoint.getPoint()
		);
	}

	@Test
	@DisplayName("포인트 적립 성공 테스트 - Redis 캐시 히트")
	void successRewardPointCacheHit() {
		Long storeId = 1L;
		String barcode = "1234567890";
		int reward = 100;
		Category category = Category.COSMETIC;

		RewardPointRequestServiceDto request = new RewardPointRequestServiceDto(storeId, barcode, reward);

		BarcodeRedisValueDto cachedValueDto = new BarcodeRedisValueDto(barcode, 1L);

		Store store = Store.builder()
			.id(storeId)
			.name("테스트 화장품")
			.category(category)
			.build();

		Member member = Member.builder()
			.id(1L)
			.userId("testUser")
			.barcode(barcode)
			.build();

		Point existingPoint = null;
		Point updatedPoint = Point.builder()
			.barcode(barcode)
			.category(category)
			.point(100)
			.build();

		RewardPointResponseServiceDto expectedResponse = new RewardPointResponseServiceDto(barcode, reward, 100);

		// given
		// Redis 바코드 존재함을 String을 반환하도록 설정
		given(valueOperations.get("barcode:" + request.barcode())).willReturn("mockRedisValue");
		given(objectMapper.convertValue(any(), eq(BarcodeRedisValueDto.class))).willReturn(cachedValueDto);
		given(storeService.findById(request.storeId())).willReturn(store);
		given(pointRepository.findByBarcodeAndCategory(request.barcode(), store.getCategory()))
			.willReturn(existingPoint);
		given(pointSaver.reward(request, existingPoint, store)).willReturn(updatedPoint);
		given(mapper.toRewardPointResponseServiceDto(request.barcode(), request.rewardPoint(), updatedPoint.getPoint()))
			.willReturn(expectedResponse);

		// when
		RewardPointResponseServiceDto response = pointService.rewardPoint(request);

		// then
		assertThat(response.barcode()).isEqualTo(expectedResponse.barcode());
		assertThat(response.rewardPoint()).isEqualTo(expectedResponse.rewardPoint());
		assertThat(response.totalPoint()).isEqualTo(expectedResponse.totalPoint());

		then(valueOperations).should(times(1)).get("barcode:" + request.barcode());
		then(objectMapper).should(times(1)).convertValue(any(), eq(BarcodeRedisValueDto.class));

		// Redis 캐시 성공으로 DB 메서드 호출되지 않는 것 검증
		then(memberService).should(never()).findByBarcode(anyString());
		then(barcodeToRedisSaver).should(never()).save(anyString(), anyLong());

		then(storeService).should(times(1)).findById(request.storeId());
		then(pointRepository).should(times(1)).findByBarcodeAndCategory(request.barcode(), store.getCategory());
		then(pointSaver).should(times(1)).reward(request, existingPoint, store);
		then(pointRepository).should(times(1)).save(updatedPoint);
		then(publisher).should(times(1)).publishEvent(
			new RewardPointEvent(updatedPoint, request.rewardPoint(), store.getId())
		);
		then(mapper).should(times(1)).toRewardPointResponseServiceDto(
			request.barcode(), request.rewardPoint(), updatedPoint.getPoint()
		);
	}

	@Test
	@DisplayName("포인트 적립 실패 테스트 - 존재하지 않는 바코드")
	void failNotFoundBarcode() {
		int reward = 100;
		Long storeId = 1L;
		String barcode = "9999999999";

		RewardPointRequestServiceDto request = new RewardPointRequestServiceDto(storeId, barcode, reward);

		// given
		// 1. Redis 바코드 정보 X
		given(valueOperations.get("barcode:" + request.barcode())).willReturn(null);
		// 2. DB 바코드 정보 X
		given(memberService.findByBarcode(barcode)).willReturn(null);

		// when & then
		assertThrows(NullPointerException.class,
			() -> pointService.rewardPoint(request)
		);

		// verify
		then(valueOperations).should(times(1)).get("barcode:" + request.barcode());
		then(memberService).should(times(1)).findByBarcode(barcode);

		then(barcodeToRedisSaver).should(never()).save(anyString(), anyLong());
		then(storeService).should(never()).findById(anyLong());
		then(pointRepository).should(never()).save(any());
	}

	@Test
	@DisplayName("포인트 적립 실패 테스트 - 존재하지 않는 가게")
	void failNotFoundStore() {
		// given
		int reward = 100;
		Long storeId = 99L;
		String barcode = "9999999999";

		RewardPointRequestServiceDto request = new RewardPointRequestServiceDto(storeId, barcode, reward);

		BarcodeRedisValueDto cachedValueDto = new BarcodeRedisValueDto(barcode, 1L);

		// given
		given(valueOperations.get("barcode:" + request.barcode())).willReturn("mockRedisValue");
		given(objectMapper.convertValue(any(), eq(BarcodeRedisValueDto.class))).willReturn(cachedValueDto);
		given(storeService.findById(storeId)).willReturn(null);

		// when & then
		assertThrows(NullPointerException.class,
			() -> pointService.rewardPoint(request)
		);

		// verify
		then(valueOperations).should(times(1)).get("barcode:" + request.barcode());
		then(objectMapper).should(times(1)).convertValue(any(), eq(BarcodeRedisValueDto.class));
		then(storeService).should(times(1)).findById(storeId);

		then(pointRepository).should(never()).findByBarcodeAndCategory(anyString(), any());
		then(pointSaver).should(never()).reward(any(), any(), any());
	}

	@Test
	@DisplayName("포인트 사용 성공 테스트")
	void successUsePoint() {
		//given
		String barcode = "1234567890";
		Long storeId = 1L;
		int usePoint = 100;

		UsePointRequestServiceDto request = new UsePointRequestServiceDto(storeId, barcode, usePoint);

		Category category = Category.COSMETIC;

		Store store = Store.builder()
			.id(storeId)
			.name("테스트 가게")
			.category(category)
			.build();

		Point existingPoint = Point.builder()
			.barcode(barcode)
			.category(category)
			.point(200)
			.build();

		Point updatedPoint = Point.builder()
			.barcode(barcode)
			.category(category)
			.point(100)
			.build();

		UsePointResponseServiceDto expectedResponse = new UsePointResponseServiceDto(
			storeId, barcode, usePoint, 100
		);

		doNothing().when(memberService).existsByBarcode(barcode);
		given(storeService.findById(storeId)).willReturn(store);
		given(pointRepository.findByBarcodeAndCategory(barcode, category)).willReturn(existingPoint);
		given(pointConsumer.use(existingPoint, usePoint)).willReturn(updatedPoint);
		given(mapper.toUsePointResponseServiceDto(storeId, barcode, usePoint, 100)).willReturn(expectedResponse);

		// when
		UsePointResponseServiceDto response = pointService.usePoint(request);

		// then
		assertThat(response.storeId()).isEqualTo(expectedResponse.storeId());
		assertThat(response.barcode()).isEqualTo(expectedResponse.barcode());
		assertThat(response.usePoint()).isEqualTo(expectedResponse.usePoint());
		assertThat(response.remainPoint()).isEqualTo(expectedResponse.remainPoint());

		then(pointRepository).should().save(updatedPoint);
		then(publisher).should().publishEvent(any(UsePointEvent.class));
	}

	@Test
	@DisplayName("포인트 사용 실패 테스트 - 존재하지 않는 바코드")
	void failUsePointNotFoundBarcode() {
		// given
		int reward = 100;
		Long storeId = 1L;
		String barcode = "9999999999";

		UsePointRequestServiceDto request = new UsePointRequestServiceDto(storeId, barcode, reward);

		doThrow(new GlobalException(MemberErrorCode.NOT_FOUND))
			.when(memberService).existsByBarcode(barcode);

		// when & then
		assertThatThrownBy(() -> pointService.usePoint(request))
			.isInstanceOf(GlobalException.class)
			.hasMessageContaining(MemberErrorCode.NOT_FOUND.getMessage());

		then(memberService).should().existsByBarcode(barcode);
		then(storeService).shouldHaveNoInteractions();
	}

	@Test
	@DisplayName("포인트 사용 실패 테스트 - 존재하지 않는 가게")
	void failUsePointNotFoundStore() {
		// given
		int reward = 100;
		Long storeId = 1L;
		String barcode = "9999999999";

		UsePointRequestServiceDto request = new UsePointRequestServiceDto(storeId, barcode, reward);

		doNothing().when(memberService).existsByBarcode(barcode);
		doThrow(new GlobalException(StoreErrorCode.NOT_FOUND)).when(storeService).findById(storeId);

		// when & then
		assertThatThrownBy(() -> pointService.usePoint(request))
			.isInstanceOf(GlobalException.class)
			.hasMessageContaining(StoreErrorCode.NOT_FOUND.getMessage());

		then(memberService).should().existsByBarcode(barcode);
		then(storeService).should().findById(storeId);
		then(pointRepository).shouldHaveNoInteractions();
	}

	@Test
	@DisplayName("포인트 사용 실패 테스트 - 가지고 있는 포인트보다 이상인 포인트 사용")
	void failUsePointWhenUseMoreThanExistingPoint() {
		// given
		Long storeId = 1L;
		String barcode = "9999999999";
		int reward = 200;
		int currentPoint = 100;

		UsePointRequestServiceDto request = new UsePointRequestServiceDto(storeId, barcode, reward);

		Category category = Category.COSMETIC;

		Store store = Store.builder()
			.id(storeId)
			.name("테스트 가게")
			.category(category)
			.build();

		Point existingPoint = Point.builder()
			.barcode(barcode)
			.category(category)
			.point(currentPoint)
			.build();

		doNothing().when(memberService).existsByBarcode(barcode);
		given(storeService.findById(storeId)).willReturn(store);
		given(pointRepository.findByBarcodeAndCategory(barcode, category)).willReturn(existingPoint);

		// when
		assertThatThrownBy(() -> pointService.usePoint(request))
			.isInstanceOf(GlobalException.class)
			.hasMessageContaining(PointErrorCode.NOT_ENOUGH_POINT.getMessage());

		then(memberService).should().existsByBarcode(barcode);
		then(storeService).should().findById(storeId);
		then(pointRepository).should().findByBarcodeAndCategory(barcode, category);
		then(pointConsumer).shouldHaveNoInteractions();
		then(publisher).shouldHaveNoInteractions();
	}
}