package com.passtival.backend.domain.matching.model.request;

import com.passtival.backend.domain.matching.model.enums.Gender;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor  // Jackson 역직렬화를 위한 기본 생성자
public class MemberPatchRequest {

    /* 
     * 필드를 보내지 않으면 null로 처리되어 해당 필드는 수정하지 않음
     * 빈 문자열("")을 보내면 해당 필드를 비우는 것으로 처리
     */
    
    // enum이라서 @Size, @Pattern 미사용
    private Gender gender;

    @Pattern(
        regexp = "^(?:$|010-\\d{4}-\\d{4})$",
        message = "전화번호는 빈 값이거나 다음 형식여야 합니다: '010-1234-5678'"
    )
    @Size(
        min = 0,
        max = 13,
        message = "전화번호는 빈 문자열이거나 길이 13자여야 합니다."
    )
    private String phoneNumber;

    @Pattern(
        regexp = "^[a-zA-Z0-9._]*$",
        message = "인스타그램 ID는 영문, 숫자, '.', '_'만 사용 가능합니다."
    )
    @Size(
        min = 0,
        max = 30,
        message = "인스타그램 ID는 빈 문자열이거나 길이 30자 내여야 합니다."
    )
    private String instagramId;
}