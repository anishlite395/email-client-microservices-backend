package com.email.service.client;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.email.service.dto.MailRequest;
import com.email.service.entity.ScheduledEmail;
import com.email.service.repository.ScheduledEmailRepository;
import com.email.service.service.EmailService;

@Component
public class EmailScheduler {

	@Autowired
	private ScheduledEmailRepository repo;
	
	@Autowired
	private EmailService emailService;
	
	@Scheduled(cron = "0 * * * * *",zone="Asia/Kolkata")
	public void sendScheduledEmails() {
		
		LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata")).withSecond(0).withNano(0);
		
		List<ScheduledEmail> emails = repo.findByStatusAndScheduledAtLessThanEqual("SCHEDULED",now);
		
		for(ScheduledEmail email:emails) {
			emailService.sendEmail(email.getFromEmail(), convertToMailRequest(email));
			email.setStatus("SENT");
			repo.save(email);
		}
	}

	private MailRequest convertToMailRequest(ScheduledEmail email) {
		// TODO Auto-generated method stub
		MailRequest req = new MailRequest();
		req.setTo(email.getToEmail());
		req.setSubject(email.getSubject());
		req.setBody(email.getBody());
		return req;
	}
}
