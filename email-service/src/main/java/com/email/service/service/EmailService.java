package com.email.service.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.email.service.client.ConfigClient;
import com.email.service.dto.MailRequest;
import com.email.service.dto.UserImapConfig;
import com.email.service.dto.UserSmtpConfig;
import com.email.service.entity.ScheduledEmail;
import com.email.service.entity.SentEmail;
import com.email.service.repository.ScheduledEmailRepository;
import com.email.service.repository.SentEmailRepository;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	@Autowired
	private ConfigClient configClient;
	
	@Autowired
	private ScheduledEmailRepository scheduledEmailRepository;
	
	@Autowired
	private SentEmailRepository sentEmailRepository;
	
	public void sendEmail(String email, MailRequest mailRequest) {
		// TODO Auto-generated method stub
		UserSmtpConfig smtp = configClient.getSmtpConfig(email);
		UserImapConfig imap = configClient.getImapConfig(email);
		
		//Create MailSender
		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		sender.setHost(smtp.getSmtpHost());
		sender.setPort(smtp.getSmtpPort());
		sender.setUsername(smtp.getUserEmail());
		sender.setPassword(new String(Base64.getDecoder().decode(smtp.getEncryptedPassword()))
		);
		
		Properties props = new Properties();
		props.put("mail.smtp.auth",smtp.isAuthEnabled());
		props.put("mail.smtp.starttls.enable", smtp.isTlsEnabled());
		
		try {
			MimeMessage message = sender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message,true);
			helper.setFrom(smtp.getUserEmail());
			helper.setTo(mailRequest.getTo());
			
			String subject = mailRequest.getSubject();
			if(mailRequest.getInReplyToMessageId() != null && !subject.startsWith("Re:")) {
				subject = "Re: "+subject;
			}
			
			helper.setSubject(subject);
			if(mailRequest.getInReplyToMessageId() != null) {
				message.setHeader("In-Reply-To", mailRequest.getInReplyToMessageId());
				
				if(mailRequest.getReferences() != null) {
					String refs = String.join(" ", mailRequest.getReferences());
					message.setHeader("References", refs);
				} else {
					message.setHeader("References", mailRequest.getInReplyToMessageId());
				}
			}
			helper.setText(mailRequest.getBody(),mailRequest.isHtml());
			sender.send(message);
			String messageId = message.getMessageID();
			saveToSent(message,imap);
			saveToDatabase(message,mailRequest,messageId,email);
		}catch(Exception e) {
			throw new RuntimeException("Email Sending Failed",e);
		}
	}

	private void saveToDatabase(MimeMessage message, MailRequest mailRequest, String messageId, String from) {
		// TODO Auto-generated method stub
		try {
			SentEmail email = new SentEmail();
			email.setFromEmail(from);
			email.setToEmail(mailRequest.getTo());
			email.setSubject(mailRequest.getSubject());
			email.setBody(mailRequest.getBody());
			email.setMessageId(messageId);
			
			if(mailRequest.getReferences() != null) {
				email.setReferences(String.join(" ", mailRequest.getReferences()));
			}else if(mailRequest.getInReplyToMessageId() != null) {
				email.setReferences(mailRequest.getInReplyToMessageId());
			}
			email.setSentAt(LocalDateTime.now());
			sentEmailRepository.save(email);	
		}catch(Exception e) {
			throw new RuntimeException("DB save failed",e);
		}
	}

	private void saveToSent(MimeMessage message, UserImapConfig imap) {
		// TODO Auto-generated method stub
		try {
			Properties props = new Properties();
			props.put("mail.store.protocol", "imap");
			
			Session session = Session.getInstance(props);
			Store store = session.getStore("imap");
			
			store.connect(imap.getImapHost(),imap.getUserEmail(),new String(Base64.getDecoder().decode(imap.getEncryptedPassword()))
					);
			
			Folder sentFolder = store.getFolder("Sent");
			if(!sentFolder.exists()) {
				sentFolder.create(Folder.HOLDS_MESSAGES);
			}
			sentFolder.open(Folder.READ_WRITE);
			sentFolder.appendMessages(new Message[] {message});
			sentFolder.close(false);
			store.close();
			
		}catch(Exception e) {
			throw new RuntimeException("Saving to sent failed",e);
		}
		
	}

	public void scheduleEmail(MailRequest mailRequest, String from) {
		// TODO Auto-generated method stub
		ScheduledEmail email = new ScheduledEmail();
		email.setFromEmail(from);
		email.setToEmail(mailRequest.getTo());
		email.setSubject(mailRequest.getSubject());
		email.setBody(mailRequest.getBody());
		LocalDateTime scheduledAtFromRequest = mailRequest.getScheduledAt();
		ZonedDateTime scheduledAtIST = scheduledAtFromRequest.atZone(ZoneId.of("Asia/Kolkata"));
		email.setScheduledAt(scheduledAtIST.toLocalDateTime());
		email.setStatus("SCHEDULED");
		scheduledEmailRepository.save(email);
	}

	public List<ScheduledEmail> findByStatus(String status) {
		// TODO Auto-generated method stub
		return scheduledEmailRepository.findByStatus("SCHEDULED");
	}

}
