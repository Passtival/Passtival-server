package com.passtival.backend.domain.raffle.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.passtival.backend.domain.raffle.model.entity.Prize;
import com.passtival.backend.domain.raffle.repository.PrizeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrizeService {

	/**
	 * 의존성 주입
	 */
	private final PrizeRepository prizeRepository;


	/**
	 * 상품 목록 조회
	 * @return 상품 목록
	 */
	public List<Prize> getAllPrizes() {
		try {
			return prizeRepository.findAll();
		} catch (Exception e) {
			log.error("데이터베이스에서 상품 목록 조회 중 오류 발생", e);
			throw new RuntimeException("상품 목록 조회 중 오류가 발생했습니다.", e);
		}
	}

	/**
	 * 상품 ID로 상품 조회
	 * @param prizeId 상품 ID
	 * @return 상품 정보
	 */
	public Prize getPrizeById(Long prizeId) {
		try {
			return prizeRepository.findById(prizeId)
				.orElseThrow(() -> new RuntimeException("해당 ID의 상품을 찾을 수 없습니다."));
		} catch (RuntimeException e) {
			throw e; // RuntimeException은 그대로 전파
		} catch (Exception e) {
			log.error("데이터베이스에서 상품 조회 중 오류 발생 - prizeId: {}", prizeId, e);
			throw new RuntimeException("상품 조회 중 오류가 발생했습니다.", e);
		}
	}

}
