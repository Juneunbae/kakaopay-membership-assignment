package com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.mapper;

import org.mapstruct.Mapper;

import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.RewardPointResponseServiceDto;

@Mapper(componentModel = "spring")
public interface PointApplicationMapper {
	RewardPointResponseServiceDto toRewardPointResponseServiceDto(
		String barcode, Integer rewardPoint, Integer totalPoint
	);
}