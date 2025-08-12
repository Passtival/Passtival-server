package com.passtival.backend.domain.admin.controller;


import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.domain.matching.scheduler.MatchingScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//매칭 관련 테스트 컨트롤러
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MatchingScheduler matchingScheduler;

    //매칭 진행 시키기
    @GetMapping("/executeMatching")
    public BaseResponse<String> manualMatching() {
        matchingScheduler.executeMatching();
        return BaseResponse.success("매칭 실행 완료");
    }

    //일일 데이터 초기화
    @GetMapping("/cleanupMatching")
    public BaseResponse<String> manualCleanup() {
        matchingScheduler.dailyCleanup();
        return BaseResponse.success("데이터 정리 완료");
    }
}
