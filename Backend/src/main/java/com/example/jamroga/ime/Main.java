package com.example.jamroga.ime;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class Main {
    
    public static void main(String[] args) {
        log.error("You shouldn't be here!");
        SpringApplication.run(Main.class, args);
    }
}
