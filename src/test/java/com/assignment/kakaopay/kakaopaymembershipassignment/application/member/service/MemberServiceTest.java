package com.assignment.kakaopay.kakaopaymembershipassignment.application.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.mapper.member.MemberApplicationMapper;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.member.IssueBarcodeRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.member.MemberCreateRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.member.IssueBarcodeResponseServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.member.MemberCreateResponseServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.service.member.MemberService;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.member.Member;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.member.generator.UserIdGenerator;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.member.service.BarcodeIssuer;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.member.service.BarcodeToRedisSaver;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.member.service.MemberCreator;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.GlobalException;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.member.MemberErrorCode;
import com.assignment.kakaopay.kakaopaymembershipassignment.infrastructure.member.MemberRepository;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
	@Mock
	private MemberRepository memberRepository;

	@Mock
	private UserIdGenerator userIdGenerator;

	@Mock
	private MemberApplicationMapper mapper;

	@Mock
	private BarcodeIssuer barcodeIssuer;

	@Mock
	private BarcodeToRedisSaver barcodeToRedisSaver;

	@InjectMocks
	private MemberService memberService;

	private final MemberCreator memberCreator = new MemberCreator();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		memberService = new MemberService(barcodeIssuer, memberCreator, userIdGenerator, memberRepository, mapper,
			barcodeToRedisSaver);
	}

	@Test
	@DisplayName("멤버 생성 성공 테스트")
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
	@DisplayName("멤버 생성 실패 테스트 - userId 생성 실패")
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
	@DisplayName("멤버 생성 실패 테스트 - DB 저장 실패")
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

	@Test
	@DisplayName("바코드 발급 성공 테스트")
	void successIssueBarcode() {
		//given
		String userId = "123456789";
		String username = "홍길동";
		String barcode = "987654321";
		Long memberId = 1L;

		IssueBarcodeRequestServiceDto request = new IssueBarcodeRequestServiceDto(userId);

		Member member = Member.builder()
			.id(memberId)
			.userId(userId)
			.username(username)
			.build();

		IssueBarcodeResponseServiceDto expectedResponse = new IssueBarcodeResponseServiceDto(
			member.getId(), member.getUserId(), member.getUsername(), barcode
		);

		given(memberRepository.findByUserId(userId)).willReturn(Optional.of(member));
		given(barcodeIssuer.issue(member)).willReturn(barcode);
		given(memberRepository.save(member)).willReturn(member);
		given(mapper.toIssueBarcodeResponseServiceDto(memberId, userId, username, barcode)).willReturn(
			expectedResponse);

		// when
		IssueBarcodeResponseServiceDto result = memberService.issueBarcode(request);

		// then
		assertThat(result).isNotNull();
		assertThat(result.barcode()).isEqualTo(barcode);
		assertThat(result.userId()).isEqualTo(userId);
		assertThat(result.username()).isEqualTo(username);

		verify(memberRepository).findByUserId(userId);
		verify(barcodeIssuer).issue(member);
		verify(memberRepository).save(member);
		verify(barcodeToRedisSaver).save(barcode, memberId);
		verify(mapper).toIssueBarcodeResponseServiceDto(memberId, userId, username, barcode);
	}

	@Test
	@DisplayName("바코드 발급 실패 테스트 - 존재하지 않는 회원")
	void failNotFoundMember() {
		// given
		String userId = "999999999";
		IssueBarcodeRequestServiceDto request = new IssueBarcodeRequestServiceDto(userId);

		given(memberRepository.findByUserId(userId)).willReturn(Optional.empty());

		// when & then
		GlobalException exception = assertThrows(GlobalException.class, () -> {
			memberService.issueBarcode(request);
		});

		assertThat(exception.getBaseErrorCode()).isEqualTo(MemberErrorCode.NOT_FOUND);

		verify(memberRepository).findByUserId(userId);
		verifyNoMoreInteractions(barcodeIssuer, memberRepository, mapper);
	}

	@Test
	@DisplayName("바코드 발급 실패 테스트 - 이미 발급된 바코드가 있으면 기존 바코드 반환")
	void alreadyReturnBarcode() {
		// given
		String userId = "123456789";
		String existingBarcode = "1234567890";
		Long memberId = 1L;
		String username = "홍길동";

		Member member = Member.builder()
			.id(memberId)
			.userId(userId)
			.username(username)
			.barcode(existingBarcode)
			.build();

		given(memberRepository.findByUserId(userId)).willReturn(Optional.of(member));
		given(barcodeIssuer.issue(member)).willReturn(existingBarcode);

		IssueBarcodeResponseServiceDto responseDto = new IssueBarcodeResponseServiceDto(
			memberId, userId, username, existingBarcode
		);

		given(mapper.toIssueBarcodeResponseServiceDto(memberId, userId, username, existingBarcode)).willReturn(
			responseDto);

		IssueBarcodeRequestServiceDto request = new IssueBarcodeRequestServiceDto(userId);

		// when
		IssueBarcodeResponseServiceDto result = memberService.issueBarcode(request);

		// then
		assertThat(result).isNotNull();
		assertThat(result.barcode()).isEqualTo(existingBarcode);

		verify(memberRepository).findByUserId(userId);
		verify(mapper).toIssueBarcodeResponseServiceDto(memberId, userId, username, existingBarcode);
	}
}