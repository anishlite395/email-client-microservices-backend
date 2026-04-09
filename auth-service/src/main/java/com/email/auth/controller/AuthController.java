package com.email.auth.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.email.auth.entity.AuthRequest;
import com.email.auth.entity.UserInfo;
import com.email.auth.service.JwtService;
import com.email.auth.service.UserInfoService;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	public static final Logger log = LoggerFactory.getLogger(AuthController.class);
	
	@Autowired
	private UserInfoService userInfoService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtService jwtService;
	
	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody UserInfo userInfo){
		log.info("Recieved Registration Request: {}", userInfo);
		return ResponseEntity.ok(userInfoService.addNewUser(userInfo));
	}

	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest authRequest){
		
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), 
				authRequest.getPassword()));
		
		if(authentication.isAuthenticated()) {
			String token = jwtService.generateToken(authRequest.getEmail());
			return ResponseEntity.ok(Map.of("token",token));
		}
		
		throw new UsernameNotFoundException("Invalid Credentials");
	}
	
}
