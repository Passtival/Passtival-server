package com.passtival.backend.domain.raffle.service;

import org.springframework.stereotype.Service;

import com.passtival.backend.domain.authentication.service.AuthenticationService;
import com.passtival.backend.domain.raffle.model.entity.Applicant;
import com.passtival.backend.domain.raffle.model.request.ApplicantRegistrationRequest;
import com.passtival.backend.domain.raffle.repository.ApplicantRepository;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RaffleService {

	private final ApplicantRepository applicantRepository;
	private final AuthenticationService authenticationService;

	/**
	 * 신청자 등록
	 * @param request 신청자 등록 요청 정보
	 */
	@Transactional
	public void registerApplicant(ApplicantRegistrationRequest request) {
		// 1. 인증키 유효성 검사
		authenticationService.validateAuthenticationKey(request.getAuthenticationKey());

		// 2. 학번 중복 확인
		if (applicantRepository.existsByStudentId(request.getStudentId())) {
			throw new BaseException(BaseResponseStatus.DUPLICATE_APPLICANT);
		}

		// 3. 신청자 등록
		Applicant newApplicant = Applicant.builder()
			.applicantName(request.getApplicantName())
			.studentId(request.getStudentId())
			.build();

		applicantRepository.save(newApplicant);
	}

}
