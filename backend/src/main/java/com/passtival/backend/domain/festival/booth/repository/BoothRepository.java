package com.passtival.backend.domain.festival.booth.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.passtival.backend.domain.festival.booth.model.entity.Booth;

public interface BoothRepository extends JpaRepository<Booth, Long> {

	@Query("SELECT b FROM Booth b " + "WHERE (:cursorId IS NULL OR b.id < :cursorId) " + "ORDER BY b.id ASC")
	List<Booth> findPageByCursor(@Param("cursorId") Long cursorId, Pageable pageable);

	boolean existsByName(String name);

}
