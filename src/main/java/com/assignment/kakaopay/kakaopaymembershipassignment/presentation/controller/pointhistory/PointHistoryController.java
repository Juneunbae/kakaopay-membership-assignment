package com.assignment.kakaopay.kakaopaymembershipassignment.presentation.controller.pointhistory;

import java.util.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.pointhistory.GetPointHistoryRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.pointhistory.GetPointHistoryResponseServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.service.pointhistory.PointHistoryService;
import com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.mapper.pointhistory.PointHistoryPresentationMapper;
import com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.response.pointhistory.GetPointHistoryResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "포인트 사용 내역 조회")
@RequestMapping("/api/v1/point/histories")
public class PointHistoryController {
	private final PointHistoryService service;
	private final PointHistoryPresentationMapper mapper;

	@Operation(summary = "내역 조회", description = "포인트 사용/적립 내역을 조회하는 API")
	@GetMapping
	public ResponseEntity<List<GetPointHistoryResponseDto>> getPointHistories(
		@RequestParam Date startDate, @RequestParam Date endDate, @RequestParam String barcode
	) {
		GetPointHistoryRequestServiceDto serviceDto = mapper.toGetPointHistoryRequestServiceDto(
			startDate, endDate, barcode
		);

		List<GetPointHistoryResponseServiceDto> serviceResponse = service.getPointHistories(serviceDto);

		List<GetPointHistoryResponseDto> responseDto = mapper.toGetPointHistoryResponseDto(serviceResponse);

		return ResponseEntity.ok(responseDto);
	}
}