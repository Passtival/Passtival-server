package com.passtival.backend.domain.matching.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchingResultDto {

    // 내 정보
    private UserInfo myInfo;

    // 상대방 정보
    private UserInfo partnerInfo;

    // 매칭 날짜
    private String matchingDate;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInfo {
        private String phoneNumber;
        private String instagramId;
    }
}