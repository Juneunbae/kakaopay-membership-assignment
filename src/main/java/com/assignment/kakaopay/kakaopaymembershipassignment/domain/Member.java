package com.assignment.kakaopay.kakaopaymembershipassignment.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@DynamicInsert
@DynamicUpdate
@Table(name = "k_member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(
		nullable = false,
		unique = true,
		length = 9
	)
	private String userId;

	@Column(
		nullable = false,
		length = 50
	)
	private String username;

	@Column(
		nullable = false,
		unique = true,
		length = 10
	)
	private String barcode;

	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime createdAt;
}