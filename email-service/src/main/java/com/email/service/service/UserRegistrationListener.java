package com.email.service.service;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.email.service.client.ConfigClient;
import com.email.service.dto.UserImapConfig;
import com.email.service.dto.UserRegisteredEvent;
import com.email.service.dto.UserSmtpConfig;


@Component
public class UserRegistrationListener {
	
	private static final Logger log = LoggerFactory.getLogger(UserRegistrationListener.class);

	@Autowired
	private HmailService hmailService;
	
	@Autowired
	private ConfigClient configClient;
	
	@KafkaListener(topics = "user-registered-topic", groupId = "email-group")
	public void handleUserRegistration(UserRegisteredEvent event) {
		log.info("Recieved the user from Kafka: "+event);
		hmailService.createMailBox(event.getEmail(), event.getPassword());
		
		//Storing UserSmtpConfig details
		UserImapConfig imapConfig = new UserImapConfig();
		imapConfig.setUserEmail(event.getEmail());
		imapConfig.setImapHost("localhost");
		imapConfig.setImapPort(143);
		String[] parts = event.getEmail().split("@");
		imapConfig.setUseSsl(false);
		imapConfig.setUsername(parts[0]);
		imapConfig.setEncryptedPassword(Base64.getEncoder().encodeToString(event.getPassword().getBytes()));
		configClient.saveImapConfig(event.getEmail(),imapConfig);
		log.info("Saved Imap Config Details: "+imapConfig);
		UserSmtpConfig smtpConfig = new UserSmtpConfig();
		smtpConfig.setUserEmail(event.getEmail());
		smtpConfig.setSmtpHost("localhost");
		smtpConfig.setSmtpPort(25);
		String[] smtp_parts = event.getEmail().split("@");
		smtpConfig.setAuthEnabled(true);
		smtpConfig.setSmtpUsername(smtp_parts[0]);
		smtpConfig.setEncryptedPassword(Base64.getEncoder().encodeToString(event.getPassword().getBytes()));
		configClient.saveSmtpConfig(event.getEmail(), smtpConfig);
		log.info("Saved Smtp Config Details: "+smtpConfig);
	}
	
}
