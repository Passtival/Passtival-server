package com.passtival.backend.domain.festival.booth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.passtival.backend.domain.festival.booth.model.entity.Booth;


public interface BoothRepository extends JpaRepository<Booth, Long> {

	Optional<Booth> findByName(String name);
}
