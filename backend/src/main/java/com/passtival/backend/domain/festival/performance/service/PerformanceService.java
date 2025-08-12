package com.passtival.backend.domain.festival.performance.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.passtival.backend.domain.festival.performance.model.response.PerformanceResponseDTO;
import com.passtival.backend.domain.festival.performance.model.entity.Performance;
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
	public PerformanceResponseDTO getPerformanceByName(String name) {
		Performance performance = performanceRepository.findByTitle(name)
			.orElseThrow(() -> new IllegalArgumentException("해당 이름의 공연이 없습니다."));
		return PerformanceResponseDTO.of(performance); // Entity → DTO 변환
	}

}
