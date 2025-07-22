package com.assignment.kakaopay.kakaopaymembershipassignment.presentation.controller.member;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.member.IssueBarcodeRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.member.MemberCreateRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.member.IssueBarcodeResponseServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.member.MemberCreateResponseServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.service.member.MemberService;
import com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.mapper.member.MemberPresentationMapper;
import com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.request.member.IssueBarcodeRequestDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.request.member.MemberCreateRequestDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.response.member.IssueBarcodeResponseDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.response.member.MemberCreateResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "멤버")
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {
	private final MemberService memberService;
	private final MemberPresentationMapper mapper;

	@Operation(summary = "멤버 가입하기", description = "멤버 가입을 위한 API")
	@PostMapping
	public ResponseEntity<MemberCreateResponseDto> createMember(@RequestBody MemberCreateRequestDto request) {
		MemberCreateRequestServiceDto serviceDto = mapper.toMemberCreateRequestServiceDto(
			request.username()
		);

		MemberCreateResponseServiceDto serviceResponse = memberService.createMember(serviceDto);

		MemberCreateResponseDto responseDto = mapper.toMemberCreateResponseDto(
			serviceResponse.userId(), serviceResponse.username()
		);

		return ResponseEntity.ok(responseDto);
	}

	@Operation(summary = "통합 바코드 발급하기", description = "가입된 유저들에게 통합 바코드 발급하는 API")
	@PostMapping("/issues/barcode")
	public ResponseEntity<IssueBarcodeResponseDto> issueBarcode(@RequestBody IssueBarcodeRequestDto request) {
		IssueBarcodeRequestServiceDto serviceDto = mapper.toIssueBarcodeRequestServiceDto(
			request.userId()
		);

		IssueBarcodeResponseServiceDto serviceResponse = memberService.issueBarcode(serviceDto);

		IssueBarcodeResponseDto responseDto = mapper.toIssueBarcodeResponseDto(
			serviceResponse.id(), serviceResponse.userId(), serviceResponse.username(), serviceResponse.barcode()
		);

		return ResponseEntity.ok(responseDto);
	}
}