package com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.mapper.pointhistory;

import java.util.Date;
import java.util.List;

import org.mapstruct.Mapper;

import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.pointhistory.GetPointHistoryRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.pointhistory.GetPointHistoryResponseServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.response.pointhistory.GetPointHistoryResponseDto;

@Mapper(componentModel = "spring")
public interface PointHistoryPresentationMapper {
	GetPointHistoryRequestServiceDto toGetPointHistoryRequestServiceDto(Date startDate, Date endDate, String barcode);

	List<GetPointHistoryResponseDto> toGetPointHistoryResponseDto(List<GetPointHistoryResponseServiceDto> dto);
}