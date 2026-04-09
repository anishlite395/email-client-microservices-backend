package com.inbox.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.inbox.service.dto.UserImapConfig;

@FeignClient(name="config-service",url="http://localhost:8082")
public interface ConfigClient {
	
	@GetMapping("/config/imap/{email}")
	UserImapConfig getImap(@PathVariable String email);

}
