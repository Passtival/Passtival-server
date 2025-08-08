package com.passtival.backend.domain.phoneMatch.meeting.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "meeting_result")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchingResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resultId;

    @Column(nullable = false)
    private Long userId1; // 남성 또는 첫 번째 사용자

    @Column(nullable = false)
    private Long userId2; // 여성 또는 두 번째 사용자

    @Column(nullable = false)
    private LocalDate matchingDate;
}
