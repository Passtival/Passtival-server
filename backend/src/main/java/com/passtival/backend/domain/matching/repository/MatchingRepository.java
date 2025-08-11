package com.passtival.backend.domain.matching.repository;

import com.passtival.backend.domain.matching.entity.Matching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MatchingRepository extends JpaRepository<Matching, Long> {
    
    // 특정 날짜의 매칭 결과 조회
    List<Matching> findByMatchingDate(LocalDate date);

    // 한 사용자의 매칭 상대방 찾기 (핵심!)
    @Query("SELECT m FROM Matching m WHERE m.matchingDate = :date AND (m.maleId = :memberId OR m.femaleId = :memberId)")
    Optional<Matching> findMemberMatchingByDate(@Param("date") LocalDate date, @Param("memberId") Long memberId);

    // 일일 데이터 삭제용
    @Modifying
    @Query("DELETE FROM Matching m WHERE m.matchingDate < :date")
    int deleteByMatchingDateBefore(@Param("date") LocalDate date);

    // 모든 매칭 결과 삭제
    @Modifying
    @Query("DELETE FROM Matching")
    void deleteAllMatchingResults();
}
