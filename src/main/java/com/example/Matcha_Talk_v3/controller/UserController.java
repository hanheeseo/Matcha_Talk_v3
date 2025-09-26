package com.example.Matcha_Talk_v3.controller;

import com.example.Matcha_Talk_v3.domain.dto.LoginRequestDto;
import com.example.Matcha_Talk_v3.domain.dto.UserRegistrationRequestDto;
import com.example.Matcha_Talk_v3.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @GetMapping("/exists")
    public ResponseEntity<Map<String, Boolean>> checkLoginIdExists(@RequestParam("loginId") String loginId) {
        boolean exists = userService.loginIdExists(loginId);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}
