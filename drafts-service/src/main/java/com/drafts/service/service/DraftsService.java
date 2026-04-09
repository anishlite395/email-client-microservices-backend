package com.drafts.service.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Properties;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;

import com.drafts.service.ConfigClient;
import com.drafts.service.entity.Drafts;
import com.drafts.service.entity.UserImapConfig;

import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.NoSuchProviderException;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.UIDFolder;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class DraftsService {
	
	@Autowired
	private ConfigClient configClient;
	
	public List<Drafts> readAll(String email) {
		// TODO Auto-generated method stub
		UserImapConfig config = configClient.getImapConfig(email);
		List<Drafts> drafts = new ArrayList<>();
		
		try {
			Store store = connect(config);
			Folder folder = getDraftFolder(store);
			
			folder.open(Folder.READ_ONLY);
			UIDFolder uidFolder = (UIDFolder) folder;
			
			Message[] messages = folder.getMessages();
			
			for(Message message: messages) {
				long uid = uidFolder.getUID(message);
				
				try {
					Drafts draft = new Drafts();
					draft.setUid(uid);
					draft.setFrom(getHeaderSafe(message, "From"));
                    draft.setTo(getHeaderSafe(message, "To"));
                    draft.setSubject(getHeaderSafe(message,"Subject"));
                    draft.setBody(extractText(message));
                    draft.setRead(message.isSet(Flags.Flag.SEEN));

                    drafts.add(draft);
				}catch (Exception ex) {
                    System.err.println("Skipping draft UID " + uid + ": " + ex);
                }
			}
			folder.close(false);
			store.close();
			
		}catch(Exception e) {
			throw new RuntimeException("Read Drafts failed",e);
		}
		
		return drafts;
	}
	
	public void saveDraft(String email, Drafts drafts) {
		// TODO Auto-generated method stub
		UserImapConfig config = configClient.getImapConfig(email);
		
		try {
			Session session = buildSession(config);
			Store store = session.getStore("imap");
			
			String password = decode(config);
			store.connect(config.getImapHost(), config.getUserEmail(), password);
			
			Folder folder = getDraftFolder(store);
			folder.open(Folder.HOLDS_MESSAGES);
			
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(config.getUserEmail()));
			message.setRecipients(Message.RecipientType.TO, drafts.getTo());
			message.setContent(drafts.getBody(),"text/plain");
			message.setSubject(drafts.getSubject());
			message.setSentDate(new Date());
			message.setFlag(Flags.Flag.DRAFT, true);
			
			folder.appendMessages(new Message[] {message});

            folder.close(false);
            store.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void deleteAll(String email,List<Long> uids) {
		// TODO Auto-generated method stub
		UserImapConfig config = configClient.getImapConfig(email);
		List<Drafts> drafts = new ArrayList<>();
		
		try {
			Store store = connect(config);
			Folder folder = getDraftFolder(store);
			
			folder.open(Folder.READ_WRITE);
			UIDFolder uidFolder = (UIDFolder) folder;
			
			Message[] messages = folder.getMessages();
			
			for(Long uid:uids) {
				Message message = uidFolder.getMessageByUID(uid);
				if(message != null) {
					message.setFlag(Flags.Flag.DELETED, true);
				}
			}
			folder.close(true);
			store.close();
			
		}catch(Exception e) {
			throw new RuntimeException("Delete Drafts failed",e);
		}
	}
		
	
	
	
	

	//===================== HELPER METHODS =========================
	
	private Store connect(UserImapConfig config) throws Exception {
		Session session = buildSession(config);
		Store store = session.getStore("imap");
		store.connect(config.getImapHost(), config.getUserEmail(), decode(config));
		return store;
	}

	private String decode(UserImapConfig config) {
		// TODO Auto-generated method stub
		return new String(Base64.getDecoder().decode(config.getEncryptedPassword()));
	}
	
	private Folder getDraftFolder(Store store) throws MessagingException {
		Folder folder = store.getFolder("Drafts");
		
		if(!folder.exists()) {
			folder.create(Folder.HOLDS_MESSAGES);
		}
		return folder;
	}

	private Session buildSession(UserImapConfig config) {
		// TODO Auto-generated method stub
		Properties props = new Properties();
		props.put("mail.store.protocol", "imap");
		props.put("mail.imap.host", config.getImapHost());
		props.put("mail.imap.port", config.getImapPort());
		
		if(config.isUseSsl()) {
			props.put("mail.imap.ssl.enable", "true");
			props.put("mail.imap.ssl.trust", "*");
		}
		return Session.getInstance(props);
	}
	
	private String extractText(Part part) throws IOException, MessagingException {
		if(part.isMimeType("text/plain") || part.isMimeType("text/html")) {
			return part.getContent().toString();
		}
		
		if(part.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) part.getContent();
			
			for(int i=0;i<multipart.getCount();i++) {
				String text = extractText(multipart.getBodyPart(i));
				if(text != null) return text;
			}
		}
		return null;
	}
	
	private String getHeaderSafe(Message message, String name) {
		try {
			String[] headers = message.getHeader(name);
			return (headers != null && headers.length > 0) ? headers[0] : "";
		} catch(Exception e) {
			return "";
		}
	}



}
