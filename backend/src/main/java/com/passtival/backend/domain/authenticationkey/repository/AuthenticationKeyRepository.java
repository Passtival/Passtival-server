package com.passtival.backend.domain.authenticationkey.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.passtival.backend.domain.authenticationkey.model.AuthenticationKey;

@Repository
public interface AuthenticationKeyRepository extends JpaRepository<AuthenticationKey, Long> {
	/**
	 * 인증 키를 ID 순으로 오름차순 정렬하여 첫 번째 항목을 반환합니다.
	 * @return 첫 번째 인증 키
	 */
	AuthenticationKey findFirstByOrderByIdAsc();

}
