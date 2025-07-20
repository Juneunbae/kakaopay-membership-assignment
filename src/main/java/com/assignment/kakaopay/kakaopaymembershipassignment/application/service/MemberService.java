package com.assignment.kakaopay.kakaopaymembershipassignment.application.service;

import org.springframework.stereotype.Service;

import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.mapper.MemberApplicationMapper;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.IssueBarcodeRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.MemberCreateRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.IssueBarcodeResponseServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.MemberCreateResponseServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.member.Member;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.member.generator.UserIdGenerator;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.member.service.BarcodeIssuer;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.member.service.MemberCreator;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.GlobalException;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.MemberErrorCode;
import com.assignment.kakaopay.kakaopaymembershipassignment.infrastructure.MemberRepository;

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

		return mapper.toIssueBarcodeResponseServiceDto(
			member.getId(), member.getUserId(), member.getUsername(), barcode
		);
	}
}