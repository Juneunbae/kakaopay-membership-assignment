package com.assignment.kakaopay.kakaopaymembershipassignment.domain.point;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.assignment.kakaopay.kakaopaymembershipassignment.domain.category.Category;

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
@Table(name = "k_member_point")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(
		nullable = false,
		length = 10
	)
	private String barcode;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Category category;

	private Integer point;

	@Column(
		nullable = false
	)
	@CreatedDate
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime updatedAt;

	public void updatePoint(Integer point) {
		this.point = point;
	}
}