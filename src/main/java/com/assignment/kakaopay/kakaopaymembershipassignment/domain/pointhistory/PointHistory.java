package com.assignment.kakaopay.kakaopaymembershipassignment.domain.pointhistory;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;

import com.assignment.kakaopay.kakaopaymembershipassignment.domain.category.Category;
import com.assignment.kakaopay.kakaopaymembershipassignment.domain.point.Action;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@DynamicInsert
@DynamicUpdate
@Table(name = "k_point_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(
		nullable = false,
		length = 10
	)
	private String barcode;

	private Integer point;

	private Integer totalPoint;

	@Column(
		nullable = false,
		length = 5
	)
	@Enumerated(EnumType.STRING)
	private Action action;

	private Long storeId;

	@Column(
		nullable = false,
		length = 50
	)
	private String storeName;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Category category;

	@CreatedDate
	@Column(
		nullable = false
	)
	private LocalDateTime createdAt;
}