package com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.mapper;

import org.mapstruct.Mapper;

import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.MemberCreateResponseServiceDto;

@Mapper(componentModel = "spring")
public interface MemberApplicationMapper {
	MemberCreateResponseServiceDto toMemberCreateResponseServiceDto(String userId, String username);
}