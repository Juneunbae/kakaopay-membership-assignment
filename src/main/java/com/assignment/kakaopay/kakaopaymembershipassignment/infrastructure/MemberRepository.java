package com.assignment.kakaopay.kakaopaymembershipassignment.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assignment.kakaopay.kakaopaymembershipassignment.domain.member.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Boolean existsByUsername(String username);

	Optional<Member> findByUserId(String userId);

	Boolean existsByBarcode(String barcode);
}