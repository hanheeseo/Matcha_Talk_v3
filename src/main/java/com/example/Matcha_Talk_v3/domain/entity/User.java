package com.example.Matcha_Talk_v3.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_pid")
    private Long userPid;

    @Column(name = "login_id", nullable = false, unique = true, length = 30)
    private String loginId;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "nick_name", nullable = false, length = 30)
    private String nickName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;

    @Column(name = "gender", nullable = false, length = 1)
    private String gender;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "email_verified")
    @ColumnDefault("0")
    private Boolean emailVerified;

    @Column(name = "language_code", length = 8)
    private String languageCode;

    @Column(name = "failed_login_count")
    @ColumnDefault("0")
    private Integer failedLoginCount;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "enabled")
    @ColumnDefault("1")
    private Boolean enabled;

    @Column(name = "rolename", length = 30)
    @ColumnDefault("'ROLE_USER'")
    private String rolename;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
