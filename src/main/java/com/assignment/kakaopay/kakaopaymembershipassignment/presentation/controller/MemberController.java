package com.assignment.kakaopay.kakaopaymembershipassignment.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.MemberCreateRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.MemberCreateResponseServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.service.MemberService;
import com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.mapper.MemberPresentationMapper;
import com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.request.MemberCreateRequestDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.response.MemberCreateResponseDto;

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
}