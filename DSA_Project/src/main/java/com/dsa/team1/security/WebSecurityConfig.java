package com.dsa.team1.security;

import org.springframework.beans.factory.annotation.Autowired;
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

	private OAuth2UserService oAuth2User;
	
	public WebSecurityConfig(OAuth2UserService oAuth2User) {
		this.oAuth2User = oAuth2User;
	}
	// 로그인 없이 접근 가능한 경로. 필요한거 더 추가해서 사용하세요
	private static final String[] PUBLIC_URLS = {
			"/",
			"/images/**",
			"/css/**",
			"/js/**",
			"/member/joinForm1",
			"/member/login",
			"/kkirikkiri/member/join",
			"/socialgroup/**",		// 임시추가
			"/dashboard/**",       // 나중에 까먹지말고 빼야댐 (나연)
			"/member/join1",
			"/member/join",
			"/kkirikkiri/member/idCheck",
			"/member/idCheck",
			"/member/mapTest"
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
					.failureUrl("/member/loginForm?error=true")
					.permitAll()
					)
			.logout(logout -> logout
					.logoutUrl("/member/logout")
					.logoutSuccessUrl("/"))
			.oauth2Login(oauth2 -> oauth2
					.loginPage("/member/loginForm")
					.defaultSuccessUrl("/")
					.failureUrl("/loginForm?error")
					.userInfoEndpoint(userInfo -> userInfo
							.userService(oAuth2User)
					)
			);
		
		http
			.cors(AbstractHttpConfigurer::disable)
			.csrf(AbstractHttpConfigurer::disable);
		
		
		return http.build();
	}
	@Bean
	public BCryptPasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}