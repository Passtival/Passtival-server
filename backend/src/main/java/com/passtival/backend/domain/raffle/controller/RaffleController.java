package com.passtival.backend.domain.raffle.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/raffle")
public class RaffleController {

	@GetMapping("/prizes")
	public void getPrizes() {
		// 추후에 실제 로직을 구현할 예정
	}

	@GetMapping("/prizes/{prizeId}")
	public void getPrizeById(@PathVariable("prizeId") Long prizeId) {
		// 추후에 실제 로직을 구현할 예정
	}

	@PostMapping("/applicants")
	public void saveApplicant() {
		// 추후에 실제 로직을 구현할 예정
	}

}
