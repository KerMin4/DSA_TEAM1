package com.dsa.team1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class HomeController {

    @GetMapping({"", "/"})
    public String home() {
        return "Main";
    }
    //안녕하세요? 이 편지는 영국에서 시작되어.............ㅇㅇddd
    // 제발
}
