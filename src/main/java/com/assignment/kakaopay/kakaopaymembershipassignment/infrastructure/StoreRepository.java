package com.assignment.kakaopay.kakaopaymembershipassignment.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assignment.kakaopay.kakaopaymembershipassignment.domain.Store;

public interface StoreRepository extends JpaRepository<Store, Long> {
}