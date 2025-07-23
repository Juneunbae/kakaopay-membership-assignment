package com.assignment.kakaopay.kakaopaymembershipassignment.domain.point.service;

import org.springframework.stereotype.Service;

import com.assignment.kakaopay.kakaopaymembershipassignment.domain.point.Point;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PointConsumer {
	public Point use(Point point, Integer usePoint) {
		int updatePoint = point.getPoint() - usePoint;
		point.updatePoint(updatePoint);
		log.debug("바코드 : {} - 포인트 : {} 사용", point.getBarcode(), usePoint);
		return point;
	}
}