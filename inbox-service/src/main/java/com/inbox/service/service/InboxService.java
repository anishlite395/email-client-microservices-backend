package com.inbox.service.service;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inbox.service.client.ConfigClient;
import com.inbox.service.dto.EmailDto;
import com.inbox.service.dto.UserImapConfig;

import jakarta.mail.Address;
import jakarta.mail.FetchProfile;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.UIDFolder;
import jakarta.mail.internet.InternetAddress;

@Service
public class InboxService {
	
	@Autowired
	private ConfigClient configClient;

	public List<EmailDto> readInbox(String email) {
		// TODO Auto-generated method stub
		
		UserImapConfig config = configClient.getImap(email);
		
		List<EmailDto> emails = new ArrayList<>();
		
		try {
			Properties props = new Properties();
			props.put("mail.store.protocol", "imap");
			props.put("mail.imap.host", config.getImapHost());
			props.put("mail.imap.port", config.getImapPort());
			props.put("mail.imap.partialfetch", "false");
			
			if(config.isUseSsl()) {
				props.put("mail.imap.ssl.enable", "true");
			}
			
			Session session = Session.getInstance(props);
			Store store = session.getStore("imap");
			
			String password = new String(Base64.getDecoder().decode(config.getEncryptedPassword()));
			
			store.connect(config.getImapHost(), config.getUserEmail(), password);
			
			Folder folder = store.getFolder("INBOX");
			folder.open(Folder.READ_WRITE);
			
			UIDFolder uidFolder = (UIDFolder) folder;
			Message[] messages = folder.getMessages();
			
			
			for(Message message:messages) {
				long uid = uidFolder.getUID(message);
				try {
					
					boolean isRead = message.isSet(Flags.Flag.SEEN);
					
					EmailDto dto = new EmailDto();
					dto.setUid(uid);
					dto.setFrom(safeGetFrom(message,"From"));
					dto.setSubject(getHeaderSafe(message,"Subject"));
					dto.setRead(isRead);
					dto.setTo(safeGetFrom(message, "To"));
					dto.setBody(extractText(message));
					dto.setSentDate(getHeaderSafe(message,"Date"));;
					emails.add(dto);
				}catch (Exception ex) {
					System.err.println("Skipping corrupted message UID " + uid + ": " + ex);
			    }
			}
			folder.close(false);
			store.close();
		}catch(Exception e) {
			throw new RuntimeException("IMAP Read failed",e);
		}
		
		return emails;
	}
	
	

	private String safeGetFrom(Message message,String newHeader) {
		// TODO Auto-generated method stub
		String header = getHeaderSafe(message, newHeader);
		
		if(header == null || header.isEmpty()) return "";
		
		try {
			InternetAddress[] addresses = InternetAddress.parse(header);
			if(addresses.length > 0) {
				return addresses[0].getAddress();
			}
		}catch(Exception e) {
			System.err.println("Failed to Parse From Header: "+header);
		}
		return null;
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
	
	private String getHeaderSafe(Message message,String name) {
		try {
			String[] headers = message.getHeader(name);
			return (headers != null && headers.length > 0) ? headers[0]: "";
		}catch(Exception e) {
			return "";			
		}
	}

	public void markAsRead(String email, Long uid, boolean read) {
		// TODO Auto-generated method stub
		UserImapConfig config = configClient.getImap(email);
		try {
			Session session = Session.getInstance(new Properties());
			Store store = session.getStore("imap");
			
			String password = new String(Base64.getDecoder().decode(config.getEncryptedPassword()));
			store.connect(config.getImapHost(),config.getUserEmail(),password);
			
			Folder folder = store.getFolder("INBOX");
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
	
	public EmailDto readSingleEmailByEmail(Long uid, String email) {

	    UserImapConfig config = configClient.getImap(email);

	    Store store = null;
	    Folder folder = null;

	    try {
	        Properties props = new Properties();
	        props.put("mail.store.protocol", "imap");
	        props.put("mail.imap.host", config.getImapHost());
	        props.put("mail.imap.port", config.getImapPort());

	        props.put("mail.imap.connectiontimeout", "10000");
	        props.put("mail.imap.timeout", "10000");

	        if (config.isUseSsl()) {
	            props.put("mail.imap.ssl.enable", "true");
	            props.put("mail.imap.ssl.trust", "*");
	        }

	        Session session = Session.getInstance(props);
	        store = session.getStore("imap");

	        String password = new String(
	                Base64.getDecoder().decode(config.getEncryptedPassword())
	        );

	        store.connect(config.getImapHost(), config.getUserEmail(), password);

	        folder = store.getFolder("INBOX");
	        folder.open(Folder.READ_ONLY);

	        UIDFolder uidFolder = (UIDFolder) folder;
	        Message message = uidFolder.getMessageByUID(uid);

	        if (message == null) {
	            throw new RuntimeException("Email not found");
	        }

	        // 🔥 SAFE extraction (hMailServer compatible)
	        String from = getHeaderSafe(message, "From");
	        String to = getHeaderSafe(message,"To");
	        String subject = getHeaderSafe(message, "Subject");
	        String dateHeader = getHeaderSafe(message, "Date");

	        Date sentDate = parseEmailDate(dateHeader);

	        String body;
	        try {
	            body = extractText(message);
	        } catch (Exception e) {
	            body = "(Failed to load body)";
	        }

	        boolean isRead = false;
	        try {
	            isRead = message.isSet(Flags.Flag.SEEN);
	        } catch (Exception ignored) {}

	       return new EmailDto(uid, from, to, subject, dateHeader, isRead, body);

	    } catch (Exception e) {
	        throw new RuntimeException("IMAP read failed", e);
	    } finally {
	        try {
	            if (folder != null && folder.isOpen()) folder.close(false);
	            if (store != null && store.isConnected()) store.close();
	        } catch (Exception ignored) {}
	    }
	}

	private Date parseEmailDate(String dateStr) {
	    try {
	        ZonedDateTime zdt = ZonedDateTime.parse(
	                dateStr,
	                DateTimeFormatter.RFC_1123_DATE_TIME
	        );
	        return Date.from(zdt.toInstant());
	    } catch (Exception e) {
	        return null;
	    }
	}
	
	public void deleteEmails(String email,List<Long> uids) {
		// TODO Auto-generated method stub
		UserImapConfig config = configClient.getImap(email);
		
		try {
			Session session = Session.getInstance(new Properties());
			Store store = session.getStore("imap");
			
			String password = new String(Base64.getDecoder().decode(config.getEncryptedPassword()));
			store.connect(config.getImapHost(), config.getUserEmail(), password);
			
			Folder folder = store.getFolder("INBOX");
			folder.open(Folder.READ_WRITE);
			
			UIDFolder uidFolder = (UIDFolder) folder;
			
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
