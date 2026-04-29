package com.dailyq.controller;

import com.dailyq.dto.LoginRequest;
import com.dailyq.dto.LoginResponse;
import com.dailyq.dto.Result;
import com.dailyq.service.UserService;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return Result.success(response);
    }
}
