package com.passtival.backend.domain.festival.performance.service;

import java.util.List;

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

	/**
	 * 모든 공연 목록 조회 (페이징 가능)
	 * @param pageable 페이지 요청 정보
	 * 값이 비었을 때 : PERFORMANCE_NOT_FOUND에러 메시지
	 * @return Page<Performance>
	 */
	public Page<Performance> getAllPerformances(Pageable pageable) {
		Page<Performance> page = performanceRepository.findAll(pageable);
		if (page.isEmpty()) {
			throw new BaseException(BaseResponseStatus.PERFORMANCE_NOT_FOUND);
		}
		return page;
	}

	/**
	 * 커서기반 페이지네이션
	 */
	public CursorPageResponse<PerformanceResponse> getPerformances(Long cursorId, int size) {
		Pageable pageable = PageRequest.of(0, size); // offset=0 고정
		List<Performance> performances = performanceRepository.findPageByCursor(cursorId, pageable);

		if (performances.isEmpty()) {
			throw new BaseException(BaseResponseStatus.BOOTH_NOT_FOUND); // 부스 없음 예외
		}

		Long nextCursor = performances.isEmpty() ? null : performances.get(performances.size() - 1).getId();

		return new CursorPageResponse<>(
			performances.stream().map(PerformanceResponse::of).toList(),
			nextCursor
		);
	}

	// 공연 이름 id로 조회
	public PerformanceDetailResponse getPerformanceById(Long performanceId) {
		Performance performance = performanceRepository.findById(performanceId)
			.orElseThrow(() -> new BaseException(BaseResponseStatus.PERFORMANCE_NOT_FOUND));
		return PerformanceDetailResponse.of(performance);
	}

}
