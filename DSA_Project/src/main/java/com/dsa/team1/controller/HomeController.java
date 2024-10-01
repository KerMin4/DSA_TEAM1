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
    // GIT 행동강령
    // 제 1원칙 : 하루 시작과 함께 sts에서 pull을 한 후, 자신이 작성한 파일을 push 한다.
    // 제 2원칙 : 제 1원칙을 준수한다.
}
