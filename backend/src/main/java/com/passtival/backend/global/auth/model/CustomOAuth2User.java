package com.passtival.backend.global.auth.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.passtival.backend.domain.matching.model.enums.Gender;

public class CustomOAuth2User implements OAuth2User {

	private final AuthUserDto authUserDto;

	public CustomOAuth2User(AuthUserDto authUserDto) {
		this.authUserDto = authUserDto;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return null;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		Collection<GrantedAuthority> collection = new ArrayList<>();
		collection.add(new GrantedAuthority() {
			@Override
			public String getAuthority() {
				return authUserDto.getRole();
			}
		});
		return collection;
	}

	@Override
	public String getName() {
		return authUserDto.getName();
	}

	public String getGender() {
		Gender gender = authUserDto.getGender();
		return gender != null ? gender.name() : null;
	}

	public String getSocialId() {
		return authUserDto.getSocialId();
	}

	public String getPhoneNumber() {
		return authUserDto.getPhoneNumber();
	}

	public Long getMemberId() {
		return authUserDto.getUserId();
	}

}
