package com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.mapper.pointhistory;

import org.mapstruct.Mapper;

import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.pointhistory.GetPointHistoryResponseServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.pointhistory.PointHistory;

@Mapper(componentModel = "spring")
public interface PointHistoryApplicationMapper {
	GetPointHistoryResponseServiceDto toGetPointHistoryResponseServiceDto(PointHistory pointHistory);
}