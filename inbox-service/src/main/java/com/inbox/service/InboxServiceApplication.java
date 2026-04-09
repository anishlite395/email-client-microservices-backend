package com.inbox.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class InboxServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InboxServiceApplication.class, args);
	}

}
