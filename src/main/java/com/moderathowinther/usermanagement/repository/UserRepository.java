package com.moderathowinther.usermanagement.repository;

import com.moderathowinther.usermanagement.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    void deleteByEmail(String email);
}
