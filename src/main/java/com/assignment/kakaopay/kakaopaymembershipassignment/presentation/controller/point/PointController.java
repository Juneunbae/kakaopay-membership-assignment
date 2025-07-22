package com.assignment.kakaopay.kakaopaymembershipassignment.presentation.controller.point;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.point.RewardPointRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.point.RewardPointResponseServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.service.point.PointService;
import com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.mapper.point.PointPresentationMapper;
import com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.request.point.RewardPointRequestDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.response.point.RewardPointResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "포인트")
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PointController {
	private final PointService pointService;
	private final PointPresentationMapper mapper;

	@Operation(summary = "포인트 적립하기", description = "포인트 적립을 위한 API")
	@PostMapping("/rewards/point")
	public ResponseEntity<RewardPointResponseDto> rewardPoint(@RequestBody RewardPointRequestDto request) {
		RewardPointRequestServiceDto serviceDto = mapper.toRewardPointRequestServiceDto(
			request.storeId(), request.barcode(), request.rewardPoint()
		);

		RewardPointResponseServiceDto serviceResponse = pointService.rewardPoint(serviceDto);

		RewardPointResponseDto responseDto = mapper.toRewardPointResponseDto(
			serviceResponse.barcode(), serviceResponse.rewardPoint(), serviceResponse.totalPoint()
		);

		return ResponseEntity.ok(responseDto);
	}
}