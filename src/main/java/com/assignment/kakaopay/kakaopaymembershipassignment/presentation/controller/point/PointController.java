package com.assignment.kakaopay.kakaopaymembershipassignment.presentation.controller.point;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.point.RewardPointRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.request.point.UsePointRequestServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.point.RewardPointResponseServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.dto.response.point.UsePointResponseServiceDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.application.service.point.PointService;
import com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.mapper.point.PointPresentationMapper;
import com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.request.point.RewardPointRequestDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.request.point.UsePointRequestDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.response.point.RewardPointResponseDto;
import com.assignment.kakaopay.kakaopaymembershipassignment.presentation.dto.response.point.UsePointResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "포인트")
@RequestMapping("/api/v1/points")
@RequiredArgsConstructor
public class PointController {
	private final PointService pointService;
	private final PointPresentationMapper mapper;

	@Operation(summary = "포인트 적립하기", description = "포인트 적립을 위한 API")
	@PostMapping("/reward")
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

	@Operation(summary = "포인트 사용하기", description = "포인트 사용 API")
	@PostMapping("/usage")
	public ResponseEntity<UsePointResponseDto> usePoint(@RequestBody UsePointRequestDto request) {
		UsePointRequestServiceDto serviceDto = mapper.toUsePointRequestServiceDto(
			request.storeId(), request.barcode(), request.usePoint()
		);

		UsePointResponseServiceDto serviceRes = pointService.usePoint(serviceDto);

		UsePointResponseDto responseDto = mapper.toUsePointResponseDto(
			serviceRes.storeId(), serviceRes.barcode(), serviceRes.usePoint(), serviceRes.remainPoint()
		);

		return ResponseEntity.ok(responseDto);
	}
}