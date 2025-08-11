package com.passtival.backend.global.auth.security;

import com.passtival.backend.domain.member.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomMemberDetails implements UserDetails {

    private final Member member;

    public CustomMemberDetails(Member member) {
        this.member = member;
    }

    // 우리 프로젝트에서 사용할 메서드들
    public Long getMemberId() {
        return member.getMemberId();
    }

    public String getName() {
        return member.getName();
    }

    public String getPhoneNumber() {
        return member.getPhoneNumber();
    }

    // UserDetails 인터페이스 구현
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Role enum을 GrantedAuthority로 변환
        return Collections.singletonList(
                new SimpleGrantedAuthority(member.getRole().name())
        );
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        // 전화번호를 username으로 사용 (고유 식별자)
        return member.getPhoneNumber();
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