package com.assignment.kakaopay.kakaopaymembershipassignment.application.service.pointhistory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.mapper.pointhistory.PointHistoryApplicationMapper;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.pointhistory.GetPointHistoryRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.pointhistory.GetPointHistoryResponseServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.service.member.MemberService;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.pointhistory.PointHistory;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.pointhistory.service.PointHistoryGetter;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.GlobalException;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.pointhistory.PointHistoryErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointHistoryService {
	private final MemberService memberService;
	private final PointHistoryGetter pointHistoryGetter;
	private final PointHistoryApplicationMapper pointHistoryApplicationMapper;

	@Transactional
	public List<GetPointHistoryResponseServiceDto> getPointHistories(GetPointHistoryRequestServiceDto request) {
		memberService.existsByBarcode(request.barcode());

		if (request.startDate().after(request.endDate())) {
			throw new GlobalException(PointHistoryErrorCode.NOT_AFTER_START_DATE);
		}

		LocalDateTime startDate = converToLocalDateTime(request.startDate());
		LocalDateTime endDate = converToLocalDateTime(request.endDate()).plusDays(1);

		List<PointHistory> histories = pointHistoryGetter.getHistories(startDate, endDate);

		return histories.stream().map(
			pointHistoryApplicationMapper::toGetPointHistoryResponseServiceDto
		).toList();
	}

	private LocalDateTime converToLocalDateTime(Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
}