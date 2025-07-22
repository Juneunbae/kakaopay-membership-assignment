package com.assignment.kakaopay.kakaopaymembershipassignment.domain.point.service;

import org.springframework.stereotype.Service;

import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.point.RewardPointRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.point.Point;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.store.Store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointSaver {

	public Point reward(RewardPointRequestServiceDto request, Point point, Store store) {
		if (point == null) {
			log.debug("바코드 : {} - 새로운 포인트 적립", request.barcode());
			return Point.builder()
				.barcode(request.barcode())
				.category(store.getCategory())
				.point(request.rewardPoint())
				.build();
		} else {
			int updatePoint = point.getPoint() + request.rewardPoint();
			point.updatePoint(updatePoint);
			log.debug("바코드 : {} - 포인트 적립", request.barcode());
			return point;
		}
	}
}