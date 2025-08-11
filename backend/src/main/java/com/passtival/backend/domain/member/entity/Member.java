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

    @Column(length = 100)//unique = true, length = 100
    private String socialId;// 소셜 로그인 ID (예: "kakao_1234567890")

    @Column(length = 50)
    private String name; // "홍길동"

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Gender gender;// "male" 또는 "female" 카카오톡 형식을 기본으로 함

    @Column(unique = true, length = 100)
    private String phoneNumber; // 제공자에 따른 형태를 0101235678로 정규화 후 저장

    @Column(length = 100)
    private String instagramId; // 인스타그램 ID

    @Column(length = 255)
    private String password;

    @Column(nullable = false)
    private boolean apply = false;//신청 유무 기본 값 flase

    @Column
    private LocalDateTime applicationTime; // isApply가 true로 바뀐 시간 (선착순 기준)

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Role role;

    // 명시적 getter 메서드 추가 (Lombok 문제 해결)
    public Long getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getInstagramId() {
        return instagramId;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public Gender getGender() {
        return gender;
    }

    public boolean isApply() {
        return apply;
    }

    public LocalDateTime getApplicationTime() {
        return applicationTime;
    }

    public String getSocialId() {
        return socialId;
    }
}
