package com.email.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.email.auth.entity.UserInfo;

public interface UserRepository extends JpaRepository<UserInfo, Long> {
	
	Optional<UserInfo> findByEmail(String username);

}
