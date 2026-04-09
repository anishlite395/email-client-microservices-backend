package com.drafts.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class DraftsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DraftsServiceApplication.class, args);
	}

}
