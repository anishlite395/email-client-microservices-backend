package com.email.service.repository;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.email.service.entity.ScheduledEmail;

@Repository
public interface ScheduledEmailRepository extends JpaRepository<ScheduledEmail, Long> {

	List<ScheduledEmail> findByStatusAndScheduledAtLessThanEqual(String string, LocalDateTime now);
	
	List<ScheduledEmail> findByStatus(String status);

}
