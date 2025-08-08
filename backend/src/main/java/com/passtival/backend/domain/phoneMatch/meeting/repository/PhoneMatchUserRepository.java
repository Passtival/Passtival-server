package com.passtival.backend.domain.phoneMatch.meeting.repository;

import com.passtival.backend.domain.phoneMatch.meeting.entity.PhoneMatchUser;
import com.passtival.backend.domain.phoneMatch.meeting.enums.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PhoneMatchUserRepository extends JpaRepository<PhoneMatchUser, Long> {
    // 로그인용
    Optional<PhoneMatchUser> findByPhoneNumber(String phoneNumber);

    // 매칭 알고리즘용 (성별별 선착순)
    List<PhoneMatchUser> findByIsApplyTrueAndGenderOrderByApplicationTimeAsc(Gender gender);

    // 일일 초기화용
    @Modifying
    @Query("UPDATE PhoneMatchUser u SET u.isApply = false, u.applicationTime = null")
    void resetAllApplications();
}
