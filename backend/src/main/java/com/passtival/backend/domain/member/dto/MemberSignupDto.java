package com.passtival.backend.domain.member.dto;

import com.passtival.backend.domain.member.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class MemberSignupDto {
    @NotNull(message = "성별은 필수입니다.")
    private Gender gender;

    @NotBlank(message = "전화번호는 필수입니다.")
    @Size(max = 20, message = "전화번호에 지정된 길이를 초과할 수 없습니다.")
    private String phoneNumber;

    @Size(max = 35, message = "인스타그램 ID의 최대 길이를 초과할 수 없습니다.")
    private String instagramId;
}