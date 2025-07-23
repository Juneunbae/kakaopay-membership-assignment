package com.assignment.kakaopay.kakaopaymembershipassignment.application.point.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.mapper.point.PointApplicationMapper;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.point.RewardPointRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.point.UsePointRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.point.RewardPointResponseServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.point.UsePointResponseServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.event.UsePointEvent;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.service.member.MemberService;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.service.point.PointService;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.service.store.StoreService;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.category.Category;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.point.Point;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.point.service.PointConsumer;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.point.service.PointSaver;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.store.Store;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.GlobalException;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.member.MemberErrorCode;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.point.PointErrorCode;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.store.StoreErrorCode;
import com.assignment.kakaopay.kakaopaymembershipassignment.infrastructure.point.PointRepository;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {
	@Mock
	private PointSaver pointSaver;

	@Mock
	private StoreService storeService;

	@Mock
	private MemberService memberService;

	@Mock
	private PointConsumer pointConsumer;

	@Mock
	private PointRepository pointRepository;

	@Mock
	private PointApplicationMapper mapper;

	@Mock
	private ApplicationEventPublisher publisher;

	@InjectMocks
	private PointService pointService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		pointService = new PointService(
			pointSaver, storeService, memberService, pointConsumer, pointRepository, mapper, publisher
		);
	}

	@Test
	@DisplayName("포인트 적립 성공 테스트")
	void successRewardPoint() {
		// given
		int reward = 100;
		Long storeId = 1L;
		String barcode = "1234567890";

		RewardPointRequestServiceDto request = new RewardPointRequestServiceDto(storeId, barcode, reward);

		Category category = Category.COSMETIC;
		Store store = Store.builder()
			.id(storeId)
			.name("테스트 화장품")
			.category(category)
			.build();

		Point existingPoint = null;
		Point updatedPoint = Point.builder()
			.barcode(barcode)
			.category(category)
			.point(100)
			.build();

		RewardPointResponseServiceDto expectedResponse = new RewardPointResponseServiceDto(barcode, reward, 100);

		given(storeService.findById(storeId)).willReturn(store);
		given(pointRepository.findByBarcodeAndCategory(barcode, category)).willReturn(existingPoint);
		given(pointSaver.reward(request, existingPoint, store)).willReturn(updatedPoint);
		given(mapper.toRewardPointResponseServiceDto(barcode, reward, 100)).willReturn(expectedResponse);

		// when
		doNothing().when(memberService).existsByBarcode(barcode);
		RewardPointResponseServiceDto response = pointService.rewardPoint(request);

		// then
		assertThat(response.barcode()).isEqualTo(expectedResponse.barcode());
		assertThat(response.rewardPoint()).isEqualTo(expectedResponse.rewardPoint());
		assertThat(response.totalPoint()).isEqualTo(100);

		then(pointRepository).should().save(updatedPoint);
	}

	@Test
	@DisplayName("포인트 적립 실패 테스트 - 존재하지 않는 바코드")
	void failNotFoundBarcode() {
		// given
		int reward = 100;
		Long storeId = 1L;
		String barcode = "9999999999";

		RewardPointRequestServiceDto request = new RewardPointRequestServiceDto(storeId, barcode, reward);

		doThrow(new GlobalException(MemberErrorCode.NOT_FOUND))
			.when(memberService).existsByBarcode(barcode);

		// when & then
		assertThatThrownBy(() -> pointService.rewardPoint(request))
			.isInstanceOf(GlobalException.class)
			.hasMessageContaining(MemberErrorCode.NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("포인트 적립 실패 테스트 - 존재하지 않는 가게")
	void failNotFoundStore() {
		// given
		int reward = 100;
		Long storeId = 1L;
		String barcode = "9999999999";

		RewardPointRequestServiceDto request = new RewardPointRequestServiceDto(storeId, barcode, reward);

		doNothing().when(memberService).existsByBarcode(barcode);
		given(storeService.findById(storeId)).willThrow(new GlobalException(StoreErrorCode.NOT_FOUND));

		// when & then
		assertThatThrownBy(() -> pointService.rewardPoint(request))
			.isInstanceOf(GlobalException.class)
			.hasMessageContaining(StoreErrorCode.NOT_FOUND.getMessage());
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