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

import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.mapper.PointApplicationMapper;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.RewardPointRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.RewardPointResponseServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.service.MemberService;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.service.PointService;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.service.StoreService;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.category.Category;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.point.Point;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.point.service.PointSaver;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.store.Store;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.GlobalException;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.MemberErrorCode;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.StoreErrorCode;
import com.assignment.kakaopay.kakaopaymembershipassignment.infrastructure.PointRepository;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {
	@Mock
	private PointSaver pointSaver;

	@Mock
	private StoreService storeService;

	@Mock
	private MemberService memberService;

	@Mock
	private PointRepository pointRepository;

	@Mock
	private PointApplicationMapper mapper;

	@InjectMocks
	private PointService pointService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		pointService = new PointService(pointSaver, storeService, memberService, pointRepository, mapper);
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

		given(memberService.existsByBarcode(barcode)).willReturn(true);
		given(storeService.findById(storeId)).willReturn(store);
		given(pointRepository.findByBarcodeAndCategory(barcode, category)).willReturn(existingPoint);
		given(pointSaver.reward(request, existingPoint, store)).willReturn(updatedPoint);
		given(mapper.toRewardPointResponseServiceDto(barcode, reward, 100)).willReturn(expectedResponse);

		// when
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

		given(memberService.existsByBarcode(barcode)).willReturn(false);

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

		given(memberService.existsByBarcode(barcode)).willReturn(true);
		given(storeService.findById(storeId)).willThrow(new GlobalException(StoreErrorCode.NOT_FOUND));

		// when & then
		assertThatThrownBy(() -> pointService.rewardPoint(request))
			.isInstanceOf(GlobalException.class)
			.hasMessageContaining(StoreErrorCode.NOT_FOUND.getMessage());
	}
}