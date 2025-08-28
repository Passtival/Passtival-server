package com.passtival.backend.domain.festival.booth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.festival.booth.model.entity.Booth;
import com.passtival.backend.domain.festival.booth.model.request.BoothRequest;
import com.passtival.backend.domain.festival.booth.repository.BoothRepository;
import com.passtival.backend.domain.festival.menu.model.entity.Menu;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/seed")
@RequiredArgsConstructor
@Tag(name = "데이터 주입 API", description = "공연, 부스, 메뉴 데이터 주입")
public class BoothSeedController {

	private final BoothRepository boothRepository;

	@Value("${app.admin.seed-key}")
	private String seedKey;

	// 인증키 확인
	private void validateKey(String key) {
		if (!seedKey.equals(key)) {
			throw new BaseException(BaseResponseStatus.INVALID_AUTH_KEY);
		}
	}

	@PostMapping("/booths")
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
						.price(menuReq.getPrice())
						.build());
				}
			}

			boothRepository.save(booth);
		}

		return BaseResponse.success("Booths 데이터 삽입 성공!");
	}
}
