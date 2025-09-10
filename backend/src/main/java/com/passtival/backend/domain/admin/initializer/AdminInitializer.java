package com.passtival.backend.domain.admin.initializer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.passtival.backend.domain.admin.model.entity.Admin;
import com.passtival.backend.domain.admin.repository.AdminRepository;

import lombok.RequiredArgsConstructor;

//빈으로 등록 -> 애플리케이션 시작 시 이 클래스
@Component
@RequiredArgsConstructor
//초기화 작업들이 모두 끝난 후 가장 마지막에 실행 (혹시 모를 충돌 방지)
@Order(Integer.MAX_VALUE)
//implements ApplicationRunner -> 메소드를 딱 한 번 자동으로 호출
public class AdminInitializer implements ApplicationRunner {

	private final AdminRepository adminRepository;

	@Value("${admin.username}")
	private String username;

	@Value("${admin.password}")
	private String password;

	@Override
	public void run(ApplicationArguments args) {
		initializeDefaultAdmin();
	}

	private void initializeDefaultAdmin() {

		// 이미 존재하는지 확인
		Admin admin = adminRepository.findByLoginId(username).orElse(null);

		// 존재하면 비밀번호 업데이트 후 종료
		if (admin != null) {
			admin.updatePassword(password);
			adminRepository.save(admin);
			return;
		}
		// 존재하지 않으면 새로 생성
		admin = Admin.createAdmin(username, password);
		adminRepository.save(admin);

	}
}
