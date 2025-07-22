package com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.mapper.point;

import org.mapstruct.Mapper;

import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.point.RewardPointResponseServiceDto;

@Mapper(componentModel = "spring")
public interface PointApplicationMapper {
	RewardPointResponseServiceDto toRewardPointResponseServiceDto(
		String barcode, Integer rewardPoint, Integer totalPoint
	);
}