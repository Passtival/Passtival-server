package com.passtival.backend.domain.member.service;

import org.springframework.stereotype.Service;

import com.passtival.backend.domain.authenticationkey.model.AuthenticationKey;
import com.passtival.backend.domain.authenticationkey.repository.AuthenticationKeyRepository;
import com.passtival.backend.domain.member.model.entity.Member;
import com.passtival.backend.domain.member.model.entity.request.LevelUpRequest;
import com.passtival.backend.domain.member.repository.MemberRepository;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final AuthenticationKeyRepository authenticationKeyRepository;

	public void levelUp(Long memberId, LevelUpRequest request) {

		Member member = memberRepository.findByMemberId(memberId)
			.orElseThrow(() -> new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND));

		AuthenticationKey authenticationKey = authenticationKeyRepository.findFirstByOrderByIdAsc();

		// 인증키 존재 여부 확인
		if (authenticationKey == null) {
			throw new BaseException(BaseResponseStatus.NOT_FOUND_AUTH_KEY);
		}
		// 인증키 검증
		if (!authenticationKey.getAuthenticationKey().equals(request.getAuthenticationKey())) {
			throw new BaseException(BaseResponseStatus.INVALID_AUTH_KEY);
		}

		// 인증키 레벨과 요청 레벨 검증
		if (!authenticationKey.getLevel().equals(request.getLevel())) {
			throw new BaseException(BaseResponseStatus.INVALID_LEVEL);
		}

		// level = 3 프리미엄 응모권 지급
		if (request.getLevel() == 3) {
			member.setPremiumRaffle(true);
		}

		// 레벨업 처리
		member.updateProfile(request.getName(), request.getStudentId(), request.getLevel());
		memberRepository.save(member);

		// 인증키 삭제
		authenticationKeyRepository.delete(authenticationKey);
	}
}
