package com.assignment.kakaopay.kakaopaymembershipassignment.infrastructure.pointhistory;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assignment.kakaopay.kakaopaymembershipassignment.domain.pointhistory.PointHistory;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
	List<PointHistory> findAllByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);
}