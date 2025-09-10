package com.passtival.backend.domain.festival.performance.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.passtival.backend.domain.festival.performance.model.response.CursorPageResponse;
import com.passtival.backend.domain.festival.performance.model.response.PerformanceDetailResponse;
import com.passtival.backend.domain.festival.performance.model.response.PerformanceResponse;
import com.passtival.backend.domain.festival.performance.model.entity.Performance;
import com.passtival.backend.domain.festival.performance.repository.PerformanceRepository;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PerformanceService {

	private final PerformanceRepository performanceRepository;

	// 공연 이름 id로 조회
	public PerformanceDetailResponse getPerformanceById(Long performanceId) {
		Performance performance = performanceRepository.findById(performanceId)
			.orElseThrow(() -> new BaseException(BaseResponseStatus.PERFORMANCE_NOT_FOUND));
		return PerformanceDetailResponse.of(performance);
	}

	public List<PerformanceResponse> getPerformancesByClosestTime() {
		LocalDateTime now = LocalDateTime.now();

		return performanceRepository.findAll().stream()
			.sorted(Comparator.comparingLong(p ->
				Math.abs(Duration.between(now, p.getStartTime()).toMinutes())
			))
			.map(PerformanceResponse::of)
			.toList();
	}

}
