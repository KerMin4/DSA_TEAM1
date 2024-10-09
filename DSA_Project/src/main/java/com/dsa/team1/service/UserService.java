package com.dsa.team1.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;
import com.dsa.team1.entity.UserEntity;

public interface UserService {
	
	// 프로필 사진땜에 추가함 -나연-
    boolean idCheck(String id);
    void join(String userid, String password, String phone, String email, String location, String name, String username,
              MultipartFile profileImage) throws IOException;
    UserEntity findUserByUserId(String userId);
    
}



// 원래 코드임
// void join(String userName, String password, String phone, String email, String location, String name, String username/*,List<String> interests */);
// boolean idCheck(String id);

 