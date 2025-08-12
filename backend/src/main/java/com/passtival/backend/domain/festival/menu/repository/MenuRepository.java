package com.passtival.backend.domain.festival.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.passtival.backend.domain.festival.menu.model.entity.Menu;

public interface MenuRepository extends JpaRepository<Menu, Long> {

}
