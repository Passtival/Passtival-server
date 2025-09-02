package com.passtival.backend.global.security.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.passtival.backend.domain.member.model.entity.Member;
import com.passtival.backend.domain.member.repository.MemberRepository;
import com.passtival.backend.global.exception.BaseException;
import com.passtival.backend.global.security.model.CustomMemberDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomMemberDetailsService implements UserDetailsService {

	private final MemberRepository memberRepository;

	/**
	 * 소셜 ID로 사용자 정보 조회 (Spring Security 표준 인터페이스)
	 * @param socialId 소셜 로그인 ID (예: "kakao_1234567890")
	 * @return UserDetails 사용자 인증 정보
	 * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우
	 */
	@Override
	public UserDetails loadUserByUsername(String socialId) {
		// 1. 입력 검증
		validateSocialId(socialId);
		// 2. 회원 조회
		Member member = findMemberBySocialId(socialId);
		// 3. CustomMemberDetails 생성 및 반환
		return new CustomMemberDetails(member);
	}

	/**
	 * 소셜 ID 유효성 검증
	 * @param socialId 검증할 소셜 ID
	 * @throws BaseException 소셜 ID가 null이거나 형식이 잘못된 경우
	 */
	private void validateSocialId(String socialId) {
		if (socialId == null || socialId.trim().isEmpty()) {
			// BaseException 대신 BadCredentialsException 사용
			throw new BadCredentialsException("소셜 ID가 비어있습니다.");
		}

		if (!socialId.contains("_")) {
			// BaseException 대신 BadCredentialsException 사용
			throw new BadCredentialsException("잘못된 소셜 ID 형식입니다: " + socialId);
		}
	}

	/**
	 * 소셜 ID로 회원 조회
	 * @param socialId 소셜 ID
	 * @return Member 회원 정보
	 * @throws BaseException 회원을 찾을 수 없는 경우
	 */
	private Member findMemberBySocialId(String socialId) {
		return memberRepository.findBySocialId(socialId.trim())
			.orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다: " + socialId));
	}
}
