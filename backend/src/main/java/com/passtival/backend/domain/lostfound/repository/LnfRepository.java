package com.passtival.backend.domain.lostfound.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.passtival.backend.domain.lostfound.model.entity.FoundItem;

public interface LnfRepository extends JpaRepository<FoundItem, Long> {

	List<FoundItem> findAllByOrderByCreatedAtDesc();
}
