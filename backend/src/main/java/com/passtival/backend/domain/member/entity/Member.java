package com.passtival.backend.domain.member.entity;


import com.passtival.backend.domain.member.enums.Gender;
import com.passtival.backend.global.common.enums.Role;
import com.passtival.backend.global.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "members")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(name = "social_id", length = 50)
    private String socialId;// 소셜 로그인 ID (예: "kakao_1234567890")

    @Column(length = 25)
    private String name; // "홍길동"

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private Gender gender;// "MALE" 또는 "FEMALE"

    @Column(name = "phone_number", unique = true, length = 20)
    private String phoneNumber; // 제공자에 따른 형태를 0101235678로 정규화 후 저장

    @Column(name = "instagram_id", length = 35)
    private String instagramId; // 인스타그램 ID

    @Column(name = "applied", nullable = false)
    private boolean applied;//신청 유무 기본 값 false

    @Column(name = "applied_at")
    private LocalDateTime appliedAt; // applied가 true로 바뀐 시간 (선착순 기준)

    //회원가입만 하고 온보딩을 끝내지 않은 사용자 체크
    @Column(name="onboarding_completed")
    private boolean onboardingCompleted;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private Role role;

    public static Member createSocialMember(String socialId, String name, Gender gender, String phoneNumber,
                                       String instagramId) {
        return Member.builder()
                .socialId(socialId)
                .name(name)
                //.gender(gender)
                //.phoneNumber(phoneNumber)
                .instagramId(instagramId)
                .applied(false)
                .appliedAt(null)
                .onboardingCompleted(false)
                .role(Role.USER)
                .build();
    }
    //온보딩 완료 처리
    public void completeOnboarding(Gender gender, String phoneNumber, String instagramId) {
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.instagramId = instagramId;
        this.onboardingCompleted = true;
    }

    public void applyForMatching(String instagramId) {
        // 인스타그램 ID 선택 입력 처리
        if (instagramId == null || instagramId.trim().isEmpty()) {
            this.instagramId = "";
        } else {
            this.instagramId = instagramId.trim();
        }

        // 매칭 신청 상태 및 시간 업데이트
        this.applied = true;
        this.appliedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }
}
