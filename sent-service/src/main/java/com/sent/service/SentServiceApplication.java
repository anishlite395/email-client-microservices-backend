package com.sent.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SentServiceApplication.class, args);
	}

}
