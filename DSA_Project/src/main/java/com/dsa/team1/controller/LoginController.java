package com.dsa.team1.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.dsa.team1.entity.enums.Interest;
import com.dsa.team1.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("member")
@RequiredArgsConstructor
public class LoginController{
	
	private final UserService us;
	@Value("${kakao.javascript.key}")
	private String kakaoAppkey;
	
	@GetMapping("loginForm")
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
	
	// when we want to use kakao map api in html view,use apikey like this.
	@GetMapping("joinForm1")
	public String joinFoorm(Model model) {
		model.addAttribute("kakaoAppkey", kakaoAppkey);
		return "member/joinForm1";
	}
	
	@PostMapping("/join1")
	public String join(
	        @RequestParam("interests") List<String> interests,
	        @RequestParam("location") String location,
	        @RequestParam("userid") String userid,
	        @RequestParam("password") String password,
	        @RequestParam("email") String email,
	        @RequestParam("phone") String phone,
	        @RequestParam("name") String name,
	        @RequestParam("username") String username,
	        @RequestParam("gender")Integer gender,
	        @RequestParam("year")String year,
	        @RequestParam("month")String month,
	        @RequestParam("day")String day,
	        @RequestParam("profileImage") MultipartFile profileImage// 프로필 사진 추가함 -나연-
	) {
	    log.debug("흥미: {}", interests);
	    log.debug(location);
	    log.debug("{} {} {}", year, month, day);
	    int birth =  10000*Integer.parseInt(year) + 100 * Integer.parseInt(month) + Integer.parseInt(day);
	    try {
			us.join(userid, password, phone, email, location, name, username, gender, profileImage, interests, birth);
		} catch (IOException e) {
	
			e.printStackTrace();
		}

	    return "redirect:/";
	}
	
	@ResponseBody
	@PostMapping("/idCheck")
	public boolean idCheck(@RequestParam("userid")String id) {
		boolean result = us.idCheck(id);
		return result;
	}
	
	@GetMapping("/mapTest")
	public String mapTest(Model model) {
		model.addAttribute("kakaoAppkey", kakaoAppkey);
		return "member/mapTest";
	}
	

}
