package com.dsa.team1.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("socialgroup")
public class SocialGroupController {
	
	@Value("${socialgroup.pageSize}")
	int pageSize;  // 페이지당 글 수를 application.properties에서 읽어와 주입

	@Value("${socialgroup.linkSize}")
	int linkSize;  // 페이지 번호 링크의 개수를 application.properties에서 읽어와 주입

	@Value("${socialgroup.uploadPath}")
	String uploadPath;  // 첨부파일 저장 경로를 application.properties에서 읽어와 주입

	@GetMapping("create")
	public String create() {
		return "socialgroup/createForm";
	}

	@GetMapping("socialing")
	public String socialing() {
		return "socialgroup/socialing";
	}
	
	@GetMapping("board")
	public String board() {
		return "socialgroup/board";
	}

}
