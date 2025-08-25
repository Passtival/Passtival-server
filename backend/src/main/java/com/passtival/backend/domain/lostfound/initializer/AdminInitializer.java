package com.passtival.backend.domain.lostfound.initializer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.passtival.backend.domain.lostfound.model.entity.Admin;
import com.passtival.backend.domain.lostfound.repository.AdminRepository;

import lombok.RequiredArgsConstructor;

//빈으로 등록 -> 애플리케이션 시작 시 이 클래스
@Component
@RequiredArgsConstructor
//초기화 작업들이 모두 끝난 후 가장 마지막에 실행 (혹시 모를 충돌 방지)
@Order(Integer.MAX_VALUE)
//implements ApplicationRunner -> 메소드를 딱 한 번 자동으로 호출
public class AdminInitializer implements ApplicationRunner {

	private final AdminRepository adminRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${admin.initial.login-id}")
	private String initialLoginId;

	@Value("${admin.initial.auth-key}")
	private String initialAuthKey;

	@Override
	public void run(ApplicationArguments args) {
		initializeDefaultAdmin();
	}

	private void initializeDefaultAdmin() {
		if (adminRepository.existsByLoginId(initialLoginId)) {
			//이미 존재
			return;
		}

		String hashedAuthKey = passwordEncoder.encode(initialAuthKey);

		Admin admin = Admin.createAdmin(initialLoginId, hashedAuthKey);
		adminRepository.save(admin);

	}
}
