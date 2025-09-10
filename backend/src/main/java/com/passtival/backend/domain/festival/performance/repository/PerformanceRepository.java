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

	boolean existsByTitle(String title);

}
