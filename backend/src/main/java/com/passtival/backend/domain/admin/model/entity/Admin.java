package com.passtival.backend.domain.admin.model.entity;

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
@Table(name = "admin")
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

	@Column(name = "password", nullable = false, length = 255)
	private String password;

	// 항상 ADMIN
	@Column(nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	private Role role;

	public static Admin createAdmin(String loginId, String password) {
		return Admin.builder()
			.loginId(loginId)
			.password(password)
			.role(Role.ADMIN)
			.build();
	}

	public void updatePassword(String newPassword) {
		this.password = newPassword;
	}
}
