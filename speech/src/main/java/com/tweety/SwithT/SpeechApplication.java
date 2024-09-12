package com.tweety.SwithT;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class SpeechApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpeechApplication.class, args);
	}

}
