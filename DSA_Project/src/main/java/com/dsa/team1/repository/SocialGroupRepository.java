package com.dsa.team1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dsa.team1.entity.SocialGroupEntity;

@Repository
public interface SocialGroupRepository extends JpaRepository<SocialGroupEntity, Integer> {

	// 그룹 이름 또는 설명에 검색어가 포함된 그룹 찾기
    List<SocialGroupEntity> findByGroupNameContainingOrDescriptionContaining(String groupName, String description);

    @Query("SELECT g FROM SocialGroupEntity g " +
            "WHERE (:query IS NULL OR g.groupName LIKE %:query% OR g.description LIKE %:query%) " +
            "AND (:category IS NULL OR g.interests = :category) " +
            "AND (:region IS NULL OR g.location = :region)")
     List<SocialGroupEntity> filterGroups(@Param("query") String query, 
                                          @Param("category") String category, 
                                          @Param("region") String region);
    
}
