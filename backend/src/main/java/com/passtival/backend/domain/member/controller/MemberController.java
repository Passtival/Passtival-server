package com.passtival.backend.domain.member.controller;

import com.passtival.backend.domain.member.entity.Member;
import com.passtival.backend.domain.member.service.MemberService;
import com.passtival.backend.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public BaseResponse<String> signup(@RequestBody Member member) {
        return memberService.registerMember(member);
    }

}
