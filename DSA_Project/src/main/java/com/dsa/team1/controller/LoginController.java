package com.dsa.team1.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dsa.team1.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("member")
@RequiredArgsConstructor
public class LoginController{
	
	private final UserService us;
	
	@GetMapping("login")
	public String loginForm() {
		return "member/loginForm";
	}
	/*
	@GetMapping("joinForm")
	public String joinForm() {
		log.debug("회원가입 진입시도.");
		return "member/joinForm";
	}
	
	@PostMapping("join")
	public String join(@RequestParam("userid") String userid,
			@RequestParam("password") String password,
			@RequestParam("phone") String phone,
			@RequestParam("email") String email,
			@RequestParam("location") String location,
			@RequestParam("interests") List<String> interests
			) {
		log.debug("여기까진 왔냐?");
		
		us.join(userid, password, phone, email, location, interests);
		
		return "redirect:/";
	}
	*/
	
	@GetMapping("joinForm1")
	public String joinFoorm() {
		return "member/joinForm1";
	}
	
	@PostMapping("/join1")
	public String join(
			@RequestParam("interests") List<String> interests,
			@RequestParam("location")String location,
			@RequestParam("userid") String userid,
			@RequestParam("password")String password,
			@RequestParam("email")String email,
			@RequestParam("phone")String phone,
			@RequestParam("name")String name,
			@RequestParam("username")String username
			) {
		log.debug("흥미: {}", interests);
		log.debug(location);
		us.join(userid, password, phone, email, location, name, username);
		return "redirect:/";
	}
	
}
