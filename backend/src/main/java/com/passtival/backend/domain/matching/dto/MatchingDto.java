package com.passtival.backend.domain.matching.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchingDto {

    // 내 정보
    private MemberInfo myInfo;

    // 상대방 정보
    private MemberInfo partnerInfo;

    // 매칭 날짜
    private String matchingDate;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberInfo {
        private String phoneNumber;
        private String instagramId;
    }
}