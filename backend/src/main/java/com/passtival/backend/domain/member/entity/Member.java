package com.passtival.backend.domain.member.entity;


import com.passtival.backend.domain.member.enums.Gender;
import com.passtival.backend.global.common.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(name = "social_id", length = 100)
    private String socialId;// 소셜 로그인 ID (예: "kakao_1234567890")

    @Column(length = 50)
    private String name; // "홍길동"

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Gender gender;// "male" 또는 "female"

    @Column(name = "phone_number", unique = true, length = 100)
    private String phoneNumber; // 제공자에 따른 형태를 0101235678로 정규화 후 저장

    @Column(name = "instagram_id", length = 100)
    private String instagramId; // 인스타그램 ID

    @Column(length = 255)
    private String password;

    @Column(name = "applied", nullable = false)
    private boolean applied = false;//신청 유무 기본 값 false

    @Column(name = "applied_at")
    private LocalDateTime appliedAt; // applied가 true로 바뀐 시간 (선착순 기준)

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Role role;

}
