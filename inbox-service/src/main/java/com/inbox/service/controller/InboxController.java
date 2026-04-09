package com.inbox.service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inbox.service.dto.EmailDto;
import com.inbox.service.service.InboxService;

@RestController
@RequestMapping("/inbox")
public class InboxController {
	
	@Autowired
	private InboxService inboxService;
	
	@GetMapping
	public List<EmailDto> getInbox(@RequestHeader("X-User-Email") String email){
		return inboxService.readInbox(email);
	}
	
	@PutMapping("/{uid}/read")
	public ResponseEntity<Void> markRead(@RequestHeader("X-User-Email") String email,
			                         @PathVariable Long uid,@RequestParam boolean read){
		
		inboxService.markAsRead(email,uid,read);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/{uid}")
	public ResponseEntity<EmailDto> getSingleEmail(@RequestHeader("X-User-Email") String email,@PathVariable Long uid){
		EmailDto dto = inboxService.readSingleEmailByEmail(uid,email);
		return ResponseEntity.ok(dto);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<Void> delete(@RequestHeader("X-User-Email") String email,
			         @RequestBody List<Long> uids){
		
		inboxService.deleteEmails(email,uids);
		return ResponseEntity.ok().build();
	}
}
