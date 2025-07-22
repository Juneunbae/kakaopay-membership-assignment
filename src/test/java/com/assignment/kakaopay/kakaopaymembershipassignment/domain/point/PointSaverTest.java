package com.assignment.kakaopay.kakaopaymembershipassignment.domain.point;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.RewardPointRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.category.Category;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.point.service.PointSaver;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.store.Store;

@DisplayName("PointSaver 단위 테스트")
public class PointSaverTest {
	private final PointSaver pointSaver = new PointSaver();

	@Test
	@DisplayName("기존 포인트가 없는 경우 새 포인트 생성")
	void createNewPointWhenNull() {
		// given
		RewardPointRequestServiceDto request = new RewardPointRequestServiceDto(1L, "1234567890", 100);

		Store store = Store.builder()
			.id(1L)
			.name("테스트가게")
			.category(Category.COSMETIC)
			.build();

		// when
		Point result = pointSaver.reward(request, null, store);

		// then
		assertThat(result.getBarcode()).isEqualTo(request.barcode());
		assertThat(result.getPoint()).isEqualTo(100);
		assertThat(result.getCategory()).isEqualTo(Category.COSMETIC);
	}

	@Test
	@DisplayName("기존 포인트가 있는 경우 누적")
	void updateExistingPoint() {
		// given
		RewardPointRequestServiceDto request = new RewardPointRequestServiceDto(1L, "1234567890", 50);

		Point existingPoint = Point.builder()
			.barcode("1234567890")
			.category(Category.COSMETIC)
			.point(150)
			.build();

		Store store = Store.builder()
			.id(1L)
			.name("테스트가게")
			.category(Category.COSMETIC)
			.build();

		// when
		Point result = pointSaver.reward(request, existingPoint, store);

		// then
		assertThat(result.getPoint()).isEqualTo(200);
		assertThat(result).isSameAs(existingPoint);
	}
}