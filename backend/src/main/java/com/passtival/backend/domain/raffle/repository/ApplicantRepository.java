package com.passtival.backend.domain.raffle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.passtival.backend.domain.raffle.model.entity.Applicant;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicant, Long> {

	/**
	 * 학번으로 지원자 존재 여부 확인
	 * @param studentId
	 * @return 지원자가 존재하면 true, 아니면 false
	 */
	boolean existsByStudentId(String studentId);
}
