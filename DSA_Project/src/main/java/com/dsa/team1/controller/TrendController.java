package com.dsa.team1.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dsa.team1.dto.PlaceDTO;
import com.dsa.team1.dto.SocialGroupDTO;
import com.dsa.team1.dto.UserDTO;
import com.dsa.team1.security.AuthenticatedUser;
import com.dsa.team1.service.TrendService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("trend")
public class TrendController {

	private final TrendService trendService;
	
	@GetMapping("/trend")
    public String getTrends(
    		@AuthenticationPrincipal AuthenticatedUser user,
    		Model model) {
		
        // 유저 정보 가져오기
//        UserDTO userDTO = trendService.getUserInfo(user.getId());
		
		log.info("[TrendController-getTreds] user: {}", user);

        // 그룹 및 플레이스 목록 가져오기
		List<SocialGroupDTO> groupL = trendService.getGroupsByUserInterests(user.getId());
//        List<SocialGroupDTO> groupL = trendService.getGroupsByUserDemographics(user.getId());
        List<SocialGroupDTO> locationGroupL = trendService.getGroupsByUserLocation(user.getId());
        List<PlaceDTO> locationPlaceL = trendService.getPlacesByUserLocation(user.getId());
        List<PlaceDTO> placeL = trendService.getUpcomingPlaces();
        
        log.info("[TrendController-getTrends] groups: {}, locationGroups: {}, places: {}", groupL, locationGroupL, placeL);

        model.addAttribute("locationGroups", locationGroupL);
        model.addAttribute("locationPlaces", locationPlaceL);
        model.addAttribute("places", placeL);

        return "trend/trend"; // HTML 파일 이름
    }
}
