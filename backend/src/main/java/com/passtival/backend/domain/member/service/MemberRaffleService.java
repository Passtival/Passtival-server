package com.passtival.backend.domain.member.service;

import org.springframework.stereotype.Service;

import com.passtival.backend.domain.member.model.entity.Member;
import com.passtival.backend.domain.member.repository.MemberRepository;
import com.passtival.backend.domain.raffle.model.response.MemberRaffleProfileResponse;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberRaffleService {

	private final MemberRepository memberRepository;

	// 회원 정보 조회
	public MemberRaffleProfileResponse getMemberRaffleProfile(Long memberId) {

		Member member = memberRepository.findByMemberId(memberId)
			.orElseThrow(() -> new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND));

		return new MemberRaffleProfileResponse(member.getLevel(), member.getName(), member.getStudentId());
	}
}
