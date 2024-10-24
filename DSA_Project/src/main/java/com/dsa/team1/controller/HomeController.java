
package com.dsa.team1.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.dsa.team1.entity.NotificationEntity;
import com.dsa.team1.repository.NotificationRepository;
import com.dsa.team1.security.AuthenticatedUser;
import com.dsa.team1.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;
    private final NotificationRepository notificationRepository;
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
    
    @GetMapping("/notifications")
	public String gotNotifications(@AuthenticationPrincipal AuthenticatedUser user, Model model ) {
		List<NotificationEntity> notifications = notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(user.getId());
		log.debug("데이터 설마 없니? :{}", notifications);
		log.debug(user.getId());
		model.addAttribute("notifications", notifications);
		return "notifications";
	}
}




/* public class HomeController {

    @GetMapping({"", "/"})
    public String home() {
        return "Main";
    }
}
*/ 

