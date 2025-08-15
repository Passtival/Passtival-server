package com.passtival.backend.domain.festival.menu.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.passtival.backend.domain.festival.menu.model.entity.Menu;
import com.passtival.backend.domain.festival.menu.model.response.MenuResponse;
import com.passtival.backend.domain.festival.menu.repository.MenuRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuService {

	private final MenuRepository menuRepository;

	/**
	 * 모든 메뉴 목록 조회
	 */
	public List<Menu> getAllMenu() {
		try {
			return menuRepository.findAll();
		} catch (Exception e) {
			throw new RuntimeException("상품 목록 조회 중 오류가 발생했습니다.", e);
		}
	}

	/**
	 * 메뉴 id로 단일 조회
	 */
	public MenuResponse getMenuById(Long menuId) {
		Menu menu = menuRepository.findById(menuId)
			.orElseThrow(() -> new IllegalArgumentException("해당 ID의 메뉴가 없습니다."));
		return MenuResponse.of(menu);
	}

}
