package com.email.service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.email.service.dto.MailRequest;
import com.email.service.entity.ScheduledEmail;
import com.email.service.service.EmailService;

@RestController
@RequestMapping("/email")
public class EmailController {
	
	@Autowired
	private EmailService emailService;

	@PostMapping("/send")
	public ResponseEntity<String> send(@RequestHeader("X-User-Email") String email,
								@RequestBody MailRequest mailRequest){
		
		if(mailRequest.getScheduledAt() != null) {
			emailService.scheduleEmail(mailRequest,email);
			return ResponseEntity.ok("Email Scheduled");
		}
		else {
			emailService.sendEmail(email,mailRequest);
			return ResponseEntity.ok("Email Sent");			
		}
	}
	
	@PostMapping("/reply")
	public ResponseEntity<String> reply(@RequestHeader("X-User-Email") String email,
									@RequestBody MailRequest mailRequest){
		
		emailService.sendEmail(email, mailRequest);
		return ResponseEntity.ok("Reply Sent");
	}
	
	@GetMapping("/scheduled")
	public ResponseEntity<List<ScheduledEmail>> getAllScheduledEmails(){
		List<ScheduledEmail> scheduledEmails = emailService.findByStatus("SCHEDULED");
		return ResponseEntity.ok(scheduledEmails);
	}
}
