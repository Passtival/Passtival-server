package com.passtival.backend.domain.authenticationkey.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(value = {})
public class AuthenticationKey {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// @Column(nullable = false, unique = true, length = 5)
	@Column(nullable = false, unique = false, length = 5) // 대용량 데이터 삽입 테스트용으로 unique 제약 조건 제거
	private String authenticationKey; // 인증 키 값

	@Setter
	private Integer level;

	public AuthenticationKey(String authenticationKey, Integer level) {
		this.authenticationKey = authenticationKey;
		this.level = level;
	}
}
