package com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.mapper;

import org.mapstruct.Mapper;

import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.IssueBarcodeRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.MemberCreateRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.response.IssueBarcodeResponseDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.response.MemberCreateResponseDto;

@Mapper(componentModel = "spring")
public interface MemberPresentationMapper {
	MemberCreateRequestServiceDto toMemberCreateRequestServiceDto(String username);

	MemberCreateResponseDto toMemberCreateResponseDto(String userId, String username);

	IssueBarcodeRequestServiceDto toIssueBarcodeRequestServiceDto(String userId);

	IssueBarcodeResponseDto toIssueBarcodeResponseDto(Long id, String userId, String username, String barcode);
}