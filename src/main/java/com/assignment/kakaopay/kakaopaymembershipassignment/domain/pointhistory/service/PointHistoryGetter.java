package com.assignment.kakaopay.kakaopaymembershipassignment.domain.pointhistory.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.assignment.kakaopay.kakaopaymembershipassignment.domain.pointhistory.PointHistory;
import com.assignment.kakaopay.kakaopaymembershipassignment.infrastructure.pointhistory.PointHistoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointHistoryGetter {
	private final PointHistoryRepository pointHistoryRepository;

	public List<PointHistory> getHistories(LocalDateTime startDate, LocalDateTime endDate) {
		return pointHistoryRepository.findAllByCreatedAtBetweenOrderByCreatedAtDesc(startDate, endDate);
	}
}