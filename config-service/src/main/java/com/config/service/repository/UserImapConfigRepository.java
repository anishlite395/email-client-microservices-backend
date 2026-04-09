package com.config.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.config.service.entity.UserImapConfig;

@Repository
public interface UserImapConfigRepository extends JpaRepository<UserImapConfig, Long> {

	Optional<UserImapConfig> findByUserEmail(String email);
}
