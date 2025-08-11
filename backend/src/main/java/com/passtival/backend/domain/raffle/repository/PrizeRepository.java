package com.passtival.backend.domain.raffle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.passtival.backend.domain.raffle.model.entity.Prize;

@Repository
public interface PrizeRepository extends JpaRepository<Prize, Long> {
}
