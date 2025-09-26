package com.example.Matcha_Talk_v3.domain.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRegistrationRequestDto {
    private String loginId;
    private String password;
    private String nickName;
    private String email;
    private String countryCode;
    private String gender;
    private LocalDate birthDate;
}
