package com.example.jamroga.ime;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;

@SpringBootApplication
@Slf4j
public class ImageManipulatorEnterpriseApplication implements CommandLineRunner {
	@Autowired
	private CommandLineFrontend commandLineFrontend;
	
	public static void main(String[] args) {
		log.info("STARTING");
		SpringApplication.run(ImageManipulatorEnterpriseApplication.class, args);
		log.info("FINISHED");
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("EXECUTING : command line runner");
		new CommandLine(commandLineFrontend).execute(args);
	}
}
