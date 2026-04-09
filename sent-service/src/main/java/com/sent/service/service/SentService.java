package com.sent.service.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sent.service.client.ConfigClient;
import com.sent.service.client.dto.EmailDto;
import com.sent.service.client.dto.UserImapConfig;

import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.UIDFolder;

@Service
public class SentService {
	
	@Autowired
	private ConfigClient configClient;

	public List<EmailDto> readSent(String email) {
		// TODO Auto-generated method stub
		
		UserImapConfig config = configClient.getImap(email);
		List<EmailDto> emails = new ArrayList<>();
		
		try {
			Properties props = new Properties();
			props.put("mail.store.protocol", "imap");
			props.put("mail.imap.host", config.getImapHost());
			props.put("mail.imap.port", config.getImapPort());
			
			if(config.isUseSsl()) {
				props.put("mail.imap.ssl.enable", "true");
			}
			
			Session session = Session.getInstance(props);
			Store store = session.getStore("imap");
			
			String password = new String(Base64.getDecoder().decode(config.getEncryptedPassword()));
			
			store.connect(config.getImapHost(), config.getUserEmail(), password);
			
			Folder folder = store.getFolder("Sent");
			
			if(!folder.exists()) {
				folder = store.getFolder("Sent Items");
			}
			folder.open(Folder.READ_ONLY);
			
			UIDFolder uidFolder = (UIDFolder)folder;
			Message[] messages = folder.getMessages();
			
			for(Message message:messages) {
				long uid = uidFolder.getUID(message);
				
				EmailDto dto = new EmailDto();
				dto.setUid(uid);
				dto.setFrom(getHeaderSafe(message,"From"));
				dto.setTo(getHeaderSafe(message,"To"));
				dto.setSubject(getHeaderSafe(message,"Subject"));
				dto.setSentDate(getHeaderSafe(message,"Date"));
				dto.setBody(extractText(message));
				dto.setSeen(message.isSet(Flags.Flag.SEEN));
				emails.add(dto);
			}
			folder.close(false);
			store.close();
		}catch(Exception e) {
			throw new RuntimeException("Sent Read Failed",e);
		}
		
		return emails;
	}
	
	private String getHeaderSafe(Message message,String name) {
		try {
			String[] headers = message.getHeader(name);
			return (headers != null && headers.length > 0) ? headers[0]: "";
		}catch(Exception e) {
			return "";			
		}
	}

	private String extractText(Part part) throws MessagingException,
    IOException {
		// TODO Auto-generated method stub

		if(part.isMimeType("text/plain")) {
			return part.getContent().toString();
		}

		if(part.isMimeType("text/html")) {
			return part.getContent().toString();
		}

		if(part.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) part.getContent();
	
			for(int i=0;i < multipart.getCount();i++) {
				String text = extractText(multipart.getBodyPart(i));
				if(text != null) return text;
			}
		}

		return null;
	}
	
	public void markAsRead(String email, Long uid, boolean read) {
		// TODO Auto-generated method stub
		UserImapConfig config = configClient.getImap(email);
		try {
			Session session = Session.getInstance(new Properties());
			Store store = session.getStore("imap");
			
			String password = new String(Base64.getDecoder().decode(config.getEncryptedPassword()));
			store.connect(config.getImapHost(),config.getUserEmail(),password);
			
			Folder folder = store.getFolder("Sent");
			if(!folder.exists()) {
				folder = store.getFolder("Sent Items");
			}
			folder.open(Folder.READ_WRITE);
			
			UIDFolder uidFolder = (UIDFolder) folder;
			Message message = uidFolder.getMessageByUID(uid);
			message.setFlag(Flags.Flag.SEEN, read);
			
			folder.close(true);
			store.close();
		}catch(Exception e) {
			throw new RuntimeException("Update read failed",e);
		}
	}

	public void deleteSent(String email, List<Long> uids) {
		// TODO Auto-generated method stub
		UserImapConfig config = configClient.getImap(email);
		
		try {
			Properties props = new Properties();
			props.put("mail.store.protocol", "imap");
			props.put("mail.imap.host", config.getImapHost());
			props.put("mail.imap.port", config.getImapPort());
			
			if(config.isUseSsl()) {
				props.put("mail.imap.ssl.enable", "true");		
			}
			
			Session session = Session.getInstance(props);
			Store store = session.getStore("imap");
			
			String password = new String(Base64.getDecoder().decode(config.getEncryptedPassword()));
			
			store.connect(config.getImapHost(),config.getUserEmail(),password);
			
			Folder folder = store.getFolder("Sent");
			folder.open(Folder.READ_WRITE);
			UIDFolder uidFolder = (UIDFolder)folder;
			for(Long uid:uids) {
				Message msg = uidFolder.getMessageByUID(uid);
				if(msg != null) {
					msg.setFlag(Flags.Flag.DELETED, true);
				}
			}
			folder.expunge();
			folder.close(true);
			store.close();
	}catch(Exception e) {
			throw new RuntimeException("Delete Failed",e);
	}

	}
}
