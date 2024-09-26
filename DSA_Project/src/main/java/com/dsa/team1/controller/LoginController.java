package com.dsa.team1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("member")
@RequiredArgsConstructor
public class LoginController{
	
	@GetMapping("login")
	public String loginForm() {
		return "member/loginForm";
	}
	
	@GetMapping("join")
	public String joinForm() {
		log.debug("회원가입 진입시도.");
		return "member/joinForm";
		///주석 추가
	}
}
