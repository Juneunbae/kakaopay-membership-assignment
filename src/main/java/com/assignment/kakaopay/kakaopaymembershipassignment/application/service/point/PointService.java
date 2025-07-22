package com.assignment.kakaopay.kakaopaymembershipassignment.application.service.point;

import org.springframework.stereotype.Service;

import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.mapper.point.PointApplicationMapper;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.point.RewardPointRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.point.RewardPointResponseServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.service.store.StoreService;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.service.member.MemberService;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.point.Point;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.point.service.PointSaver;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.store.Store;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.GlobalException;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.member.MemberErrorCode;
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
	private final PointRepository pointRepository;
	private final PointApplicationMapper mapper;

	@Transactional
	public RewardPointResponseServiceDto rewardPoint(RewardPointRequestServiceDto request) {
		if (!memberService.existsByBarcode(request.barcode())) {
			throw new GlobalException(MemberErrorCode.NOT_FOUND);
		}

		Store store = storeService.findById(request.storeId());

		Point point = pointRepository.findByBarcodeAndCategory(request.barcode(), store.getCategory());

		Point rewardPoint = pointSaver.reward(request, point, store);
		pointRepository.save(rewardPoint);

		return mapper.toRewardPointResponseServiceDto(
			request.barcode(), request.rewardPoint(), rewardPoint.getPoint()
		);
	}
}