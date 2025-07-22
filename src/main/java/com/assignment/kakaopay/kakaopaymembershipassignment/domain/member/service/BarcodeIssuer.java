package com.assignment.kakaopay.kakaopaymembershipassignment.domain.member.service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.assignment.kakaopay.kakaopaymembershipassignment.domain.member.Member;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.GlobalException;
import com.assignment.kakaopay.kakaopaymembershipassignment.exception.member.MemberErrorCode;
import com.assignment.kakaopay.kakaopaymembershipassignment.infrastructure.member.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BarcodeIssuer {
	private final MemberRepository memberRepository;

	public String issue(Member member) {
		if (member.getBarcode() != null) {
			log.debug("재발급 요청으로 기존 바코드 반환 : {}", member.getBarcode());
			return member.getBarcode();
		}

		String barcode = generateBarcode();
		while (memberRepository.existsByBarcode(barcode)) {
			log.debug("중복 바코드, 재생성 시도");
			barcode = generateBarcode();
		}

		return barcode;
	}

	private String generateBarcode() {
		UUID uuid = UUID.randomUUID(); // 랜덤 UUID 발급
		String uuidSTring = uuid.toString();

		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256"); // 발급한 UUID에 SHA-256 알고리즘 적용
			byte[] hashBytes = digest.digest(uuidSTring.getBytes()); // 이진 데이터로 가져오기

			BigInteger bigInteger = new BigInteger(1, hashBytes); // 해시 바이트 배열을 BigInteger 양수 형태로 변환
			BigInteger modulus = BigInteger.TEN.pow(10); // 10^10 모듈러 연산으로 10자리 숫자 생성
			BigInteger barcodeNumber = bigInteger.mod(modulus); // bigInteger를 modulus로 나눈 나머지 값 생성

			return String.format("%010d", barcodeNumber);
		} catch (NoSuchAlgorithmException e) {
			throw new GlobalException(MemberErrorCode.NO_SUCH_SHA_256);
		}
	}
}