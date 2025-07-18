package com.assignment.kakaopay.kakaopaymembershipassignment.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assignment.kakaopay.kakaopaymembershipassignment.domain.member.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Boolean existsByUsername(String username);
}