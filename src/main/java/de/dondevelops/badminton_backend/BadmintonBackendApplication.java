package de.dondevelops.badminton_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
//OLD PORT 3122
@SpringBootApplication
@EnableScheduling
public class BadmintonBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BadmintonBackendApplication.class, args);
	}

}
