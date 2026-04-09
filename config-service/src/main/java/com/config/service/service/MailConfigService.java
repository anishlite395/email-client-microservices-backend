package com.config.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.config.service.entity.UserImapConfig;
import com.config.service.entity.UserSmtpConfig;
import com.config.service.repository.UserImapConfigRepository;
import com.config.service.repository.UserSmtpConfigRepository;
import com.config.service.util.CryptoUtil;

@Service
public class MailConfigService {
	
	@Autowired
	private UserSmtpConfigRepository userSmtpConfigRepository;
	
	@Autowired
	private UserImapConfigRepository userImapConfigRepository;
	
	@Autowired
	private CryptoUtil cryptoUtil;

	public void saveSmtpConfig(String email, UserSmtpConfig config) {
		// TODO Auto-generated method stub
		config.setUserEmail(email);
		userSmtpConfigRepository.save(config);
	}

	public UserSmtpConfig getSmtpConfig(String email) {
		// TODO Auto-generated method stub
		return userSmtpConfigRepository.findByUserEmail(email).orElseThrow(()-> 
		                       new RuntimeException("SMTP Config not found"));
	}

	public UserImapConfig getImapConfig(String email) {
		return userImapConfigRepository.findByUserEmail(email).orElseThrow(
				()-> new RuntimeException("IMAP Config not found"));
	}
	
	public void saveImapConfig(String email,UserImapConfig config) {
		config.setUserEmail(email);
		userImapConfigRepository.save(config);
	}
}
