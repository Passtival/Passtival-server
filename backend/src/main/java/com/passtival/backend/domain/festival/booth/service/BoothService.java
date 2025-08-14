package com.passtival.backend.domain.festival.booth.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.passtival.backend.domain.festival.booth.model.entity.Booth;
import com.passtival.backend.domain.festival.booth.model.response.BoothResponse;
import com.passtival.backend.domain.festival.booth.repository.BoothRepository;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoothService {

	private final BoothRepository boothRepository;

	/**
	 * 모든 부스 목록 조회 (페이징 가능)
	 * @param pageable 페이지 요청 정보
	 * @return Page<Booth>
	 */
	public Page<BoothResponse> getAllBooths(Pageable pageable) throws BaseException {
		Page<Booth> page = boothRepository.findAll(pageable);
		if (page.isEmpty()) {
			throw new BaseException(BaseResponseStatus.PERFORMANCE_NOT_FOUND);
		}
		return page.map(BoothResponse::of);
	}

	// 부스 이름 조회
	public BoothResponse getBoothByName(String name) {
		Booth booth = boothRepository.findByName(name)
			.orElseThrow(() -> new IllegalArgumentException("해당 이름의 부스가 없습니다."));
		return BoothResponse.of(booth); // Entity → DTO 변환
	}
}
