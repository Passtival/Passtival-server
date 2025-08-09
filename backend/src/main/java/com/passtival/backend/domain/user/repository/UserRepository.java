package com.passtival.backend.domain.user.repository;

import com.passtival.backend.domain.user.entity.User;
import com.passtival.backend.domain.user.enums.Gender;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 로그인용
    Optional<User> findByPhoneNumber(String phoneNumber);

    // 회원가입용 전화번호 중복 검사
    boolean existsByPhoneNumber(String phoneNumber);

    // 매칭 알고리즘용 (성별별 선착순)
    List<User> findByapplyTrueAndGenderOrderByApplicationTimeAsc(Gender gender);

    // 특정 사용자들만 신청 상태 초기화
    @Modifying
    @Query("UPDATE User u SET u.apply = false, u.applicationTime = null WHERE u.userId IN :userIds")
    void resetApplicationsByUserIds(@Param("userIds") List<Long> userIds);

    // 모든 사용자 신청 상태 초기화 (매칭 가능자가 없을 때 사용)
    @Modifying
    @Query("UPDATE User u SET u.apply = false, u.applicationTime = null")
    void resetAllApplications();

    // 특정 사용자들의 정보를 조회하는 메서드 추가
    @Query("SELECT u FROM User u WHERE u.userId IN :userIds")
    List<User> findByUserIdIn(@Param("userIds") List<Long> userIds);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.apply = true AND u.gender = :gender")
    long countByIsApplyTrueAndGender(@Param("gender") Gender gender);

    // 제한된 수만 조회 (Pageable 활용)
    @Query("SELECT u FROM User u WHERE u.apply = true AND u.gender = :gender ORDER BY u.applicationTime ASC")
    List<User> findTopApplicantsByGender(@Param("gender") Gender gender, Pageable pageable);

    // ID만 조회하는 메서드 (메모리 효율적)
    @Query("SELECT u.userId FROM User u WHERE u.apply = true AND u.gender = :gender ORDER BY u.applicationTime ASC")
    List<Long> findUserIdsByGender(@Param("gender") Gender gender);
}
