package com.assignment.kakaopay.kakaopaymembershipassignment.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assignment.kakaopay.kakaopaymembershipassignment.domain.PointHistory;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
}