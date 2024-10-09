package com.dsa.team1.service;

import java.io.IOException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dsa.team1.entity.UserEntity;
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
    private final BCryptPasswordEncoder passwordEncoder;
    private final FileManager fileManager;;

    @Override
    public void join(String userid, String password, String phone, String email, String location, String name, String username, MultipartFile profileImage) throws IOException {
        String profileImagePath = null;

        if (!profileImage.isEmpty()) {
            profileImagePath = fileManager.saveFile("C:/upload", profileImage);
        }

        UserEntity userEntity = UserEntity.builder()
                .userId(userid)
                .password(passwordEncoder.encode(password))
                .phoneNumber(phone)
                .email(email)
                .profileImage(profileImagePath)  // 사진 저장
                .preferredLocation(location)
                .userName(username)
                .name(name)
                .build();

        ur.save(userEntity);
    }

    @Override
    public boolean idCheck(String id) {
        return !ur.existsById(id);
    }
    
    @Override
    public UserEntity findUserByUserId(String userId) {
        return ur.findByUserId(userId).orElse(null);
    }
}
