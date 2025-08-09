package com.passtival.backend.domain.user.controller;

import com.passtival.backend.domain.user.entity.User;
import com.passtival.backend.domain.user.service.UserService;
import com.passtival.backend.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matching")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public BaseResponse<String> signup(@RequestBody User user) {
        return userService.registerUser(user);
    }

}
