package com.passtival.backend.global.auth.model;

import com.passtival.backend.global.common.enums.Role;

public interface Oauth2Response {

	//제공자 (Ex. naver, google, ...)
	String getProvider();

	//제공자에서 발급해주는 아이디(번호)
	String getProviderId();

	String getSocialId();

	//사용자 실명 (설정한 이름)
	String getName();

	//사용자 닉네임 (설정한 닉네임)
	String getNickname();

	//사용자 역할 (기본: USER)
	Role getRole();
}
