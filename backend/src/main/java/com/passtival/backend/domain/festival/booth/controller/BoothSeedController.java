package com.passtival.backend.domain.festival.booth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/seed")
@RequiredArgsConstructor
public class BoothSeedController {

	private final BoothRepository boothRepository;

	@Value("${app.admin.seed-key}")
	private String seedKey;

	// 인증키 확인
	private void validateKey(String key) {
		if (!seedKey.equals(key)) {
			throw new SecurityException("Invalid seed key");
		}
	}

	@PostMapping("/booths")
	public ResponseEntity<?> insertBooths(
		@RequestHeader("X-ADMIN-KEY") String key,
		@RequestBody List<BoothRequest> boothRequests) {

		validateKey(key);

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

		return ResponseEntity.ok(BaseResponse.success("Booths inserted!"));
	}


}
