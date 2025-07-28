package com.assignment.kakaopay.kakaopaymembershipassignment.application.service.member;

import org.springframework.stereotype.Service;

import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.mapper.member.MemberApplicationMapper;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.member.IssueBarcodeRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.member.MemberCreateRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.member.IssueBarcodeResponseServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.member.MemberCreateResponseServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.member.Member;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.member.generator.UserIdGenerator;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.member.service.BarcodeIssuer;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.member.service.BarcodeToRedisSaver;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.member.service.MemberCreator;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.GlobalException;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.member.MemberErrorCode;
import com.assignment.kakaopay.kakaopaymembershipassignment.infrastructure.member.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
	private final BarcodeIssuer barcodeIssuer;
	private final MemberCreator memberCreator;
	private final UserIdGenerator userIdGenerator;
	private final MemberRepository memberRepository;
	private final MemberApplicationMapper mapper;
	private final BarcodeToRedisSaver barcodeToRedisSaver;

	@Transactional
	public MemberCreateResponseServiceDto createMember(MemberCreateRequestServiceDto request) {
		Boolean checkUsername = memberRepository.existsByUsername(request.username());
		if (checkUsername) {
			throw new GlobalException(MemberErrorCode.ALREADY_EXISTS);
		}

		String userId = userIdGenerator.generateUserId();
		Member member = memberCreator.create(userId, request.username());
		memberRepository.save(member);
		log.debug("회원 : {} 생성 완료 - UserID : {}", member.getUserId(), member.getUsername());

		return mapper.toMemberCreateResponseServiceDto(member.getUserId(), member.getUsername());
	}

	@Transactional
	public IssueBarcodeResponseServiceDto issueBarcode(IssueBarcodeRequestServiceDto request) {
		Member member = memberRepository.findByUserId(request.userId())
			.orElseThrow(() -> new GlobalException(MemberErrorCode.NOT_FOUND));

		String barcode = barcodeIssuer.issue(member);

		member.issueBarcode(barcode);
		memberRepository.save(member);
		log.debug("회원 : {} - 바코드 : {} 발급 완료", member.getUserId(), barcode);

		barcodeToRedisSaver.save(barcode, member.getId());

		return mapper.toIssueBarcodeResponseServiceDto(
			member.getId(), member.getUserId(), member.getUsername(), barcode
		);
	}

	public void existsByBarcode(String barcode) {
		if (!memberRepository.existsByBarcode(barcode)) {
			throw new GlobalException(MemberErrorCode.NOT_FOUND);
		}
	}
}