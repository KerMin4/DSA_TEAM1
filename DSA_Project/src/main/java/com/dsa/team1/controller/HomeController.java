package com.dsa.team1.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.dsa.team1.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;

    @GetMapping({"", "/"})
    public String home(Model model) {
        // 현재 인증된 사용자 정보 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userid = auth.getName();  // 인증된 사용자의 아이디 가져오기
        
        // 사용자 정보 조회
        var user = userService.findUserByUserId(userid);

        // 사용자 프로필 이미지가 있으면 모델에 추가
        if (user != null && user.getProfileImage() != null) {
            model.addAttribute("profileImage", user.getProfileImage());
        }

        return "Main";
    }
}




/* public class HomeController {

    @GetMapping({"", "/"})
    public String home() {
        return "Main";
    }
}
*/ 
