package com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.mapper.member;

import org.mapstruct.Mapper;

import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.member.IssueBarcodeRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.member.MemberCreateRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.response.member.IssueBarcodeResponseDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.response.member.MemberCreateResponseDto;

@Mapper(componentModel = "spring")
public interface MemberPresentationMapper {
	MemberCreateRequestServiceDto toMemberCreateRequestServiceDto(String username);

	MemberCreateResponseDto toMemberCreateResponseDto(String userId, String username);

	IssueBarcodeRequestServiceDto toIssueBarcodeRequestServiceDto(String userId);

	IssueBarcodeResponseDto toIssueBarcodeResponseDto(Long id, String userId, String username, String barcode);
}