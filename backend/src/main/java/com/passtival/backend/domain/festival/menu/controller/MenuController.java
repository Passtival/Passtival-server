package com.passtival.backend.domain.festival.menu.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.festival.menu.model.entity.Menu;
import com.passtival.backend.domain.festival.menu.model.response.MenuResponseDTO;
import com.passtival.backend.domain.festival.menu.service.MenuService;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.common.BaseResponseStatus;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/passtival")
public class MenuController {

	private final MenuService menuService;

	/**
	 * 부스 전체 메뉴 목록 조회
	 */
	@GetMapping("/menu")
	public BaseResponse<List<MenuResponseDTO>> getAllMenu() {
		List<Menu> menus = menuService.getAllMenu();
		List<MenuResponseDTO> menuResponseDTOS = menus.stream()
			.map(MenuResponseDTO::of)
			.collect(Collectors.toList());
		return BaseResponse.success(menuResponseDTOS);
	}

	/**
	 * 부스 메뉴 id로 목록 조회
	 */
	@GetMapping("/menu/{id}")
	public BaseResponse<MenuResponseDTO> getMenuById(@PathVariable Long id) {
		try {
			MenuResponseDTO booth = menuService.getMenuById(id);
			return BaseResponse.success(booth);
		} catch (RuntimeException e) {
			return BaseResponse.fail(BaseResponseStatus.PRIZE_NOT_FOUND);
		}
	}
}
