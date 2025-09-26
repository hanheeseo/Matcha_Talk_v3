package com.example.Matcha_Talk_v3.controller;

import com.example.Matcha_Talk_v3.domain.dto.LoginRequestDto;
import com.example.Matcha_Talk_v3.domain.dto.UserRegistrationRequestDto;
import com.example.Matcha_Talk_v3.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationRequestDto requestDto) {
        userService.registerUser(requestDto);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto requestDto) {
        String successMessage = userService.login(requestDto);
        return ResponseEntity.ok(successMessage);
    }
}
