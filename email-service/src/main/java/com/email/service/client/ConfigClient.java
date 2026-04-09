package com.email.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.email.service.dto.UserImapConfig;
import com.email.service.dto.UserSmtpConfig;

@FeignClient(name="config-service",url="http://localhost:8082")
public interface ConfigClient {

	@GetMapping("/config/smtp/{email}")
	UserSmtpConfig getSmtpConfig(@PathVariable String email);
	
	@GetMapping("/config/imap/{email}")
	UserImapConfig getImapConfig(@PathVariable String email);

	@PostMapping("/config/imap/{email}")
	void saveImapConfig(@PathVariable String email,UserImapConfig config);
	
	@PostMapping("/config/smtp/{email}")
	void saveSmtpConfig(@PathVariable String email,UserSmtpConfig config);
	
	
}
