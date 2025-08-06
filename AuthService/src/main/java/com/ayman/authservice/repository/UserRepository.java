package com.ayman.authservice.repository;

import com.ayman.authservice.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByEmail(String email);
    User findUserByUsername(String username);
    User findUserByPhoneNumber(String phoneNumber);
    User findUserById(Long id);
}
