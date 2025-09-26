package com.example.jamroga.ime;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class ImageManipulatorEnterpriseWebApplication {
	
	public static void main(String[] args) {
		log.info("STARTING");
		SpringApplication.run(ImageManipulatorEnterpriseWebApplication.class, args);
		log.info("FINISHED");
	}
}
