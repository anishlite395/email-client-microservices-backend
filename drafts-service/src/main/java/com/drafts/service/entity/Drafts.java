package com.drafts.service.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Drafts {
	
	private Long uid;
	private String from;
	private String to;
	private String subject;
	private Date sentDate;
	private boolean read;
	private String body;
	
	

}
