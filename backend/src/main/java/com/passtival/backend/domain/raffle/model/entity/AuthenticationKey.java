package com.passtival.backend.domain.raffle.model.entity;

import com.passtival.backend.global.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "authentication_key")
@NoArgsConstructor
public class AuthenticationKey extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "authentication_key", nullable = false, unique = true)
	private String key; // 인증 키 값

	public AuthenticationKey(String key) {
		this.key = key;
	}

}
