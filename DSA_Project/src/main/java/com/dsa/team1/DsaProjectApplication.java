package com.dsa.team1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DsaProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(DsaProjectApplication.class, args);
	}

}
