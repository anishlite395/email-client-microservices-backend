package com.sent.service.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.sent.service.client.dto.EmailDto;
import com.sent.service.service.SentService;

@RestController
@RequestMapping("/sent")
public class SentController {

	@Autowired
	private SentService service;
	
	@GetMapping
	public List<EmailDto> getSent(@RequestHeader("X-User-Email") String email){
		return service.readSent(email);
	}
	
	@DeleteMapping("/delete")
	public ResponseEntity<Void> delete(@RequestHeader("X-User-Email") String email,@RequestBody List<Long> uids){
		service.deleteSent(email,uids);
		return ResponseEntity.ok().build();
	}
	
	@PutMapping("/read/{uid}")
	public ResponseEntity<Void> markAsRead(@RequestHeader("X-User-Email") String email,
											@PathVariable Long uid){
		service.markAsRead(email,uid,true);
		return ResponseEntity.ok().build();
	}
}
