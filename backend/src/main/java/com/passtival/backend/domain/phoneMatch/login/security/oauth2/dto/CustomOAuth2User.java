package com.passtival.backend.domain.phoneMatch.login.security.oauth2.dto;

import com.passtival.backend.domain.phoneMatch.meeting.enums.Gender;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final PhoneMatchUserDto phoneMatchUserDto;

    public CustomOAuth2User(PhoneMatchUserDto phoneMatchUserDto) {
        this.phoneMatchUserDto = phoneMatchUserDto;
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
                return phoneMatchUserDto.getRole().name();
            }
        });
        return collection;
    }

    @Override
    public String getName() {
        return phoneMatchUserDto.getName();
    }
    public String getGender(){
        Gender gender = phoneMatchUserDto.getGender();
        return gender != null ? gender.name() : null;
    }
    public String getSocialId(){
        return phoneMatchUserDto.getSocialId();
    }
    public String getPhoneNumber(){
        return phoneMatchUserDto.getPhoneNumber();
    }

}
