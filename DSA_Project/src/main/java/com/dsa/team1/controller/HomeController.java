
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
    
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userid = auth.getName();  
        
  
        var user = userService.findUserByUserId(userid);

       
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

