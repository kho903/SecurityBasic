package com.jikim.security1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jikim.security1.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	User findByUsername(String username);
}
