package com.dsa.team1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dsa.team1.entity.GroupHashtagEntity;
import com.dsa.team1.entity.SocialGroupEntity;

@Repository
public interface GroupHashtagRepository extends JpaRepository<GroupHashtagEntity, Integer> {
	
	// 그룹에 속한 해시태그 리스트를 가져오는 메소드
    List<GroupHashtagEntity> findByGroup(SocialGroupEntity group);
    
    // 해시태그를 기반으로 그룹 검색
    List<GroupHashtagEntity> findByNameContaining(String hashtag);
    
    // 해시태그 이름만 가져오는 쿼리
    @Query("SELECT h.name FROM GroupHashtagEntity h")
    List<String> findAllHashtags();
}
