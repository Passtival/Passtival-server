package com.passtival.backend.global.auth.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.passtival.backend.domain.matching.model.entity.Member;
import com.passtival.backend.domain.matching.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomMemberDetailsService implements UserDetailsService {

	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String socialId) throws UsernameNotFoundException {

		// username은 전화번호로 사용
		Member member = memberRepository.findBySocialId(socialId)
			.orElseThrow(() -> {
				log.warn("사용자를 찾을 수 없음: phoneNumber = {}", socialId);
				return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + socialId);
			});

		return new CustomMemberDetails(member);
	}
}
