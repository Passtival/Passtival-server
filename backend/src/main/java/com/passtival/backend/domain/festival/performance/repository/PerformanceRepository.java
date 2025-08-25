package com.passtival.backend.domain.festival.performance.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.passtival.backend.domain.festival.performance.model.entity.Performance;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {

	Optional<Performance> findByTitle(String title);

	Optional<Performance> findByTitleAndDay(String title, Integer day);

	@Query("SELECT p FROM Performance p " +
		"WHERE (:cursorId IS NULL OR p.id < :cursorId) " +
		"ORDER BY p.id DESC")
	List<Performance> findPageByCursor(@Param("cursorId") Long cursorId,
		Pageable pageable);

	boolean existsByTitle(String title);

}
