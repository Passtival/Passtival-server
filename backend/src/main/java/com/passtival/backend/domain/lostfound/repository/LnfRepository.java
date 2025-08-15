package com.passtival.backend.domain.lostfound.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.passtival.backend.domain.lostfound.model.entity.FoundItem;

public interface LnfRepository extends JpaRepository<FoundItem, Long> {

}
