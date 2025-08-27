package com.passtival.backend.domain.lostfound.model.entity;

import com.passtival.backend.global.common.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admins")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Admin {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long adminId;

	@Column(name = "login_id", unique = true, nullable = false, length = 50)
	private String loginId;

	// BCrypt로 해싱된 값
	@Column(name = "auth_key", nullable = false, length = 255)
	private String authKey;

	// 항상 ADMIN
	@Column(nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	private Role role;

	public static Admin createAdmin(String loginId, String hashedAuthKey) {
		return Admin.builder()
			.loginId(loginId)
			.authKey(hashedAuthKey)
			.role(Role.ADMIN)
			.build();
	}
}
