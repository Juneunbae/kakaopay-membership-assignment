package com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.mapper.point;

import org.mapstruct.Mapper;

import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.point.RewardPointRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.point.UsePointRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.response.point.RewardPointResponseDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.response.point.UsePointResponseDto;

@Mapper(componentModel = "spring")
public interface PointPresentationMapper {
	RewardPointRequestServiceDto toRewardPointRequestServiceDto(Long storeId, String barcode, Integer rewardPoint);

	RewardPointResponseDto toRewardPointResponseDto(String barcode, Integer rewardPoint, Integer totalPoint);

	UsePointRequestServiceDto toUsePointRequestServiceDto(Long storeId, String barcode, Integer usePoint);

	UsePointResponseDto toUsePointResponseDto(Long storeId, String barcode, Integer usePoint, Integer remainPoint);
}