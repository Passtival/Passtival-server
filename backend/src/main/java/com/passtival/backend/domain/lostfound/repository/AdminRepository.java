package com.passtival.backend.domain.lostfound.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.passtival.backend.domain.lostfound.model.entity.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

	Optional<Admin> findByLoginId(String loginId);

	boolean existsByLoginId(String loginId);
}
