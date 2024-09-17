package com.dsa.team1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

//@SpringBootApplication 원래 코드
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)  
public class DsaProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(DsaProjectApplication.class, args);
	}

}



/*
 *  @SpringBootApplication(exclude = SecurityAutoConfiguration.class)
 *  위에 코드 Spring Security 시큐리티 기본 보안 인증 페이지 안 뜨게 하려고 잠깐 해둠
 */