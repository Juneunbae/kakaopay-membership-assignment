package com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.mapper;

import org.mapstruct.Mapper;

import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.MemberCreateRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.response.MemberCreateResponseDto;

@Mapper(componentModel = "spring")
public interface MemberPresentationMapper {
	MemberCreateRequestServiceDto toMemberCreateRequestServiceDto(String username);

	MemberCreateResponseDto toMemberCreateResponseDto(String userId, String username);
}