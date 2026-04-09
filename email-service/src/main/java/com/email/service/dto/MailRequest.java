package com.email.service.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailRequest {
	
	private String to;
	private String subject;
	private String body;
	private boolean Html;
	private LocalDateTime scheduledAt;
	
	//For Reply
	private String inReplyToMessageId;
	private String[] references;

}
