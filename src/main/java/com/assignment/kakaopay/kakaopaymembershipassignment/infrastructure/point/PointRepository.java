package com.assignment.kakaopay.kakaopaymembershipassignment.infrastructure.point;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assignment.kakaopay.kakaopaymembershipassignment.domain.category.Category;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.point.Point;

public interface PointRepository extends JpaRepository<Point, Long> {
	Point findByBarcodeAndCategory(String barcode, Category category);
}