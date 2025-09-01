package com.passtival.backend.global.security.model;

import java.util.Map;

import com.passtival.backend.global.common.enums.Role;

public class KakaoResponse implements Oauth2Response {

	private final Map<String, Object> attribute;

	public KakaoResponse(Map<String, Object> attribute) {
		this.attribute = attribute;
	}

	@Override
	public String getProvider() {
		return "kakao";
	}

	@Override
	public String getProviderId() {
		return attribute.get("id").toString();
	}

	@Override
	public String getSocialId() {
		return getProvider() + "_" + getProviderId(); // 소셜 ID는 provider와 providerId를 조합하여 생성
	}

	@Override
	public String getName() {
		return getNickname(); // 카카오에서는 닉네임을 이름으로 사용
	}

	@Override
	public String getNickname() {
		Map<String, Object> kakaoAccount = (Map<String, Object>)attribute.get("kakao_account");
		if (kakaoAccount == null)
			return null;
		Map<String, Object> profile = (Map<String, Object>)kakaoAccount.get("profile");
		if (profile == null)
			return null;
		Object nickname = profile.get("nickname");
		return nickname != null ? nickname.toString() : null;
	}

	@Override
	public Role getRole() {
		return Role.USER; // 기본 사용자 권한
	}
}
