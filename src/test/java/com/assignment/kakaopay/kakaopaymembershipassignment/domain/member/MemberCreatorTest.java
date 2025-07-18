package com.assignment.kakaopay.kakaopaymembershipassignment.domain.member;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.assignment.kakaopay.kakaopaymembershipassignment.domain.member.service.MemberCreator;

@DisplayName("MemberCreator 도메인 서비스 테스트")
public class MemberCreatorTest {
	private MemberCreator memberCreator;

	@BeforeEach
	void setUp() {
		memberCreator = new MemberCreator();
	}

	@Test
	void successCreate() {
		// given
		String userId = "123456789";
		String username = "홍길동";

		// when
		Member member = memberCreator.create(userId, username);

		// then
		assertThat(member.getUserId()).isEqualTo(userId);
		assertThat(member.getUsername()).isEqualTo(username);
	}
}