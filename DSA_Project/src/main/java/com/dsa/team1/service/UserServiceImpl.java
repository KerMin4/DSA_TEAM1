package com.dsa.team1.service;

import java.io.IOException;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dsa.team1.entity.InterestEntity;
import com.dsa.team1.entity.UserEntity;
import com.dsa.team1.entity.enums.Interest;
import com.dsa.team1.repository.InterestRepository;
import com.dsa.team1.repository.UserRepository;
import com.dsa.team1.util.FileManager;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository ur;
    private final InterestRepository ir;
    private final BCryptPasswordEncoder passwordEncoder;
    private final FileManager fileManager;

    @Override
    public void join(String userid, String password, String phone, String email, String location, String name, String username, Integer gender, MultipartFile profileImage, List<String> interests, int birth) throws IOException {
        String profileImagePath = null;

        if (!profileImage.isEmpty()) {
            profileImagePath = fileManager.saveFile("C:/upload", profileImage);
        }

        // gender 남자 1 여자 2
        
        UserEntity userEntity = UserEntity.builder()
                .userId(userid)
                .password(passwordEncoder.encode(password))
                .phoneNumber(phone)
                .email(email)
                .profileImage(profileImagePath)
                .preferredLocation(location)
                .userName(username)
                .name(name)
                .gender(gender)
                .birth(birth)
                .build();

        ur.save(userEntity);
        
        for(String interest : interests) {
        	InterestEntity interestEntity = InterestEntity.builder()
        											.user(userEntity)
        											.interest(interest)
        											.build();
        	ir.save(interestEntity);
        }
    }

    @Override
    public boolean idCheck(String id) {
        return !ur.existsById(id);
    }
    
    @Override
    public UserEntity findUserByUserId(String userId) {
        return ur.findByUserId(userId).orElse(null);
    }
    
    // 프로필 이미지 수정
    @Override
    public String updateProfileImage(String userId, MultipartFile profileImage) throws IOException {
        UserEntity user = findUserByUserId(userId);
        if (user != null && !profileImage.isEmpty()) {
            String profileImagePath = fileManager.saveFile("C:/upload", profileImage);
            user.setProfileImage(profileImagePath);
            ur.save(user);
            return profileImagePath; 
        }
        return null; 
    }

    // 닉네임 수정
    public void updateNickname(String userId, String nickname) {
        UserEntity user = findUserByUserId(userId);
        if (user != null) {
            user.setName(nickname);
            ur.save(user);
        }
    }

    // 전화번호 수정
    public void updatePhone(String userId, String phone) {
        UserEntity user = findUserByUserId(userId);
        if (user != null) {
            user.setPhoneNumber(phone);
            ur.save(user);
        }
    }

    // 비밀번호 수정
    public void updatePassword(String userId, String password) {
        UserEntity user = findUserByUserId(userId);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(password));
            ur.save(user);
        }
    }

    // 위치 수정
    public void updateLocation(String userId, String location) {
        UserEntity user = findUserByUserId(userId);
        if (user != null) {
            user.setPreferredLocation(location);
            ur.save(user);
        }
    }
}
