package com.passtival.backend.domain.phoneMatch.login.security.oauth2.dto.kakao;

import java.util.Map;

import com.passtival.backend.domain.phoneMatch.login.security.oauth2.dto.OAuth2Response;
import com.passtival.backend.domain.phoneMatch.meeting.enums.Gender;


public class KakaoResponse implements OAuth2Response {

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
    public String getSocialId() {
        return getProvider() + "_" + getProviderId(); // 소셜 ID는 provider와 providerId를 조합하여 생성
    }

    @Override
    public String getPhoneNumber() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");
        if (kakaoAccount == null) return null;

        Object phoneNumber = kakaoAccount.get("phone_number");
        return phoneNumber != null ? phoneNumber.toString().replaceAll("[^0-9]", "").replaceFirst("^82", "0") : null;
    }

    @Override
    public String getName() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");
        if (kakaoAccount == null) return null;
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        if (profile == null) return null;
        Object nickname = profile.get("nickname");
        return nickname != null ? nickname.toString() : null;
    }

    @Override
    public Gender getGender() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");
        if (kakaoAccount == null) return null;

        Object gender = kakaoAccount.get("gender");
        if (gender == null) return null;

        String genderStr = gender.toString();
        try {
            return Gender.valueOf(genderStr.toLowerCase());
        } catch (IllegalArgumentException e) {
            // 잘못된 값이 들어온 경우 처리 (로그 등)
            return null;
        }
    }
}
