package com.passtival.backend.domain.phoneMatch.meeting.repository;

import com.passtival.backend.domain.phoneMatch.meeting.entity.PhoneMatchUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhoneMatchUserRepository extends JpaRepository<PhoneMatchUser, Long> {
    Optional<PhoneMatchUser> findByPhoneNumber(String phoneNumber);
}
