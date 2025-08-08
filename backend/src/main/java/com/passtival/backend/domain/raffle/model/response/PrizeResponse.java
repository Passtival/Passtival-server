package com.passtival.backend.domain.raffle.model.response;

import com.passtival.backend.domain.raffle.model.entity.Prize;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PrizeResponse {

	private final Long prizeId; // 상품 ID
	private final String prizeImagePath; // 상품 이미지 경로
	private final String prizeName; // 상품 이름

	/**
	 * Prize 엔티티를 PrizeResponseDTO로 변환하는 정적 팩토리 메서드
	 *
	 * @param prize Prize 엔티티
	 * @return PrizeResponseDTO 객체
	 * @throws IllegalArgumentException prize가 null인 경우
	 */
	public static PrizeResponse of(Prize prize) {
		if (prize == null) {
			throw new IllegalArgumentException("Prize 값이 null입니다.");
		}
		return PrizeResponse.builder()
				.prizeId(prize.getPrizeId())
				.prizeImagePath(prize.getPrizeImagePath())
				.prizeName(prize.getPrizeName())
				.build();
	}

}
