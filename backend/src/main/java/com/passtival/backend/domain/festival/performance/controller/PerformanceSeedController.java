package com.passtival.backend.domain.festival.performance.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.festival.performance.model.entity.Performance;
import com.passtival.backend.domain.festival.performance.model.entity.Song;
import com.passtival.backend.domain.festival.performance.model.request.PerformanceRequest;
import com.passtival.backend.domain.festival.performance.repository.PerformanceRepository;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/seed")
@RequiredArgsConstructor
public class PerformanceSeedController {

	private final PerformanceRepository performanceRepository;

	@Value("${app.admin.seed-key}")
	private String seedKey;

	// 인증키 검증
	private void validateKey(String key) {
		if (!seedKey.equals(key)) {
			throw new SecurityException("Invalid seed key");
		}
	}

	@PostMapping("/performances")
	public BaseResponse<?> insertPerformances(
		@RequestHeader("X-ADMIN-KEY") String key,
		@RequestBody @Valid List<PerformanceRequest> performanceRequests) {

		validateKey(key);

		for (PerformanceRequest req : performanceRequests) {

			if (performanceRepository.existsByTitle(req.getTitle())) {
				throw new BaseException(BaseResponseStatus.DUPLICATE_PERFORMANCE_TITLE);
			}

			Performance perf = Performance.builder()
				.title(req.getTitle())
				.artist(req.getArtist())
				.area(req.getArea())
				.imagePath(req.getImagePath())
				.startTime(req.getStartTime())
				.endTime(req.getEndTime())
				.introduction(req.getIntroduction())
				.day(req.getDay())
				.build();

			if (req.getSongs() != null) {
				for (PerformanceRequest.SongRequest songReq : req.getSongs()) {
					perf.addSong(Song.builder()
						.title(songReq.getTitle())
						.singer(songReq.getSinger())
						.build());
				}
			}

			performanceRepository.save(perf);
		}

		return BaseResponse.success("Performances inserted!");
	}


}
