package com.passtival.backend.domain.festival.booth.seeder;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.passtival.backend.domain.festival.booth.model.entity.Booth;
import com.passtival.backend.domain.festival.booth.repository.BoothRepository;
import com.passtival.backend.domain.festival.menu.model.entity.Menu;

import lombok.RequiredArgsConstructor;

@Component
@Profile("dev") // 개발 환경에서만 동작
@RequiredArgsConstructor
public class BoothMenuDevSeeder implements ApplicationRunner {

	private final BoothRepository boothRepository;

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		// List<Booth> seeds = List.of(
		// 	build(
		// 		"푸드트럭", "음식", "외부",
		// 		LocalDateTime.of(2025, 8, 20, 10, 0),
		// 		LocalDateTime.of(2025, 8, 20, 18, 0),
		// 		"메인 광장", "핫도그, 감자튀김, 음료 판매",
		// 		"/images/booth1.jpg", "/images/location1.jpg",
		// 		List.of(menuSpec("음식", "핫도그", 5000),
		// 			menuSpec("음식", "감자튀김", 3000))
		// 	),
		// 	build(
		// 		"커피 부스", "음료", "학생회",
		// 		LocalDateTime.of(2025, 8, 20, 9, 0),
		// 		LocalDateTime.of(2025, 8, 20, 17, 0),
		// 		"학생회관 앞", "커피, 라떼, 차 판매",
		// 		"/images/booth2.jpg", "/images/location2.jpg",
		// 		List.of(menuSpec("음료", "아메리카노", 4000),
		// 			menuSpec("음료", "카페라떼", 4500))
		// 	)
		// );
		//
		// for (Booth booth : seeds) {
		// 	boolean exists = boothRepository
		// 		.findByName(booth.getName())
		// 		.isPresent();
		// 	if (!exists) {
		// 		boothRepository.save(booth);
		// 	}
		// }
		// boothRepository.flush();
	}

	private Booth build(
		String name, String type, String department,
		LocalDateTime startTime, LocalDateTime endTime,
		String location, String info,
		String imagePath, String locationImagePath,
		List<MenuSpec> menuSpecs
	) {
		Booth booth = Booth.builder()
			.name(name)
			.type(type)
			.department(department)
			.operatingStart(startTime)
			.operatingEnd(endTime)
			.location(location)
			.info(info)
			.imagePath(imagePath)
			.locationImagePath(locationImagePath)
			.build();

		if (menuSpecs != null) {
			for (MenuSpec spec : menuSpecs) {
				Menu menu = Menu.builder()
					.type(spec.type())
					.name(spec.name())
					.price(spec.price())
					.build();
				booth.addMenu(menu);
			}
		}
		return booth;
	}

	// menuSpec(): MenuSpec 객체 생성용 팩토리 메서드
	private static MenuSpec menuSpec(String type, String name, int price) {
		return new MenuSpec(type, name, price);
	}

	// MenuSpec: 부스 메뉴 정보 담는 값 객체
	private static record MenuSpec(String type, String name, int price) {}
}
