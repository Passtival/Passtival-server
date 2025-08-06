package com.passtival.backend.domain.phoneMatch.login.security.oauth2.dto;

import com.passtival.backend.domain.phoneMatch.meeting.enums.Gender;

public interface OAuth2Response {

    //제공자 (Ex. naver, google, ...)
    String getProvider();
    //제공자에서 발급해주는 아이디(번호)
    String getProviderId();

    String getSocialId();
    //사용자의 전화 번호
    String getPhoneNumber();
    //사용자 실명 (설정한 이름)
    String getName();
    //사용자 성별
    Gender getGender();
}
