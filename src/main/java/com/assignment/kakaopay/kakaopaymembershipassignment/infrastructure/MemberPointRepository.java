package com.assignment.kakaopay.kakaopaymembershipassignment.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assignment.kakaopay.kakaopaymembershipassignment.domain.MemberPoint;

public interface MemberPointRepository extends JpaRepository<MemberPoint, Long> {
}