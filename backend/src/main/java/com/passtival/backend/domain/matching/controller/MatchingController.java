package com.passtival.backend.domain.matching.controller;
import com.passtival.backend.domain.matching.dto.MatchingApplyDto;
import com.passtival.backend.domain.matching.dto.MatchingDto;
import com.passtival.backend.domain.matching.service.MatchingService;
import com.passtival.backend.global.auth.security.CustomMemberDetails;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.exception.BaseException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "Matching-API", description = "매칭 관리 API")
@RestController
@RequestMapping("/api/matching")
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;

    /**
     * 매칭 신청 API
     * @param memberDetails 인증된 사용자 정보
     * @param matchingApplyDto 매칭 신청 요청 정보 (인스타그램 ID)
     * @return 매칭 신청 결과
     * @throws BaseException 매칭 신청 실패 시
     */
    @Operation(
            summary = "매칭 신청",
            description = "매칭 신청을 합니다. 인스타그램 ID는 선택사항입니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "매칭 신청 요청 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MatchingApplyDto.class),
                            examples = @ExampleObject(
                                    name = "매칭 신청 요청 예시",
                                    value = """
                {
                  "instagramId": "my_instagram_id"
                }
                """
                            )
                    )
            )
    )
    @PostMapping("/apply")
    @PreAuthorize("hasRole('USER')")
    public BaseResponse<String> applyMatching(
            @AuthenticationPrincipal CustomMemberDetails memberDetails,
            @Valid @RequestBody MatchingApplyDto matchingApplyDto) throws BaseException {
        matchingService.applyMatching(memberDetails.getMemberId(), matchingApplyDto);
        return BaseResponse.success(null);
    }

    /**
     * 매칭 결과 조회 API
     * @param memberDetails 인증된 사용자 정보
     * @return 매칭 결과 (내 정보 + 파트너 정보)
     * @throws BaseException 매칭 결과 조회 실패 시
     */

    @Operation(
            summary = "매칭 결과 조회",
            description = "오늘의 매칭 결과를 조회합니다. 매칭 성공 시 내 정보와 파트너 정보를 반환합니다."
    )
    @GetMapping("/result")
    @PreAuthorize("hasRole('USER')")
    public BaseResponse<MatchingDto> getMatchingResult (
            @AuthenticationPrincipal CustomMemberDetails memberDetails)throws BaseException {
        MatchingDto matchingDto = matchingService.getMatchingResult(memberDetails.getMemberId());
        return BaseResponse.success(matchingDto);
    }
}