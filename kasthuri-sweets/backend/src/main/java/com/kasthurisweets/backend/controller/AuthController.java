package com.kasthurisweets.backend.controller;

import com.kasthurisweets.backend.dto.LoginRequest;
import com.kasthurisweets.backend.dto.LoginResponse;
import com.kasthurisweets.backend.dto.RegisterRequest;
import com.kasthurisweets.backend.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        userService.register(request);
        return "User registered successfully";
    }


    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return userService.login(request);



    }

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam String token) {
        userService.verifyEmail(token);
        return "Email verified successfully. You can now login.";
    }

}
