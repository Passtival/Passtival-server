package com.passtival.backend.domain.matching.controller;
import com.passtival.backend.domain.matching.dto.MatchingDto;
import com.passtival.backend.domain.member.entity.Member;
import com.passtival.backend.domain.matching.service.MatchingService;
import com.passtival.backend.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 매칭 관련 API를 처리하는 컨트롤러
 * 프로젝트의 BaseResponse 응답 규격화를 따라 일관된 응답 구조 제공
 */
@Slf4j
@RestController
@RequestMapping("/api/matching")
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;


    @PostMapping("/apply")
    @PreAuthorize("hasRole('USER')")
    public BaseResponse<String> applyMatching(
            @AuthenticationPrincipal Long memberId,
            @RequestBody Member requestMember) {
        return matchingService.applyMatching(memberId, requestMember);
    }

    @GetMapping("/result")
    @PreAuthorize("hasRole('USER')")
    public BaseResponse<MatchingDto> getMatchingResult(
            @AuthenticationPrincipal Long memberId) {
        return matchingService.getMatchingResult(memberId);
    }
}