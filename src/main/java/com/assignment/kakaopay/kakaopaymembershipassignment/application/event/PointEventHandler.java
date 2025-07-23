package com.assignment.kakaopay.kakaopaymembershipassignment.application.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.assignment.kakaopay.kakaopaymembershipassignment.domain.point.Action;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.point.Point;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.point.PointHistory;
import com.assignment.kakaopay.kakaopaymembershipassignment.infrastructure.pointhistory.PointHistoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointEventHandler {
	private final PointHistoryRepository pointHistoryRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void saveRewardPointHistory(RewardPointEvent rewardPointEvent) {
		Point point = rewardPointEvent.point();
		Integer rewardPoint = rewardPointEvent.pointAmount();
		Long storeId = rewardPointEvent.storeId();

		PointHistory pointHistory = PointHistory.builder()
			.barcode(point.getBarcode())
			.category(point.getCategory())
			.point(rewardPoint)
			.totalPoint(point.getPoint())
			.action(Action.REWARD)
			.storeId(storeId)
			.build();

		pointHistoryRepository.save(pointHistory);
		log.debug("포인트 적립 내역 저장 성공");
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void saveUsePointHistory(UsePointEvent usePointEvent) {
		Point point = usePointEvent.point();
		Integer usePoint = usePointEvent.pointAmount();
		Long storeId = usePointEvent.storeId();

		PointHistory pointHistory = PointHistory.builder()
			.barcode(point.getBarcode())
			.category(point.getCategory())
			.point(usePoint)
			.totalPoint(point.getPoint())
			.action(Action.USE)
			.storeId(storeId)
			.build();

		pointHistoryRepository.save(pointHistory);
		log.debug("포인트 사용 내역 저장 성공");
	}
}