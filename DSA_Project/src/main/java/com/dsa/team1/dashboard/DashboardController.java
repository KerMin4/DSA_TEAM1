package com.dsa.team1.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.dsa.team1.security.AuthenticatedUser;
import com.dsa.team1.service.UserService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("dashboard") 
public class DashboardController {

    private final UserService userService;

    // 마이페이지
    @GetMapping("mypage")
    public String myPage(Model model) {
        model.addAttribute("activePage", "home");  // 홈을 활성화
        return "dashboard/mypage"; 
    }

    // 그룹 관리 페이지
    @GetMapping("groupManagement")
    public String groupManagement(Model model) {
        model.addAttribute("activePage", "group-management");  // 그룹 관리를 활성화
        return "dashboard/groupManagement"; 
    }

    // 결제 관리 페이지
    @GetMapping("paymentManagement")
    public String paymentManagement(Model model) {
        model.addAttribute("activePage", "payment-management");  // 결제 관리를 활성화
        return "dashboard/paymentManagement"; 
    }

    // 프로필 수정 페이지
    @GetMapping("profileedit")
    public String editProfile(Model model) {
        model.addAttribute("activePage", "profile-edit");  // 프로필 수정을 활성화
        return "dashboard/profileedit"; 
    }

    // 프로필 이미지 수정
    @PostMapping("editProfileImage")
    public String editProfileImage(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                   @RequestParam("profileImage") MultipartFile profileImage,
                                   RedirectAttributes redirectAttributes) {
        String userId = authenticatedUser.getUsername();
        try {
            String newProfileImage = userService.updateProfileImage(userId, profileImage);
            authenticatedUser.updateProfileImage(newProfileImage);  

            redirectAttributes.addFlashAttribute("message", "프로필 이미지가 변경되었습니다.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "프로필 이미지 변경에 실패했습니다.");
        }
        return "redirect:/dashboard/mypage";
    }

    // 닉네임 수정
    @PostMapping("editNickname")
    public String editNickname(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                               @RequestParam("nickname") String nickname,
                               RedirectAttributes redirectAttributes) {
        String userId = authenticatedUser.getUsername();
        userService.updateNickname(userId, nickname);
        authenticatedUser.updateNickname(nickname);  // 세션의 닉네임 업데이트

        redirectAttributes.addFlashAttribute("message", "닉네임이 변경되었습니다.");
        return "redirect:/dashboard/mypage";
    }

    // 전화번호 수정
    @PostMapping("editPhone")
    public String editPhone(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                            @RequestParam("phone") String phone,
                            RedirectAttributes redirectAttributes) {
        String userId = authenticatedUser.getUsername();
        userService.updatePhone(userId, phone);

        redirectAttributes.addFlashAttribute("message", "전화번호가 변경되었습니다.");
        return "redirect:/dashboard/mypage";
    }

    // 비밀번호 수정
    @PostMapping("editPassword")
    public String editPassword(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                               @RequestParam("password") String password,
                               RedirectAttributes redirectAttributes) {
        String userId = authenticatedUser.getUsername();
        userService.updatePassword(userId, password);

        redirectAttributes.addFlashAttribute("message", "비밀번호가 변경되었습니다.");
        return "redirect:/dashboard/mypage";
    }

    // 위치 수정
    @PostMapping("editLocation")
    public String editLocation(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                               @RequestParam("location") String location,
                               RedirectAttributes redirectAttributes) {
        String userId = authenticatedUser.getUsername();
        userService.updateLocation(userId, location);

        redirectAttributes.addFlashAttribute("message", "위치가 변경되었습니다.");
        return "redirect:/dashboard/mypage";
    }
}
