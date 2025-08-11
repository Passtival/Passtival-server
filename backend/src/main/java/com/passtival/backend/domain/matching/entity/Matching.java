package com.passtival.backend.domain.matching.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "matching")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Matching {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resultId;

    @Column(name = "male_id", nullable = false)
    private Long maleId; // 남성 또는 첫 번째 사용자

    @Column(name = "female_id", nullable = false)
    private Long femaleId; // 여성 또는 두 번째 사용자

    @Column(name = "matching_date", nullable = false)
    private LocalDate matchingDate;
}
