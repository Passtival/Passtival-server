package com.passtival.backend.domain.raffle.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.passtival.backend.domain.raffle.model.entity.Prize;
import com.passtival.backend.domain.raffle.repository.PrizeRepository;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;

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
	public List<Prize> getAllPrizes() throws BaseException {
		try {
			return prizeRepository.findAll();
		} catch (Exception e) {
			throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
		}
	}

	/**
	 * 상품 ID로 상품 조회
	 * @param prizeId 상품 ID
	 * @return 상품 정보
	 */
	public Prize getPrizeById(Long prizeId) throws BaseException {
		try {
			return prizeRepository.findById(prizeId)
				.orElseThrow(() -> new BaseException(BaseResponseStatus.PRIZE_NOT_FOUND));
		} catch (BaseException e) {
			throw e; // RuntimeException은 그대로 전파
		} catch (Exception e) {
			throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
		}
	}

}
