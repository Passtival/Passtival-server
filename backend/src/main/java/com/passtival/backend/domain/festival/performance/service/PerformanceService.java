package com.passtival.backend.domain.festival.performance.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.passtival.backend.domain.festival.performance.model.Performance;
import com.passtival.backend.domain.festival.performance.repository.PerformanceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PerformanceService {

	private final PerformanceRepository performanceRepository;

	/**
	 * 모든 공연 목록 조회 (페이징 가능)
	 * @param pageable 페이지 요청 정보
	 * @return Page<Performance>
	 */
	public Page<Performance> getAllPerformances(Pageable pageable) {
		return performanceRepository.findAll(pageable);
	}

	// 공연 이름 조회
	public Performance getPerformanceByPerformanceTitle(String title) {
		return performanceRepository.findByTitle(title)
			.orElseThrow(() -> new RuntimeException("공연이름과 일치하는 공연이 없습니다: " + title));
	}

}
