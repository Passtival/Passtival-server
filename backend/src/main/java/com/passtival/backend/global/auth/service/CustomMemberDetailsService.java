package com.passtival.backend.global.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.passtival.backend.domain.matching.model.entity.Member;
import com.passtival.backend.domain.matching.repository.MemberRepository;
import com.passtival.backend.global.auth.model.CustomMemberDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomMemberDetailsService implements UserDetailsService {

	private final MemberRepository memberRepository;

	/**
	 * 소셜 ID로 사용자 정보 조회 (Spring Security 표준 인터페이스)
	 * @param socialId 소셜 로그인 ID (예: "kakao_1234567890")
	 * @return UserDetails 사용자 인증 정보
	 * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우
	 */
	@Override
	public UserDetails loadUserByUsername(String socialId) throws UsernameNotFoundException {
		try {
			// 1. 입력 검증
			validateSocialId(socialId);

			// 2. 회원 조회
			Member member = findMemberBySocialId(socialId);

			// 3. CustomMemberDetails 생성 및 반환
			return new CustomMemberDetails(member);

		} catch (UsernameNotFoundException e) {
			// UsernameNotFoundException은 그대로 전파 (Spring Security 표준)
			throw e;
		} catch (Exception e) {
			// 예상치 못한 예외는 UsernameNotFoundException으로 래핑
			log.error("사용자 조회 중 예외 발생: socialId={}, error={}", socialId, e.getMessage(), e);
			throw new UsernameNotFoundException("사용자 조회 중 오류가 발생했습니다: " + socialId, e);
		}
	}

	/**
	 * 소셜 ID 유효성 검증
	 * @param socialId 검증할 소셜 ID
	 * @throws UsernameNotFoundException 소셜 ID가 유효하지 않은 경우
	 */
	private void validateSocialId(String socialId) throws UsernameNotFoundException {
		try {
			if (socialId == null || socialId.trim().isEmpty()) {
				log.warn("소셜 ID가 비어있음");
				throw new UsernameNotFoundException("소셜 ID는 필수입니다");
			}

			// 소셜 ID 형식 검증 (provider_id 형태)
			if (!socialId.contains("_")) {
				log.warn("잘못된 소셜 ID 형식: {}", socialId);
				throw new UsernameNotFoundException("잘못된 소셜 ID 형식입니다: " + socialId);
			}

		} catch (UsernameNotFoundException e) {
			throw e;
		} catch (Exception e) {
			log.error("소셜 ID 검증 중 예외 발생: {}", e.getMessage(), e);
			throw new UsernameNotFoundException("소셜 ID 검증 중 오류가 발생했습니다", e);
		}
	}

	/**
	 * 소셜 ID로 회원 조회
	 * @param socialId 소셜 ID
	 * @return Member 회원 정보
	 * @throws UsernameNotFoundException 회원을 찾을 수 없는 경우
	 */
	private Member findMemberBySocialId(String socialId) throws UsernameNotFoundException {
		try {
			return memberRepository.findBySocialId(socialId.trim())
				.orElseThrow(() -> {
					log.warn("사용자를 찾을 수 없음: socialId={}", socialId);
					return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + socialId);
				});

		} catch (UsernameNotFoundException e) {
			throw e;
		} catch (Exception e) {
			log.error("회원 조회 중 예외 발생: socialId={}, error={}", socialId, e.getMessage(), e);
			throw new UsernameNotFoundException("회원 조회 중 오류가 발생했습니다: " + socialId, e);
		}
	}
}
