package com.passtival.backend.domain.matching.controller.test;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.matching.model.entity.Member;
import com.passtival.backend.domain.matching.model.enums.Gender;
import com.passtival.backend.domain.matching.repository.MemberRepository;
import com.passtival.backend.domain.matching.service.MatchingScheduler;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.common.enums.Role;
import com.passtival.backend.global.exception.BaseException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/test/matching")
@RequiredArgsConstructor
@Tag(name = "Matching-Test-API", description = "매칭 테스트용 API")
public class MatchingTestController {

	private final MemberRepository memberRepository;
	private final MatchingScheduler matchingScheduler;

	@Operation(summary = "테스트 회원 데이터 생성", description = "매칭 테스트를 위한 남성/여성 회원 데이터를 생성합니다.")
	@GetMapping("/create-test-members")
	public BaseResponse<String> createTestMembers() {
		try {
			List<Member> testMembers = new ArrayList<>();

			// 남성 회원 3명 생성
			for (int i = 1; i <= 34; i++) {
				Member male = Member.builder()
					.socialId("test_male_" + String.format("%03d", i))
					.name("테스트남성" + i)
					.gender(Gender.MALE)
					.phoneNumber("010-1111-" + String.format("%04d", 1111 + i - 1))
					.instagramId("male_insta_" + i)
					.applied(false)
					.appliedAt(null)
					.role(Role.USER)
					.build();
				testMembers.add(male);
			}

			// 여성 회원 3명 생성
			for (int i = 1; i <= 30; i++) {
				Member female = Member.builder()
					.socialId("test_female_" + String.format("%03d", i))
					.name("테스트여성" + i)
					.gender(Gender.FEMALE)
					.phoneNumber("010-2222-" + String.format("%04d", 1111 + i - 1))
					.instagramId("female_insta_" + i)
					.applied(false)
					.appliedAt(null)
					.role(Role.USER)
					.build();
				testMembers.add(female);
			}

			memberRepository.saveAll(testMembers);

			return BaseResponse.success("테스트 회원 생성 완료!");

		} catch (Exception e) {
			return BaseResponse.fail(BaseResponseStatus.INTERNAL_SERVER_ERROR, "테스트 회원 생성 실패: " + e.getMessage());
		}
	}

	@Operation(summary = "모든 테스트 회원 매칭 신청", description = "생성된 모든 테스트 회원들이 매칭에 신청합니다.")
	@PostMapping("/apply-all-test-members")
	public BaseResponse<String> applyAllTestMembers() {
		try {
			List<Member> testMembers = memberRepository.findByNameStartingWith("테스트");

			if (testMembers.isEmpty()) {
				return BaseResponse.fail(BaseResponseStatus.BAD_REQUEST, "테스트 회원이 없습니다. 먼저 테스트 회원을 생성해주세요.");
			}

			for (Member member : testMembers) {
				member.applyForMatching();
			}

			memberRepository.saveAll(testMembers);

			return BaseResponse.success(testMembers.size() + "명의 테스트 회원이 매칭에 신청했습니다!");

		} catch (Exception e) {
			return BaseResponse.fail(BaseResponseStatus.INTERNAL_SERVER_ERROR, "테스트 회원 매칭 신청 실패: " + e.getMessage());
		}
	}

	/**
	 * 수동 매칭 실행 API
	 * 6시 이전에도 매칭을 강제로 실행할 수 있음
	 */
	@Operation(
		summary = "수동 매칭 실행",
		description = "스케줄러와 별개로 매칭을 수동으로 실행합니다. 관리자 권한이 필요합니다.",
		security = @SecurityRequirement(name = "jwtAuth")
	)
	@GetMapping("/start")
	public BaseResponse<String> manualMatching() throws BaseException {
		// 이미 매칭이 진행 중인지 확인
		if (matchingScheduler.isMatchingInProgress()) {
			throw new BaseException(BaseResponseStatus.BAD_REQUEST);
		}

		// 매칭 스케줄러의 dailyMatching 메서드 호출
		matchingScheduler.dailyMatching();

		return BaseResponse.success("매칭이 성공적으로 실행되었습니다.");
	}

	/**
	 * 수동 정리 실행 API
	 * 매칭 데이터를 수동으로 정리할 수 있음
	 */
	@Operation(
		summary = "수동 매칭 데이터 정리",
		description = "당일 매칭 데이터를 수동으로 정리합니다. 관리자 권한이 필요합니다.",
		security = @SecurityRequirement(name = "jwtAuth")
	)
	@GetMapping("/cleanup")
	public BaseResponse<String> manualCleanup() {

		// 매칭 스케줄러의 dailyCleanup 메서드 호출
		matchingScheduler.dailyCleanup();

		return BaseResponse.success("매칭 데이터가 성공적으로 정리되었습니다.");
	}

	@Operation(summary = "테스트 회원 목록 조회", description = "생성된 테스트 회원들의 목록을 조회합니다.")
	@GetMapping("/test-members")
	public BaseResponse<List<String>> getTestMembers() {
		try {
			List<Member> testMembers = memberRepository.findByNameStartingWith("테스트");
			List<String> memberInfo = new ArrayList<>();

			for (Member member : testMembers) {
				String info = String.format("%s (%s) - 신청여부: %s",
					member.getName(), member.getGender(), member.isApplied());
				memberInfo.add(info);
			}

			return BaseResponse.success(memberInfo);

		} catch (Exception e) {
			return BaseResponse.fail(BaseResponseStatus.INTERNAL_SERVER_ERROR, "테스트 회원 목록 조회 실패: " + e.getMessage());
		}
	}
}
