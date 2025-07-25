package com.assignment.kakaopay.kakaopaymembershipassignment.application.pointhistory.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.mapper.pointhistory.PointHistoryApplicationMapper;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.pointhistory.GetPointHistoryRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.pointhistory.GetPointHistoryResponseServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.service.member.MemberService;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.service.pointhistory.PointHistoryService;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.category.Category;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.point.Action;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.pointhistory.PointHistory;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.pointhistory.service.PointHistoryGetter;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.GlobalException;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.member.MemberErrorCode;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.pointhistory.PointHistoryErrorCode;

@ExtendWith(MockitoExtension.class)
public class PointHistoryServiceTest {
	@Mock
	private MemberService memberService;

	@Mock
	private PointHistoryGetter pointHistoryGetter;

	@Mock
	private PointHistoryApplicationMapper mapper;

	@InjectMocks
	private PointHistoryService pointHistoryService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		pointHistoryService = new PointHistoryService(
			memberService, pointHistoryGetter, mapper
		);
	}

	@Test
	@DisplayName("포인트 적립 내역 조회 성공 테스트")
	void successGetPointHistories() {
		// given
		String barcode = "123456890";
		Date startDate = Date.from(LocalDate.of(2025, 7, 20).atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(LocalDate.of(2025, 7, 22).atStartOfDay(ZoneId.systemDefault()).toInstant());

		GetPointHistoryRequestServiceDto request = new GetPointHistoryRequestServiceDto(startDate, endDate, barcode);

		LocalDateTime expectedStart = LocalDateTime.of(2025, 7, 20, 0, 0);
		LocalDateTime expectedEnd = LocalDateTime.of(2025, 7, 23, 0, 0);

		List<PointHistory> mockHistories = List.of(
			PointHistory.builder()
				.id(1L)
				.barcode(barcode)
				.point(100)
				.totalPoint(1000)
				.action(Action.REWARD)
				.storeId(1L)
				.category(Category.COSMETIC)
				.storeName("테스트 화장품 가게")
				.createdAt(LocalDateTime.now())
				.build()
		);

		GetPointHistoryResponseServiceDto mappedDto = new GetPointHistoryResponseServiceDto(
			1L, barcode, 100, Action.REWARD, 1000,
			1L, "테스트 화장품 가게", Category.COSMETIC, LocalDateTime.now()
		);

		// when
		doNothing().when(memberService).existsByBarcode(request.barcode());
		when(pointHistoryGetter.getHistories(expectedStart, expectedEnd)).thenReturn(mockHistories);
		when(mapper.toGetPointHistoryResponseServiceDto(mockHistories.getFirst())).thenReturn(mappedDto);

		// then
		List<GetPointHistoryResponseServiceDto> result = pointHistoryService.getPointHistories(request);

		assertThat(result).hasSize(1);
		assertThat(result.getFirst().barcode()).isEqualTo(barcode);
		assertThat(result.getFirst().point()).isEqualTo(100);

		verify(memberService).existsByBarcode(request.barcode());
		verify(pointHistoryGetter).getHistories(expectedStart, expectedEnd);
		verify(mapper).toGetPointHistoryResponseServiceDto(mockHistories.getFirst());
	}

	@Test
	@DisplayName("포인트 적립 내역 조회 실패 테스트 - 존재하지 않는 바코드")
	void failNotFoundBarcode() {
		// given
		String barcode = "9999999999";
		Date startDate = Date.from(LocalDate.of(2025, 7, 20).atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(LocalDate.of(2025, 7, 22).atStartOfDay(ZoneId.systemDefault()).toInstant());

		GetPointHistoryRequestServiceDto request = new GetPointHistoryRequestServiceDto(startDate, endDate, barcode);

		doThrow(new GlobalException(MemberErrorCode.NOT_FOUND))
			.when(memberService).existsByBarcode(barcode);

		// when & then
		assertThatThrownBy(() -> pointHistoryService.getPointHistories(request))
			.isInstanceOf(GlobalException.class)
			.hasMessageContaining(MemberErrorCode.NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("포인트 적립 내역 조회 실패 테스트 - 시작일보다 종료일이 미래")
	void failEndDateAfterStartDate() {
		// given
		String barcode = "9999999999";
		Date startDate = Date.from(LocalDate.of(2025, 7, 22).atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(LocalDate.of(2025, 7, 20).atStartOfDay(ZoneId.systemDefault()).toInstant());

		GetPointHistoryRequestServiceDto request = new GetPointHistoryRequestServiceDto(startDate, endDate, barcode);

		assertThatThrownBy(() -> pointHistoryService.getPointHistories(request))
			.isInstanceOf(GlobalException.class)
			.hasMessageContaining(PointHistoryErrorCode.NOT_AFTER_START_DATE.getMessage());
	}
}