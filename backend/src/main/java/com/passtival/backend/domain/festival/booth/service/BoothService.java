package com.passtival.backend.domain.festival.booth.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.passtival.backend.domain.festival.booth.model.entity.Booth;
import com.passtival.backend.domain.festival.booth.model.response.BoothResponseDTO;
import com.passtival.backend.domain.festival.booth.repository.BoothRepository;

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
	public Page<Booth> getAllBooths(Pageable pageable) {
		return boothRepository.findAll(pageable);
	}

	// 부스 이름 조회
	public BoothResponseDTO getBoothByName(String name) {
		Booth booth = boothRepository.findByName(name)
			.orElseThrow(() -> new IllegalArgumentException("해당 이름의 부스가 없습니다."));
		return BoothResponseDTO.of(booth); // Entity → DTO 변환
	}
}
