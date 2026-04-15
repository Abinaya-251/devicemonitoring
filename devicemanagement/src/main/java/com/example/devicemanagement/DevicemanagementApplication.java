package com.example.devicemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DevicemanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevicemanagementApplication.class, args);
	}

}
