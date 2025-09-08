package com.passtival.backend.domain.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.admin.model.request.BoothRequest;
import com.passtival.backend.domain.admin.model.request.PerformanceRequest;
import com.passtival.backend.domain.festival.booth.model.entity.Booth;
import com.passtival.backend.domain.festival.booth.repository.BoothRepository;
import com.passtival.backend.domain.festival.menu.model.entity.Menu;
import com.passtival.backend.domain.festival.performance.model.entity.Performance;
import com.passtival.backend.domain.festival.performance.model.entity.Song;
import com.passtival.backend.domain.festival.performance.repository.PerformanceRepository;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/admin/seed")
@RequiredArgsConstructor
@Tag(name = "관리자 API")
public class SeedController {

	private final PerformanceRepository performanceRepository;
	private final BoothRepository boothRepository;

	@Value("${admin.seed-key}")
	private String seedKey;

	// 인증키 검증
	private void validateKey(String key) {
		if (!seedKey.equals(key)) {
			throw new BaseException(BaseResponseStatus.INVALID_AUTH_KEY);
		}
	}

	@PostMapping("/performances")
	@Operation(
		summary = "[백엔드 용] 공연 seed api",
		security = @SecurityRequirement(name = "jwtAuth")
	)
	public BaseResponse<?> insertPerformances(
		@RequestHeader("X-ADMIN-KEY") String key,
		@RequestBody @Valid List<PerformanceRequest> performanceRequests) {

		validateKey(key);

		// 요청 리스트 비어있을 때
		if (performanceRequests == null || performanceRequests.isEmpty()) {
			throw new BaseException(BaseResponseStatus.REQUEST_BODY_EMPTY);
		}

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

		return BaseResponse.success("Performances 데이터 삽입 성공!");
	}

	@PostMapping("/booths")
	@Operation(
		summary = "[백엔드 용] 부스 seed api",
		security = @SecurityRequirement(name = "jwtAuth")
	)
	public BaseResponse<String> insertBooths(
		@RequestHeader("X-ADMIN-KEY") String key,
		@RequestBody @Valid List<BoothRequest> boothRequests) {

		validateKey(key);

		if (boothRequests == null || boothRequests.isEmpty()) {
			throw new BaseException(BaseResponseStatus.REQUEST_BODY_EMPTY);
		}

		for (BoothRequest req : boothRequests) {
			if (boothRepository.existsByName(req.getName())) {
				throw new BaseException(BaseResponseStatus.DUPLICATE_BOOTH_NAME);
			}

			Booth booth = Booth.builder()
				.name(req.getName())
				.type(req.getType())
				.department(req.getDepartment())
				.operatingStart(req.getOperatingStart())
				.operatingEnd(req.getOperatingEnd())
				.location(req.getLocation())
				.info(req.getInfo())
				.imagePath(req.getImagePath())
				.locationImagePath(req.getLocationImagePath())
				.build();

			if (req.getMenus() != null) {
				for (BoothRequest.MenuRequest menuReq : req.getMenus()) {
					booth.addMenu(Menu.builder()
						.type(menuReq.getType())
						.name(menuReq.getName())
						.introduction(menuReq.getIntroduction())
						.imagePath(menuReq.getImagePath())
						.price(menuReq.getPrice())
						.build());
				}
			}

			boothRepository.save(booth);
		}

		return BaseResponse.success("Booths 데이터 삽입 성공!");
	}

}
