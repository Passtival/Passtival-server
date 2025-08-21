package com.passtival.backend.domain.festival.performance.seeder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.passtival.backend.domain.festival.performance.model.entity.Performance;
import com.passtival.backend.domain.festival.performance.model.entity.Song;
import com.passtival.backend.domain.festival.performance.repository.PerformanceRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class PerformanceDevSeeder implements ApplicationRunner {

	private final PerformanceRepository performanceRepository;

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		List<Performance> seeds = List.of(
			build(
				"신석기", "글로벌경영학과", "아리관", "images/opening.jpg",
				LocalDateTime.of(2025, 8, 30, 17, 0),
				LocalDateTime.of(2025, 8, 30, 18, 0),
				"축제 시작을 알리는 퍼포먼스",
				1,
				List.of(songSpec("아이유", "좋은 날"), songSpec("블랙핑크", "붐바야"))
			),
			build(
				"밴드 무대", "기타동아리", "야외무대 A", "images/band.jpg",
				LocalDateTime.of(2025, 8, 30, 18, 30),
				LocalDateTime.of(2025, 8, 30, 19, 30),
				"동아리 밴드의 라이브 공연",
				2,
				List.of(songSpec("Coldplay", "Viva La Vida"), songSpec("잔나비", "주저하는 연인들을 위해"))
			),
			build(
				"댄스 공연", "무용학과", "메인 광장", "images/dance.jpg",
				LocalDateTime.of(2025, 8, 30, 20, 0),
				LocalDateTime.of(2025, 8, 30, 21, 0),
				"화려한 댄스 퍼포먼스",
				3,
				List.of(songSpec("BTS", "Dynamite"), songSpec("NewJeans", "Super Shy"))
			),
			build(
				"연극 무대", "연극영화과", "소극장", "images/play.jpg",
				LocalDateTime.of(2025, 8, 31, 14, 0),
				LocalDateTime.of(2025, 8, 31, 16, 0),
				"학생들이 직접 준비한 연극 공연",
				4,
				List.of(songSpec("뮤지컬팀", "오페라의 유령 OST"))
			),
			build(
				"피날레", "축제준비위원회", "운동장", "images/finale.jpg",
				LocalDateTime.of(2025, 8, 31, 20, 0),
				LocalDateTime.of(2025, 8, 31, 21, 30),
				"불꽃놀이와 함께하는 마지막 무대",
				5,
				List.of(songSpec("싸이", "강남스타일"), songSpec("Queen", "We Are The Champions"))
			)
		);


		for (Performance p : seeds) {
			boolean exists = performanceRepository
				.findByTitleAndDay(p.getTitle(), p.getDay())
				.isPresent();
			if (!exists) {
				performanceRepository.save(p);
			}
		}
		performanceRepository.flush();
	}

	private Performance build(
		String title, String artist, String area, String imagePath,
		LocalDateTime startTime, LocalDateTime endTime,
		String introduction, Integer day, List<SongSpec> songSpecs
	) {
		Performance performance = Performance.builder()
			.title(title).artist(artist).area(area)
			.imagePath(imagePath).startTime(startTime).endTime(endTime)
			.introduction(introduction).day(day)
			.build();

		if (songSpecs != null) {
			for (SongSpec spec : songSpecs) {
				Song song = Song.builder()
					.singer(spec.singer())
					.title(spec.title())
					.build();
				performance.addSong(song);
			}
		}
		return performance;
	}

	// SongSpec: Seeder 내부에서만 사용하는 임시 데이터 구조
	// - 자바 record 문법 사용 → 불변 데이터 클래스 자동 생성 (생성자, getter, equals, hashCode, toString 포함)
	// - 가수(singer), 곡명(title)만 저장하는 값 객체
	// - static 으로 선언하여 외부 접근 차단, Seeder 클래스 내부에서만 사용
	private static SongSpec songSpec(String singer, String title) {
		return new SongSpec(singer, title);
	}

	// songSpec(): SongSpec 객체 생성용 팩토리 메서드
	private static record SongSpec(String singer, String title) {}

}
