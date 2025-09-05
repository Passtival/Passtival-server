package com.passtival.backend.domain.admin.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.passtival.backend.domain.admin.model.entity.Winner;
import com.passtival.backend.domain.admin.model.response.WinnerResponse;
import com.passtival.backend.domain.admin.repository.WinnerRepository;
import com.passtival.backend.domain.member.model.entity.Member;
import com.passtival.backend.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminRaffleService {

	private final MemberRepository memberRepository;
	private final WinnerRepository winnerRepository;
	private final SecureRandom secureRandom = new SecureRandom();

	// === 일차별 응모 로직 === //
	@Transactional
	public void executeRaffleByDay(int day) {

		// 레벨이 1인 회원 모두 조회
		List<Member> level1Members = memberRepository.findAllByLevel(1);
		// 레벨이 2인 회원 모두 조회
		List<Member> level2Members = memberRepository.findAllByLevel(2);
		// 레벨이 3인 회원 모두 조회
		List<Member> level3Members = memberRepository.findAllByLevel(3);

		// 당첨 후보(candidate) 리스트 생성
		// level 1 회원은 1번, level 2 회원은 2번, level 3 회원은 3번 추가
		List<Member> candidates = createCandidateListByDay(level1Members, level2Members, level3Members);

		// 당첨자(Winner) 선정
		Winner winner = selectWinner(candidates, day);

		// 당첨자(Winner) 추가
		winnerRepository.save(winner);

	}

	private List<Member> createCandidateListByDay(List<Member> level1Members, List<Member> level2Members,
		List<Member> level3Members) {
		List<Member> candidates = new ArrayList<>();

		// 레벨 1: 1번 추가
		candidates.addAll(level1Members);

		// 레벨 2: 2번 추가 (확률 2배)
		candidates.addAll(level2Members);
		candidates.addAll(level2Members);

		// 레벨 3: 3번 추가 (확률 3배)
		candidates.addAll(level3Members);
		candidates.addAll(level3Members);
		candidates.addAll(level3Members);

		// 리스트 섞기
		Collections.shuffle(candidates, secureRandom);

		return candidates;
	}

	public WinnerResponse getRaffleWinnersByDay(int day) {
		// day에 해당하며, id값이 가장 큰 당첨자 조회
		Winner winner = winnerRepository.findTopByDayOrderByIdDesc(day);

		return new WinnerResponse(winner.getName(), winner.getStudentId());

	}

	// === 프리미엄 응모 로직 === //
	public void executeRaffleOfPremium() {
		List<Member> premiumCandidates = memberRepository.findAllByPremiumRaffleTrue();
		Winner winner = selectWinner(premiumCandidates, 4);
		winnerRepository.save(winner);

	}

	public WinnerResponse getRaffleWinnerOfPremium() {
		Winner winner = winnerRepository.findTopByDayOrderByIdDesc(4);
		return new WinnerResponse(winner.getName(), winner.getStudentId());
	}

	// === 공통 로직 === //
	private Winner selectWinner(List<Member> candidates, int day) {
		int randomIndex = secureRandom.nextInt(candidates.size());
		Member member = candidates.get(randomIndex);
		return new Winner(member.getName(), member.getStudentId(), day);
	}
}
