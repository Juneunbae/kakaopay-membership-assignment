package com.assignment.kakaopay.kakaopaymembershipassignment.application.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.mapper.MemberApplicationMapper;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.MemberCreateRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.MemberCreateResponseServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.service.MemberService;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.member.Member;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.member.generator.UserIdGenerator;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.member.service.MemberCreator;
import com.assignment.kakaopay.kakaopaymembershipassignment.infrastructure.MemberRepository;

@DisplayName("MemberService 단위 테스트")
public class MemberServiceTest {
	@Mock
	private MemberRepository memberRepository;

	@Mock
	private UserIdGenerator userIdGenerator;

	@Mock
	private MemberApplicationMapper mapper;

	@InjectMocks
	private MemberService memberService;

	private final MemberCreator memberCreator = new MemberCreator();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		memberService = new MemberService(memberCreator, userIdGenerator, memberRepository, mapper);
	}

	@Test
	void successCreateMember() {
		// given
		String username = "홍길동";
		String userId = "123456789";
		MemberCreateRequestServiceDto request = new MemberCreateRequestServiceDto(username);

		given(memberRepository.existsByUsername(username)).willReturn(false);
		given(userIdGenerator.generateUserId()).willReturn(userId);

		ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
		MemberCreateResponseServiceDto expectedResponse =
			new MemberCreateResponseServiceDto(userId, username);
		given(mapper.toMemberCreateResponseServiceDto(any(), any())).willReturn(expectedResponse);

		// when
		MemberCreateResponseServiceDto response = memberService.createMember(request);

		// then
		verify(memberRepository).save(captor.capture());
		Member saved = captor.getValue();

		assertThat(response.userId()).isEqualTo(userId);
		assertThat(response.username()).isEqualTo(username);
		assertThat(saved.getUserId()).isEqualTo(userId);
		assertThat(saved.getUsername()).isEqualTo(username);
	}

	@Test
	void failUserIdGenerator() {
		// given
		String username = "홍길동";
		MemberCreateRequestServiceDto request = new MemberCreateRequestServiceDto(username);

		given(memberRepository.existsByUsername(username)).willReturn(false);
		given(userIdGenerator.generateUserId()).willThrow(new RuntimeException("userId 생성 실패"));

		// expect
		assertThatThrownBy(() -> memberService.createMember(request))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("userId 생성 실패");
	}

	@Test
	void failSaveMember() {
		// given
		String username = "홍길동";
		String userId = "123456789";
		MemberCreateRequestServiceDto request = new MemberCreateRequestServiceDto(username);

		given(memberRepository.existsByUsername(username)).willReturn(false);
		given(userIdGenerator.generateUserId()).willReturn(userId);
		willThrow(new RuntimeException("DB 저장 실패"))
			.given(memberRepository).save(any(Member.class));

		// except
		assertThatThrownBy(() -> memberService.createMember(request))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("DB 저장 실패");
	}
}