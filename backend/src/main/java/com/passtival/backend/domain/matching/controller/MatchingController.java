package com.passtival.backend.domain.matching.controller;
import com.passtival.backend.domain.matching.dto.MatchingResultDto;
import com.passtival.backend.domain.user.entity.User;
import com.passtival.backend.domain.matching.service.MatchingService;
import com.passtival.backend.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public BaseResponse<String> applyMatching(
            @RequestHeader("Authorization") String token,
            @RequestBody User requestUser) {
        return matchingService.applyMatching(token, requestUser);
    }
    @GetMapping("/result")
    public BaseResponse<MatchingResultDto> getMatchingResult(
            @RequestHeader("Authorization") String token) {
        return matchingService.getMatchingResult(token);
    }
}