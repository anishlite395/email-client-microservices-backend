package com.config.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.config.service.entity.UserImapConfig;
import com.config.service.entity.UserSmtpConfig;
import com.config.service.service.MailConfigService;

@RestController
@RequestMapping("/config")
public class MailConfigController {
	
	@Autowired
	private MailConfigService service;

	@PostMapping("/smtp/{email}")
	public ResponseEntity<Void> saveSmtp(@PathVariable String email,
								@RequestBody UserSmtpConfig config){

		service.saveSmtpConfig(email,config);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/smtp/{email}")
	public UserSmtpConfig getSmtp(@PathVariable String email) {
		return service.getSmtpConfig(email);
		
	}
	
	@GetMapping("/imap/{email}")
	public UserImapConfig getImap(@PathVariable String email) {
		return service.getImapConfig(email);	
	}
	
	@PostMapping("/imap/{email}")
	public ResponseEntity<Void> saveSmtp(@PathVariable String email,
								@RequestBody UserImapConfig config){

		service.saveImapConfig(email,config);
		return ResponseEntity.ok().build();
	}
}
