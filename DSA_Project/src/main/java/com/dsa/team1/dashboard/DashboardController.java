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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.dsa.team1.entity.SocialGroupEntity;
import com.dsa.team1.entity.enums.Interest;
import com.dsa.team1.security.AuthenticatedUser;
import com.dsa.team1.service.SocialGroupService;
import com.dsa.team1.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Controller
@RequiredArgsConstructor
@RequestMapping("dashboard")
public class DashboardController {

    private final UserService userService;
    private final SocialGroupService socialGroupService;

    @GetMapping("/mypage")
    public String myPage(@AuthenticationPrincipal AuthenticatedUser authenticatedUser, Model model) {
        String userId = authenticatedUser.getUsername();

        // 사용자가 가입한 그룹 목록 가져오기
        List<SocialGroupEntity> joinedGroups = socialGroupService.getJoinedGroupsByUser(userId);
        
        // 사용자가 생성한 그룹 목록 가져오기 (방장인 그룹)
        List<SocialGroupEntity> createdGroups = socialGroupService.getGroupsCreatedByUser(userId);

        // 관심사 통계 데이터를 가져옴
        Map<Interest, Long> interestStatistics = socialGroupService.getInterestGroupStatistics(userId);
        
        List<Map<String, Object>> groupEvents = joinedGroups.stream()
            .map(group -> {
                Map<String, Object> event = new HashMap<>();
                event.put("title", group.getGroupName());
                event.put("start", group.getEventDate().toString());
                return event;
            })
            .collect(Collectors.toList());

        groupEvents.addAll(createdGroups.stream()
            .map(group -> {
                Map<String, Object> event = new HashMap<>();
                event.put("title", group.getGroupName());
                event.put("start", group.getEventDate().toString());  
                return event;
            })
            .collect(Collectors.toList()));

        // 모델에 데이터 추가
        model.addAttribute("interestStatistics", interestStatistics);
        model.addAttribute("activePage", "home");
        model.addAttribute("joinedGroups", joinedGroups);
        model.addAttribute("groupEvents", groupEvents);  

        return "dashboard/mypage";
    }

    @GetMapping("groupManagement")
    public String groupManagement(@AuthenticationPrincipal AuthenticatedUser authenticatedUser, Model model) {
        // 유저 ID 가져오기
        String userId = authenticatedUser.getUsername();

        // 유저가 생성한 그룹 가져오기
        List<SocialGroupEntity> createdGroups = socialGroupService.getGroupsCreatedByUser(userId);
        
        // 유저가 가입한 그룹 가져오기
        List<SocialGroupEntity> joinedGroups = socialGroupService.getJoinedGroupsByUser(userId);

        // 관심사 통계 데이터 가져오기
        Map<Interest, Long> interestStatistics = socialGroupService.getInterestGroupStatistics(userId);

        // 현재 멤버 수 계산 (생성된 그룹 + 가입한 그룹 모두 처리)
        Map<Integer, Integer> memberCountMap = new HashMap<>();
        for (SocialGroupEntity group : createdGroups) {
            int memberCount = socialGroupService.getMemberCountByGroup(group);
            memberCountMap.put(group.getGroupId(), memberCount);
        }
      
        for (SocialGroupEntity group : joinedGroups) {
            int memberCount = socialGroupService.getMemberCountByGroup(group);
            memberCountMap.put(group.getGroupId(), memberCount);
        }

      
        model.addAttribute("createdGroups", createdGroups);
        model.addAttribute("joinedGroups", joinedGroups); 
        model.addAttribute("interestStatistics", interestStatistics);
        model.addAttribute("activePage", "group-management");
        model.addAttribute("memberCountMap", memberCountMap); 
        
        return "dashboard/groupManagement";
    }

    @PostMapping("deleteGroup")
    public String deleteGroup(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                              @RequestParam("groupId") Integer groupId,
                              RedirectAttributes redirectAttributes) {
        try {
            log.info("삭제하려는 그룹 ID: {}", groupId);
            String userId = authenticatedUser.getUsername(); 
            socialGroupService.deleteGroupById(groupId, userId); 
            log.info("그룹 ID {} 삭제 성공", groupId);
            redirectAttributes.addFlashAttribute("message", "그룹이 성공적으로 삭제되었습니다.");
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("error", "그룹 리더만 삭제할 수 있습니다.");
        } catch (Exception e) {
            log.error("그룹 삭제 중 오류 발생: 그룹 ID {}", groupId, e);
            redirectAttributes.addFlashAttribute("error", "그룹 삭제에 실패했습니다.");
        }
        return "redirect:/dashboard/groupManagement";
    }

    // 그룹 탈퇴 기능 추가
    @PostMapping("leaveGroup")
    public String leaveGroup(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                             @RequestParam("groupId") Integer groupId,
                             RedirectAttributes redirectAttributes) {
        String userId = authenticatedUser.getUsername(); 

        try {
            socialGroupService.leaveGroup(userId, groupId); 
            redirectAttributes.addFlashAttribute("message", "그룹에서 탈퇴되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "탈퇴에 실패했습니다: " + e.getMessage());
        }
        return "redirect:/dashboard/groupManagement"; 
    }

    // 결제 관리 페이지
    @GetMapping("paymentManagement")
    public String paymentManagement(Model model) {
        model.addAttribute("activePage", "payment-management");
        return "dashboard/paymentManagement";
    }

    // 프로필 수정 페이지
    @GetMapping("profileedit")
    public String editProfile(Model model) {
        model.addAttribute("activePage", "profile-edit");
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
            log.error("프로필 이미지 변경 실패", e);
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
        authenticatedUser.updateNickname(nickname);  
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
