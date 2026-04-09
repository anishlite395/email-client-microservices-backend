package com.email.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.email.service.entity.SentEmail;

public interface SentEmailRepository extends JpaRepository<SentEmail, Long> {

}
