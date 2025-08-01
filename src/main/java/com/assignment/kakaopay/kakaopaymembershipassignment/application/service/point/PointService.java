package com.assignment.kakaopay.kakaopaymembershipassignment.application.service.point;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

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
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.point.Point;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.point.service.PointConsumer;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.point.service.PointSaver;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.store.Store;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.GlobalException;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.point.PointErrorCode;
import com.assignment.kakaopay.kakaopaymembershipassignment.infrastructure.point.PointRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {
	private final PointSaver pointSaver;
	private final StoreService storeService;
	private final MemberService memberService;
	private final PointConsumer pointConsumer;
	private final PointRepository pointRepository;
	private final PointApplicationMapper mapper;
	private final ApplicationEventPublisher publisher;

	@Transactional
	public RewardPointResponseServiceDto rewardPoint(RewardPointRequestServiceDto request) {
		memberService.existsByBarcode(request.barcode());

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
		memberService.existsByBarcode(request.barcode());

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
	}

	private Point findByBarcodeAndCategory(String barcode, Category category) {
		return pointRepository.findByBarcodeAndCategory(barcode, category);
	}
}