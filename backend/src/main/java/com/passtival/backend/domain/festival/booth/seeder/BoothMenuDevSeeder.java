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
		// 	),
		//
		// 	// 1. 피시방
		// 	build(
		// 		"피사방", "놀이", "개발부",
		// 		LocalDateTime.of(2025, 8, 22, 9, 30),
		// 		LocalDateTime.of(2025, 8, 22, 19, 0),
		// 		"대신관 앞 광장", "학교 로고 굿즈 판매",
		// 		"/images/booth5.jpg", "/images/location5.jpg",
		// 		List.of(
		// 			menuSpec("상품", "마우스", 8000),
		// 			menuSpec("상품", "키보드", 3000)
		// 		)
		// 	),
		//
		// 	// 2. 디저트 카페
		// 	build(
		// 		"달콤카페", "디저트", "제과동아리",
		// 		LocalDateTime.of(2025, 8, 20, 12, 0),
		// 		LocalDateTime.of(2025, 8, 20, 19, 0),
		// 		"본관 앞 잔디밭", "케이크, 아이스크림, 쿠키 판매",
		// 		"/images/booth7.jpg", "/images/location7.jpg",
		// 		List.of(
		// 			menuSpec("디저트", "치즈케이크", 5500),
		// 			menuSpec("디저트", "아이스크림", 3000),
		// 			menuSpec("디저트", "초코쿠키", 2000)
		// 		)
		// 	),
		//
		// 	// 3. 게임 존
		// 	build(
		// 		"게임 존", "체험", "게임동아리",
		// 		LocalDateTime.of(2025, 8, 21, 10, 0),
		// 		LocalDateTime.of(2025, 8, 21, 18, 0),
		// 		"체육관 안", "다트, 제기차기, 미니게임",
		// 		"/images/booth4.jpg", "/images/location4.jpg",
		// 		List.of(
		// 			menuSpec("체험", "다트게임", 2000),
		// 			menuSpec("체험", "제기차기", 1000)
		// 		)
		// 	),
		//
		// 	// 4. 굿즈샵
		// 	build(
		// 		"굿즈샵", "상품", "홍보부",
		// 		LocalDateTime.of(2025, 8, 22, 9, 30),
		// 		LocalDateTime.of(2025, 8, 22, 19, 0),
		// 		"도서관 앞 광장", "학교 로고 굿즈 판매",
		// 		"/images/booth5.jpg", "/images/location5.jpg",
		// 		List.of(
		// 			menuSpec("상품", "에코백", 8000),
		// 			menuSpec("상품", "키링", 3000)
		// 		)
		// 	),
		//
		//
		// 	// 5. 전통 체험 부스
		// 	build(
		// 		"전통 체험 부스", "체험", "문화동아리",
		// 		LocalDateTime.of(2025, 8, 23, 10, 0),
		// 		LocalDateTime.of(2025, 8, 23, 17, 0),
		// 		"대강당 앞", "한복 체험 및 전통 놀이",
		// 		"/images/booth6.jpg", "/images/location6.jpg",
		// 		List.of(
		// 			menuSpec("체험", "한복체험", 5000),
		// 			menuSpec("체험", "투호놀이", 2000)
		// 		)
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
