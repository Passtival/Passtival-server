package com.passtival.backend.domain.festival.performance.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

	/**
	 * 모든 공연 목록 조회 (페이징 가능)
	 * @param pageable 페이지 요청 정보
	 * 값이 비었을 때 : PERFORMANCE_NOT_FOUND에러 메시지
	 * @return Page<Performance>
	 */
	public Page<Performance> getAllPerformances(Pageable pageable) throws BaseException {
		Page<Performance> page = performanceRepository.findAll(pageable);
		if (page.isEmpty()) {
			throw new BaseException(BaseResponseStatus.PERFORMANCE_NOT_FOUND);
		}
		return page;
	}

	// 공연 이름 조회
	public PerformanceResponse getPerformanceByName(String name) {
		Performance performance = performanceRepository.findByTitle(name)
			.orElseThrow(() -> new IllegalArgumentException("해당 이름의 공연이 없습니다."));
		return PerformanceResponse.of(performance); // Entity → DTO 변환
	}

}
