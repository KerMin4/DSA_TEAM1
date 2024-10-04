package com.dsa.team1.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("dashboard")
public class DashboardController {
     
    // http://localhost:7272/kkirikkiri/dashboard/mypage (대시보드 수정)
    @GetMapping("mypage")
    public String myPageCalendar() {
        return "dashboard/mypage"; 
    }
    
    // http://localhost:7272/kkirikkiri/dashboard/profileedit (프로필 수정)
    @GetMapping("profileedit")
    public String editProfile() {
        return "dashboard/profileedit"; 
    }

    // http://localhost:7272/kkirikkiri/dashboard/groupManagement (그룹 관리)
    @GetMapping("groupManagement")
    public String groupManagement() {
        return "dashboard/groupManagement"; 
    }

    // http://localhost:7272/kkirikkiri/dashboard/paymentManagement (결제 관리)
    @GetMapping("paymentManagement")
    public String paymentManagement() {
        return "dashboard/paymentManagement"; 
    }
}
