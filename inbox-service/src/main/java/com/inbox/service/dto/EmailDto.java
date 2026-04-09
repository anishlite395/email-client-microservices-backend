package com.inbox.service.dto;

import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDto {

	private Long uid;
	private String from;
	private String to;
	private String subject;
	
	private String sentDate;
	
	private boolean read;
	private String body;
	
	
	
}
