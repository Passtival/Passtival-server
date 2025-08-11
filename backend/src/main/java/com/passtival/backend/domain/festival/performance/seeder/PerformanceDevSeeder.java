package com.passtival.backend.domain.festival.performance.seeder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.passtival.backend.domain.festival.performance.model.Performance;
import com.passtival.backend.domain.festival.performance.repository.PerformanceRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class PerformanceDevSeeder {

	private final PerformanceRepository performanceRepository;

	@SuppressWarnings("unused") // init() 메서드가 다른 코드에서 직접 호출되는 흔적이 없기 때문에 생기는 경고 제거
	@PostConstruct
	@Transactional
	public void init() {
		List<Performance> seeds = new ArrayList<>();
		seeds.add(build("오프닝 무대", "DJ PSSV", "메인", "images/opening.jpg",
			LocalDateTime.of(2025, 8, 30, 17, 0),
			LocalDateTime.of(2025, 8, 30, 18, 0)));
		seeds.add(build("썸머 나이트", "Sunset Band", "서브A", "images/summer_night.jpg",
			LocalDateTime.of(2025, 8, 30, 19, 0),
			LocalDateTime.of(2025, 8, 30, 20, 0)));
		seeds.add(build("미드나잇 스페셜", "Luna & Co", "메인", "images/midnight.jpg",
			LocalDateTime.of(2025, 8, 31, 0, 0),
			LocalDateTime.of(2025, 8, 31, 1, 0)));

		for (Performance p : seeds) {
			boolean exists = performanceRepository
				.findByTitleAndStartAt(p.getTitle(), p.getStartAt())
				.isPresent();
			if (!exists) {
				performanceRepository.save(p);
			}
		}

	}

	private Performance build(String title, String artist, String area, String imagePath,
		LocalDateTime startAt, LocalDateTime endAt) {
		Performance p = new Performance();
		p.setTitle(title);
		p.setArtist(artist);
		p.setArea(area);
		p.setImagePath(imagePath);
		p.setStartAt(startAt);
		p.setEndAt(endAt);
		return p;
	}
}
