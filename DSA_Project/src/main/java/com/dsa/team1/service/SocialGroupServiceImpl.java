package com.dsa.team1.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dsa.team1.dto.SocialGroupDTO;
import com.dsa.team1.entity.GroupHashtagEntity;
import com.dsa.team1.entity.SocialGroupEntity;
import com.dsa.team1.entity.UserEntity;
import com.dsa.team1.entity.enums.GroupJoinMethod;
import com.dsa.team1.repository.GroupHashtagRepository;
import com.dsa.team1.repository.SocialGroupRepository;
import com.dsa.team1.repository.UserRepository;
import com.dsa.team1.util.FileManager;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class SocialGroupServiceImpl implements SocialGroupService {
    
    private final SocialGroupRepository socialGroupRepository;		// 그룹 저장소
    private final UserRepository userRepository; 					// User 저장소
    private final FileManager fileManager;							// 파일 저장/삭제 처리 유틸 클래스
    private final GroupHashtagRepository groupHashtagRepository;	// 해시태그 저장소 추가
    
    @Override
    public void create(SocialGroupDTO socialGroupDTO, String uploadPath, MultipartFile upload, String hashtags, GroupJoinMethod joinMethod) throws IOException {
        // 1. groupLeaderId를 사용해 UserEntity 조회
        UserEntity groupLeader = userRepository.findById(socialGroupDTO.getGroupLeaderId())
                .orElseThrow(() -> new IllegalArgumentException("리더 사용자 ID를 찾을 수 없습니다."));
        
        // 2. SocialGroupDTO를 SocialGroupEntity로 변환
        SocialGroupEntity socialGroupEntity = SocialGroupEntity.builder()
                .groupName(socialGroupDTO.getGroupName())
                .description(socialGroupDTO.getDescription())
                //.interests(socialGroupDTO.getInterests())
                .location(socialGroupDTO.getLocation())
                .groupLeader(groupLeader)  
                .groupJoinMethod(socialGroupDTO.getGroupJoinMethod())  // 가입 승인 권한 값 설정
                .memberLimit(socialGroupDTO.getMemberLimit())
                .eventDate(socialGroupDTO.getEventDate())
                .build();

        // 3. 첨부파일이 있는 경우 파일 저장
        if (upload != null && !upload.isEmpty()) {
            String fileName = fileManager.saveFile(uploadPath, upload);  
            socialGroupEntity.setProfileImage(fileName);  
        }

        // 4. 그룹 저장
        socialGroupRepository.save(socialGroupEntity);
        log.debug("새로운 그룹 생성: {}", socialGroupEntity);

        // 5. 해시태그 저장 로직 
        if (hashtags != null && !hashtags.isEmpty()) {
            String[] hashtagArray = hashtags.split(",");
            for (String hashtag : hashtagArray) {
                GroupHashtagEntity groupHashtag = GroupHashtagEntity.builder()
                    .group(socialGroupEntity)  
                    .name(hashtag.trim())
                    .build();
                groupHashtagRepository.save(groupHashtag);
            }
        }
    }
    
}
