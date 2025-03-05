package com.sanedge.modularexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ModularexampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModularexampleApplication.class, args);
	}

}
