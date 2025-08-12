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
import java.util.Set;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // 회원 구분 기준을 전화번호로 한다.
    Optional<Member> findByPhoneNumber(String phoneNumber);

    Optional<Member> findBySocialId(String socialId);

    boolean existsBySocialId(String socialId);

    // 회원가입용 전화번호 중복 검사
    boolean existsByPhoneNumber(String phoneNumber);

    // 특정 사용자들만 신청 상태 초기화 (매칭 신청 / 성공에 따라 별도로 실행을 통해 메모리 최적화)
    @Modifying
    @Query("UPDATE Member u SET u.applied = false, u.appliedAt = null WHERE u.memberId IN :memberIds")
    void resetApplicationsByMemberIds(@Param("memberIds") List<Long> memberIds);

    // 모든 사용자 신청 상태 초기화 (매칭 가능자가 없을 때 사용: 한쪽 성별 0명 일 때)
    @Modifying
    @Query("UPDATE Member u SET u.applied = false, u.appliedAt = null")
    void resetAllApplications();

    // 파트너와 나의 정보를 조회하는 메서드 추가
    @Query("SELECT u FROM Member u WHERE u.memberId IN :memberIds")
    List<Member> findByMemberIdIn(@Param("memberIds") List<Long> memberIds);

    //신청한 인원을 성별 기준으로 체크
    @Query("SELECT COUNT(u) FROM Member u WHERE u.applied = true AND u.gender = :gender")
    long countByAppliedTrueAndGender(@Param("gender") Gender gender);

    // 제한된 수만 조회 (Pageable 활용 자르기)
    @Query("SELECT u FROM Member u WHERE u.applied = true AND u.gender = :gender ORDER BY u.appliedAt ASC")
    List<Member> findTopApplicantsByGender(@Param("gender") Gender gender, Pageable pageable);

    //신청했던 사람들 id를 전달 받아 초기화
    @Modifying
    @Query("UPDATE Member m SET m.applied = false, m.appliedAt = null WHERE m.applied = true AND m.memberId NOT IN :matchedIds")
    void resetApplicationsForUnmatched(@Param("matchedIds") Set<Long> matchedIds);
}
