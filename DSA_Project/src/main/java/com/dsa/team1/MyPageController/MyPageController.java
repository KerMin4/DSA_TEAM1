package com.dsa.team1.MyPageController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("mypage")
@RequiredArgsConstructor
public class MyPageController {

   
    @GetMapping("editProfile")
    public String editProfileForm() {
        log.debug("프로필 수정 페이지");
        return "mypage/editProfile"; 
    }
}
