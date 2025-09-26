package com.example.Matcha_Talk_v3.repository;

import com.example.Matcha_Talk_v3.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
