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
			.sorted((p1, p2) -> {
				// 상태값 계산
				int s1 = getStatus(p1, now);
				int s2 = getStatus(p2, now);

				if (s1 != s2) {
					return Integer.compare(s1, s2); // 상태 우선순위
				}
				return p1.getStartTime().compareTo(p2.getStartTime()); // 같은 그룹이면 시작시간 오름차순
			})
			.map(PerformanceResponse::of)
			.toList();
	}

	private int getStatus(Performance p, LocalDateTime now) {
		if (!p.getStartTime().isAfter(now) && !p.getEndTime().isBefore(now)) {
			return 0; // 현재 공연 중
		} else if (p.getStartTime().isAfter(now)) {
			return 1; // 미래 공연
		} else {
			return 2; // 지난 공연
		}
	}



}
