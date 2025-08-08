package com.passtival.backend.domain.phoneMatch.meeting.repository;

import com.passtival.backend.domain.phoneMatch.meeting.entity.MatchingResult;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MatchingResultRepository extends JpaRepository<MatchingResult, Long> {
    // 특정 날짜의 매칭 결과 조회
    List<MatchingResult> findByMatchingDate(LocalDate date);

    // 한 사용자의 매칭 상대방 찾기 (핵심!)
    @Query("SELECT m FROM MatchingResult m WHERE m.matchingDate = :date AND (m.userId1 = :userId OR m.userId2 = :userId)")
    Optional<MatchingResult> findUserMatchingByDate(@Param("date") LocalDate date, @Param("userId") Long userId);

    // 일일 데이터 삭제용
    @Modifying
    @Query("DELETE FROM MatchingResult m WHERE m.matchingDate < :date")
    void deleteByMatchingDateBefore(LocalDate date);
}