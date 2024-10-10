package com.dsa.team1.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dsa.team1.entity.SocialGroupEntity;
import com.dsa.team1.entity.enums.Interest;

@Repository
public interface SocialGroupRepository extends JpaRepository<SocialGroupEntity, Integer> {
	
    // 그룹 이름 또는 설명에 검색어가 포함된 그룹 찾기
    List<SocialGroupEntity> findByGroupNameContainingOrDescriptionContaining(String groupName, String description);

    // 새로운 방식의 그룹 검색 쿼리
    @Query("SELECT g FROM SocialGroupEntity g " +
           "WHERE LOWER(g.groupName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(g.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<SocialGroupEntity> searchByGroupNameOrDescription(@Param("query") String query);

    // 기존 필터링 메서드 유지
    @Query("SELECT g FROM SocialGroupEntity g " +
           "WHERE (:query IS NULL OR LOWER(g.groupName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(g.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND (:joinMethod IS NULL OR g.groupJoinMethod = :joinMethod) " +
           "AND (:location IS NULL OR g.location = :location) " +
           "AND (:category IS NULL OR g.interest = :category)")
    List<SocialGroupEntity> filterGroups(@Param("query") String query, 
                                         @Param("joinMethod") String joinMethod, 
                                         @Param("location") String location,
                                         @Param("category") Interest category);

    // 그룹 ID로 그룹 헤더 이미지를 찾는 메소드
    @Query("SELECT g.profileImage FROM SocialGroupEntity g WHERE g.groupId = :groupId")
    String findGroupHeaderImageByGroupId(@Param("groupId") Integer groupId);

    
}
