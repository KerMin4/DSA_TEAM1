package com.dsa.team1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import io.github.cdimascio.dotenv.Dotenv;

@EnableJpaAuditing
@SpringBootApplication
public class DsaProjectApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();
		
		System.setProperty("DB_userId", dotenv.get("db_id"));
		System.setProperty("DB_password", dotenv.get("db_pw"));
		System.setProperty("kakao_client_id", dotenv.get("kakao_client_id"));
		System.setProperty("kakao_api_key", dotenv.get("kakao_client_secret"));
		System.setProperty("kakao_javascript_key", dotenv.get("kakao_javascript_key"));
		SpringApplication.run(DsaProjectApplication.class, args);
	}

}
