package com.passtival.backend.domain.authenticationkey;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.passtival.backend.domain.authenticationkey.repository.AuthenticationKeyRepository;
import com.passtival.backend.domain.authenticationkey.service.AuthenticationKeyImportService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class AuthenticationKeySeedRunner implements CommandLineRunner {

	private final AuthenticationKeyRepository authenticationKeyRepository;
	private final AuthenticationKeyImportService authenticationKeyImportService;

	@Override
	public void run(String... args) throws Exception {
		long count = authenticationKeyRepository.count();
		if (count > 0) {
			log.info("인증키 이미 존재 (count: {})", count);
			return;
		}
		log.info("인증키가 존재하지 않음. 인증키를 엑셀에서 불러옵니다.");
		authenticationKeyImportService.importXlsx();
		long after = authenticationKeyRepository.count();
		log.info("인증키 불러오기 완료 (count: {})", after);
	}
}
