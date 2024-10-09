package com.dsa.team1.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // 추가
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("dashboard")
public class DashboardController {
     
    @GetMapping("mypage")
    public String myPageCalendar(Model model) {
        model.addAttribute("activePage", "home"); 
        return "dashboard/mypage"; 
    }
    
    @GetMapping("profileedit")
    public String editProfile(Model model) {
        model.addAttribute("activePage", "profile-edit"); 
        return "dashboard/profileedit"; 
    }

    @GetMapping("groupManagement")
    public String groupManagement(Model model) {
        model.addAttribute("activePage", "group-management");
        return "dashboard/groupManagement"; 
    }

    @GetMapping("paymentManagement")
    public String paymentManagement(Model model) {
        model.addAttribute("activePage", "payment-management"); 
        return "dashboard/paymentManagement"; 
    }
}
