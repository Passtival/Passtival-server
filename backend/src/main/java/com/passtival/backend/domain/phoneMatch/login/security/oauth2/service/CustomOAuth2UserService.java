package com.passtival.backend.domain.phoneMatch.login.security.oauth2.service;

import com.passtival.backend.domain.phoneMatch.login.security.oauth2.dto.CustomOAuth2User;
import com.passtival.backend.domain.phoneMatch.login.security.oauth2.dto.kakao.KakaoResponse;
import com.passtival.backend.domain.phoneMatch.login.security.oauth2.dto.OAuth2Response;
import com.passtival.backend.domain.phoneMatch.login.security.oauth2.dto.PhoneMatchUserDto;
import com.passtival.backend.domain.phoneMatch.meeting.entity.PhoneMatchUser;
import com.passtival.backend.domain.phoneMatch.meeting.repository.PhoneMatchUserRepository;
import com.passtival.backend.domain.phoneMatch.meeting.enums.Role;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final PhoneMatchUserRepository phoneMatchUserRepository;

    public CustomOAuth2UserService(PhoneMatchUserRepository phoneMatchUserRepository) {
        this.phoneMatchUserRepository = phoneMatchUserRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        System.out.println(oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response;

        if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        } else {

            return null;
        }

        //모든 제공자에게 전화 번호를 필수로 받는다는 가정
        String phoneNumber = oAuth2Response.getPhoneNumber();
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            // 전화번호 동의하지 않은 경우 처리
            throw new OAuth2AuthenticationException("전화번호 동의가 필요합니다");
        }
        Optional<PhoneMatchUser> existData = phoneMatchUserRepository.findByPhoneNumber(phoneNumber);

        if (existData.isEmpty()) {
            PhoneMatchUser phoneMatchUser = new PhoneMatchUser();
            phoneMatchUser.setSocialId(oAuth2Response.getSocialId());
            phoneMatchUser.setName(oAuth2Response.getName());
            phoneMatchUser.setGender(oAuth2Response.getGender());
            phoneMatchUser.setPhoneNumber(phoneNumber);
            phoneMatchUser.setRole(Role.ROLE_USER);
            PhoneMatchUser savedUser = phoneMatchUserRepository.save(phoneMatchUser);

            PhoneMatchUserDto phoneMatchUserDto = PhoneMatchUserDto.builder()
                    .userId(savedUser.getUserId())
                    .socialId(oAuth2Response.getSocialId())
                    .name(oAuth2Response.getName())
                    .gender(oAuth2Response.getGender())
                    .phoneNumber(phoneNumber)
                    .role(phoneMatchUser.getRole())
                    .build();
            return new CustomOAuth2User(phoneMatchUserDto);
        } else {
            PhoneMatchUser phoneMatchUser = existData.get();

            PhoneMatchUserDto phoneMatchUserDto = PhoneMatchUserDto.builder()
                    .userId(phoneMatchUser.getUserId())
                    .socialId(phoneMatchUser.getSocialId())
                    .name(phoneMatchUser.getName())
                    .gender(phoneMatchUser.getGender())
                    .phoneNumber(phoneMatchUser.getPhoneNumber())
                    .role(phoneMatchUser.getRole())
                    .build();
            return new CustomOAuth2User(phoneMatchUserDto);
        }
    }
}