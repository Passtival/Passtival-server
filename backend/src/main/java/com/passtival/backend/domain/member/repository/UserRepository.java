package com.passtival.backend.domain.member.repository;

import com.passtival.backend.domain.member.entity.Member;
import com.passtival.backend.domain.member.enums.Gender;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Member, Long> {
    // 로그인용
    Optional<Member> findByPhoneNumber(String phoneNumber);

    // 회원가입용 전화번호 중복 검사
    boolean existsByPhoneNumber(String phoneNumber);


    // 특정 사용자들만 신청 상태 초기화
    @Modifying
    @Query("UPDATE Member u SET u.apply = false, u.applicationTime = null WHERE u.userId IN :userIds")
    void resetApplicationsByUserIds(@Param("userIds") List<Long> userIds);

    // 모든 사용자 신청 상태 초기화 (매칭 가능자가 없을 때 사용)
    @Modifying
    @Query("UPDATE Member u SET u.apply = false, u.applicationTime = null")
    void resetAllApplications();

    // 특정 사용자들의 정보를 조회하는 메서드 추가
    @Query("SELECT u FROM Member u WHERE u.userId IN :userIds")
    List<Member> findByUserIdIn(@Param("userIds") List<Long> userIds);
    
    @Query("SELECT COUNT(u) FROM Member u WHERE u.apply = true AND u.gender = :gender")
    long countByIsApplyTrueAndGender(@Param("gender") Gender gender);

    // 제한된 수만 조회 (Pageable 활용)
    @Query("SELECT u FROM Member u WHERE u.apply = true AND u.gender = :gender ORDER BY u.applicationTime ASC")
    List<Member> findTopApplicantsByGender(@Param("gender") Gender gender, Pageable pageable);

    // ID만 조회하는 메서드 (메모리 효율적)
    @Query("SELECT u.userId FROM Member u WHERE u.apply = true AND u.gender = :gender ORDER BY u.applicationTime ASC")
    List<Long> findUserIdsByGender(@Param("gender") Gender gender);
}
