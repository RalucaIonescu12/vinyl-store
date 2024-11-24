package com.example.vinylstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class VinylstoreApplication {

	public static void main(String[] args) {
//		SpringApplication.run(VinylstoreApplication.class, args);
		SpringApplication app = new SpringApplication(VinylstoreApplication.class);
		app.setDefaultProperties(Collections.singletonMap("server.port", "9093"));
		app.run(args);
	}

}
