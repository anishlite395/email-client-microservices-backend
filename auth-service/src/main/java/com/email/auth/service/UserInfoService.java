package com.email.auth.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.email.auth.entity.UserInfo;
import com.email.auth.util.CryptoUtil;
import com.email.service.dto.UserRegisteredEvent;
import com.email.auth.repository.UserRepository;


@Service
public class UserInfoService implements UserDetailsService {
	
	private static final Logger log = LoggerFactory.getLogger(UserInfoService.class);
	
	private UserRepository userRepository;
	
	private PasswordEncoder passwordEncoder;
	
	private CryptoUtil cryptoUtil;
	
	@Autowired
	private KafkaTemplate<String,UserRegisteredEvent> kafkaTemplate;
	
	
	
	@Autowired
	public UserInfoService(UserRepository userRepository, PasswordEncoder passwordEncoder, CryptoUtil cryptoUtil) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.cryptoUtil = cryptoUtil;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		Optional<UserInfo> userInfo = userRepository.findByEmail(username);
		if(userInfo.isEmpty()) {
			throw new UsernameNotFoundException("Cannot find user: "+username);
		}
		UserInfo user = userInfo.get();
		return new UserInfoDetails(user);
	}
	
	public String addNewUser(UserInfo userInfo) {
		userInfo.setUsername(userInfo.getUsername());
		String rawPassword = userInfo.getPassword();
		userInfo.setPassword(passwordEncoder.encode(rawPassword));
		userInfo.setRoles("ROLE_USER");
		userRepository.save(userInfo);
		UserRegisteredEvent event = new UserRegisteredEvent(userInfo.getEmail(),rawPassword);
		kafkaTemplate.send("user-registered-topic", event);
		log.info("Sent user to Kafka topic:"+event);
		return "User Added Successfully";
	}

}
