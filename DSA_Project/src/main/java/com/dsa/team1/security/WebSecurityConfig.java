package com.dsa.team1.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	// 로그인 없이 접근 가능한 경로. 필요한거 더 추가해서 사용하세요
	private static final String[] PUBLIC_URLS = {
			"/",
			"/images/**",
			"/css/**",
			"/js/**",
			"/member/join",
			"/member/login"
	};
	
	@Bean
	protected SecurityFilterChain config(HttpSecurity http) throws Exception{
		http
			.authorizeHttpRequests(author -> author
					.requestMatchers(PUBLIC_URLS).permitAll()
					.anyRequest().authenticated()
					)
			.httpBasic(Customizer.withDefaults())
			.formLogin(formLogin -> formLogin
					.loginPage("/member/loginForm")
					.usernameParameter("id")
					.passwordParameter("password")
					.loginProcessingUrl("/member/login")
					.defaultSuccessUrl("/", true)
					.permitAll()
					)
			.logout(logout -> logout
					.logoutUrl("/member/logout")
					.logoutSuccessUrl("/"));
		
		http
			.cors(AbstractHttpConfigurer::disable)
			.csrf(AbstractHttpConfigurer::disable);
			
		return http.build();
	}
	// 1
	@Bean
	public BCryptPasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
