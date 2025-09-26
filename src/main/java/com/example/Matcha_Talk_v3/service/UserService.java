package com.example.Matcha_Talk_v3.service;

import com.example.Matcha_Talk_v3.domain.dto.LoginRequestDto;
import com.example.Matcha_Talk_v3.domain.dto.UserRegistrationRequestDto;
import com.example.Matcha_Talk_v3.domain.entity.User;
import com.example.Matcha_Talk_v3.exception.DuplicateEmailException;
import com.example.Matcha_Talk_v3.exception.DuplicateLoginIdException;
import com.example.Matcha_Talk_v3.exception.InvalidPasswordException;
import com.example.Matcha_Talk_v3.exception.UserNotFoundException;
import com.example.Matcha_Talk_v3.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(UserRegistrationRequestDto requestDto) {
        userRepository.findByLoginId(requestDto.getLoginId()).ifPresent(user -> {
            throw new DuplicateLoginIdException("Login ID already exists");
        });

        userRepository.findByEmail(requestDto.getEmail()).ifPresent(user -> {
            throw new DuplicateEmailException("Email already exists");
        });

        User user = User.builder()
                .loginId(requestDto.getLoginId())
                .passwordHash(passwordEncoder.encode(requestDto.getPassword()))
                .nickName(requestDto.getNickName())
                .email(requestDto.getEmail())
                .countryCode(requestDto.getCountryCode())
                .gender(requestDto.getGender())
                .birthDate(requestDto.getBirthDate())
                .build();
        userRepository.save(user);
    }

    public String login(LoginRequestDto requestDto) {
        User user = userRepository.findByLoginId(requestDto.getLoginId())
                .orElseThrow(() -> new UserNotFoundException("User not found with login ID: " + requestDto.getLoginId()));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPasswordHash())) {
            throw new InvalidPasswordException("Invalid password");
        }

        // For now, just return a success message. JWT generation will be implemented later.
        return "Login successful";
    }
}
