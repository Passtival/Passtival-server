package com.passtival.backend.domain.member.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.passtival.backend.domain.member.model.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findBySocialId(String socialId);

	Optional<Member> findByMemberId(Long memberId);

	List<Member> findAllByLevel(int level);
}