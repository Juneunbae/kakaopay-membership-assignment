package com.assignment.kakaopay.kakaopaymembershipassignment.application.service.store;

import org.springframework.stereotype.Service;

import com.assignment.kakaopay.kakaopaymembershipassignment.domain.store.Store;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.GlobalException;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.store.StoreErrorCode;
import com.assignment.kakaopay.kakaopaymembershipassignment.infrastructure.store.StoreRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {
	private final StoreRepository storeRepository;

	public Store findById(Long id) {
		return storeRepository.findById(id)
			.orElseThrow(() -> new GlobalException(StoreErrorCode.NOT_FOUND));
	}
}