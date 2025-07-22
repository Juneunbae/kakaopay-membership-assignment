package com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.mapper.member;

import org.mapstruct.Mapper;

import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.member.IssueBarcodeResponseServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.member.MemberCreateResponseServiceDto;

@Mapper(componentModel = "spring")
public interface MemberApplicationMapper {
	MemberCreateResponseServiceDto toMemberCreateResponseServiceDto(String userId, String username);

	IssueBarcodeResponseServiceDto toIssueBarcodeResponseServiceDto(
		Long id, String userId, String username, String barcode
	);
}