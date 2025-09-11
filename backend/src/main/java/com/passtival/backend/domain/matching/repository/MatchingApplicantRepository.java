package com.passtival.backend.domain.matching.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.passtival.backend.domain.matching.model.entity.MatchingApplicant;
import com.passtival.backend.domain.matching.model.enums.Gender;

public interface MatchingApplicantRepository extends JpaRepository<MatchingApplicant, Long> {
	// 특정 회원을 제외하고 전화번호 중복 검사
	boolean existsByPhoneNumberAndMemberIdNot(String phoneNumber, Long memberId);

	// 특정 회원을 제외하고 인스타그램 ID 중복 검사
	boolean existsByInstagramIdAndMemberIdNot(String instagramId, Long memberId);

	// 특정 사용자들만 신청 상태 초기화 (매칭 신청 / 성공에 따라 별도로 실행을 통해 메모리 최적화)
	@Modifying
	@Query("UPDATE MatchingApplicant u SET u.applied = false, u.appliedAt = null WHERE u.memberId IN :memberIds")
	void resetApplicationsByMemberIds(@Param("memberIds") List<Long> memberIds);

	// 모든 사용자 신청 상태 초기화 (매칭 가능자가 없을 때 사용: 한쪽 성별 0명 일 때)
	@Modifying
	@Query("UPDATE MatchingApplicant u SET u.applied = false, u.appliedAt = null")
	void resetAllApplications();

	// 파트너와 나의 정보를 조회하는 메서드 추가
	List<MatchingApplicant> findByMemberIdIn(List<Long> memberIds);

	//신청한 인원을 성별 기준으로 확인
	long countByAppliedTrueAndGender(Gender gender);

	// 제한된 수만 조회 (Pageable 활용 자르기)
	List<MatchingApplicant> findByAppliedTrueAndGenderOrderByAppliedAtAsc(Gender gender, Pageable pageable);

	//신청했던 사람들 id를 전달 받아 초기화
	@Modifying
	@Query("UPDATE MatchingApplicant m SET m.applied = false, m.appliedAt = null WHERE m.applied = true AND m.memberId NOT IN :matchedIds")
	void resetApplicationsForUnmatched(@Param("matchedIds") Set<Long> matchedIds);

	// 테스트용 메서드들
	List<MatchingApplicant> findByNameStartingWith(String namePrefix);

	// 회원 존재 여부 확인
	boolean existsByMemberId(Long memberId);
}
