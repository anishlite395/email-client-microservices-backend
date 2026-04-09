package com.config.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.config.service.entity.UserSmtpConfig;

@Repository
public interface UserSmtpConfigRepository extends JpaRepository<UserSmtpConfig, Long> {

	Optional<UserSmtpConfig> findByUserEmail(String email);

}
