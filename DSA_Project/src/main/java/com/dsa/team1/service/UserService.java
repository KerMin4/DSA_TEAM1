package com.dsa.team1.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dsa.team1.entity.UserEntity;
import com.dsa.team1.entity.enums.Interest;

public interface UserService {
	

    boolean idCheck(String id);
    void join(String userid, String password, String phone, String email, String location, String name, String username,  Integer gender,
              MultipartFile profileImage, List<String> interests, int birth) throws IOException;
    UserEntity findUserByUserId(String userId);
    
  
    String updateProfileImage(String userId, MultipartFile profileImage) throws IOException;
    void updateNickname(String userId, String nickname);
    void updatePhone(String userId, String phone);
    void updatePassword(String userId, String password); 
    void updateLocation(String userId, String location);
	void updateInterests(String userId, List<String> interests);
}



// 원래 코드임
// void join(String userName, String password, String phone, String email, String location, String name, String username/*,List<String> interests */);
// boolean idCheck(String id);

 