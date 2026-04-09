package com.config.service.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="user_imap_config")
public class UserImapConfig {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
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
	
	@PreUpdate
	public void preUpdate() {
		updatedAt = LocalDateTime.now();
	}

}
