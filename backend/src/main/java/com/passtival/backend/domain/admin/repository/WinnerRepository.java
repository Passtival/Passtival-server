package com.passtival.backend.domain.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.passtival.backend.domain.admin.model.entity.Winner;

public interface WinnerRepository extends JpaRepository<Winner, Long> {
	// 가장 최근에 추가된 당첨자 조회
	Winner findTopByDayOrderByIdDesc(int day);
}
