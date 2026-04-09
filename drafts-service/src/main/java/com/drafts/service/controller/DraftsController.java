package com.drafts.service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.drafts.service.entity.Drafts;
import com.drafts.service.service.DraftsService;

@RestController
@RequestMapping("/drafts")
public class DraftsController {
	
	@Autowired
	private DraftsService draftsService;
	
	@PostMapping
	public void saveDraft(@RequestHeader("X-User-Email") String email,
			@RequestBody Drafts draft) {
		draftsService.saveDraft(email,draft);
	}
	
	@GetMapping
	public List<Drafts> getAllDrafts(@RequestHeader("X-User-Email") String email){
		return draftsService.readAll(email);
	}
	
	@DeleteMapping("/delete")
	public void deleteDrafts(@RequestHeader("X-User-Email") String email,@RequestBody List<Long> uids) {
		draftsService.deleteAll(email,uids);
	}

}
