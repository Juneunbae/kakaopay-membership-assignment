package com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.mapper;

import org.mapstruct.Mapper;

import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.RewardPointRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.response.RewardPointResponseDto;

@Mapper(componentModel = "spring")
public interface PointPresentationMapper {
	RewardPointRequestServiceDto toRewardPointRequestServiceDto(Long storeId, String barcode, Integer rewardPoint);

	RewardPointResponseDto toRewardPointResponseDto(String barcode, Integer rewardPoint, Integer totalPoint);
}