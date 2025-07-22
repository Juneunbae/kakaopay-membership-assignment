package com.assignment.kakaopay.kakaopaymembershipassignment.infrastructure.store;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assignment.kakaopay.kakaopaymembershipassignment.domain.store.Store;

public interface StoreRepository extends JpaRepository<Store, Long> {
}