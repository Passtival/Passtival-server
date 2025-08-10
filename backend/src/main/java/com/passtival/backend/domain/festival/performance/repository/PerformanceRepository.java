package com.passtival.backend.domain.festival.performance.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.passtival.backend.domain.festival.performance.model.Performance;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {

	Optional<Performance> findByTitle(String title);

	Optional<Performance> findByTitleAndStartAt(String title, LocalDateTime startAt);
}
