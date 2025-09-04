package com.passtival.backend.global.security.model;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.passtival.backend.domain.member.model.entity.Member;

public class CustomMemberDetails implements UserDetails {

	private final Long memberId;
	private final String role;
	private final String socialId;           // 소셜 로그인 ID

	public CustomMemberDetails(Member member) {
		this.memberId = member.getMemberId();
		this.role = "ROLE_" + member.getRole();
		this.socialId = member.getSocialId();
	}

	public CustomMemberDetails(Long memberId, String role) {
		this.memberId = memberId;
		this.role = role;
		this.socialId = null;
	}

	public Long getMemberId() {
		return this.memberId;
	}

	// UserDetails 인터페이스 구현
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority(this.role));
	}

	@Override
	public String getPassword() {
		return null; // 소셜 로그인에서는 비밀번호 없음
	}

	@Override
	public String getUsername() {
		// UserDetails의 username은 고유 식별자 역할을 하므로 memberId를 반환
		return String.valueOf(this.memberId);
	}

	/**
	 *  계정 상태 관련 메서드들 인터페이스에 정의된 모든 추상 메소드를 구현하지 않으면 자바 컴파일러가 에러를 발생시킵니다.
	 * (우리 프로젝트에서는 모두 true)
	 */
	@Override
	public boolean isAccountNonLocked() {
		return true; // 계정 잠금 기능 없음
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true; // 비밀번호 만료 기능 없음
	}

	@Override
	public boolean isEnabled() {
		return true; // 계정 비활성화 기능 없음
	}

	@Override
	public boolean isAccountNonExpired() {
		return true; // 계정 만료 기능 없음
	}

}