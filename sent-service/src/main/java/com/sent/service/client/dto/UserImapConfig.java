package com.sent.service.client.dto;

import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserImapConfig {
	
	
	private Long id;
	
	private String userEmail;
	
	private String imapHost;
	
	private int imapPort;
	
	private boolean useSsl;
	
	private String username;
	
	private String encryptedPassword;
	
	private String folder = "INBOX";
	
	private boolean enabled = true;
	
	private Long lastProcessedUid;
	
	
	private LocalDateTime createdAt = LocalDateTime.now();
	private LocalDateTime updatedAt = LocalDateTime.now();
	

	public void preUpdate() {
		updatedAt = LocalDateTime.now();
	}

}
