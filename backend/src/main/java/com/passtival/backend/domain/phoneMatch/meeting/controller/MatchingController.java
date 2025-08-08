package com.passtival.backend.domain.phoneMatch.meeting.controller;
import com.passtival.backend.domain.phoneMatch.meeting.entity.PhoneMatchUser;
import com.passtival.backend.domain.phoneMatch.meeting.service.MatchingService;
import com.passtival.backend.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 매칭 관련 API를 처리하는 컨트롤러
 * 프로젝트의 BaseResponse 응답 규격화를 따라 일관된 응답 구조 제공
 */
@RestController
@RequestMapping("/api/matching")
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;

    @PostMapping("/signup")
    public BaseResponse<String> signup(@RequestBody PhoneMatchUser user) {
        return matchingService.registerUser(user);
    }

    @PostMapping("/apply")
    public BaseResponse<String> applyMatching(
            @RequestHeader("Authorization") String token,
            @RequestBody PhoneMatchUser requestUser) {
        return matchingService.applyMatching(token, requestUser);
    }
}