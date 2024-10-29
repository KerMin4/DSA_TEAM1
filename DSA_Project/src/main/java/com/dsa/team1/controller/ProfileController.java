package com.dsa.team1.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.dsa.team1.entity.UserEntity;
import com.dsa.team1.security.AuthenticatedUser;
import com.dsa.team1.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProfileController {

	
    private final UserService userService;

    @GetMapping("/profile")
    public String getProfile(Model model) {
        AuthenticatedUser user = (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String profileImage = (user.getProfileImage() != null) ? user.getProfileImage() : "default.png";
        model.addAttribute("profileImage", profileImage);
        return "profilePage";
    }

}
