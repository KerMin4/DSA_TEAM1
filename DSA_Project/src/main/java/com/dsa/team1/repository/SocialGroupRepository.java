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

    // 검색어, 카테고리, 지역 필터링을 처리하는 쿼리
    @Query("SELECT g FROM SocialGroupEntity g " +
           "WHERE (:query IS NULL OR LOWER(g.groupName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(g.description) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR g.groupId IN (SELECT gh.group.groupId FROM GroupHashtagEntity gh WHERE LOWER(gh.name) LIKE LOWER(CONCAT('%', :query, '%')))) " + // 해시태그 검색 추가
           "AND (:joinMethod IS NULL OR g.groupJoinMethod = :joinMethod) " +
           "AND (:location IS NULL OR LOWER(g.location) LIKE LOWER(CONCAT('%', :location, '%'))) " + // String 타입 location 필터링
           "AND (:category IS NULL OR g.interest = :category)")
    List<SocialGroupEntity> filterGroups(@Param("query") String query, 
                                         @Param("joinMethod") String joinMethod, 
                                         @Param("location") String location,
                                         @Param("category") Interest category);


    // 그룹 ID로 그룹 헤더 이미지를 찾는 메소드
    @Query("SELECT g.profileImage FROM SocialGroupEntity g WHERE g.groupId = :groupId")
    String findGroupHeaderImageByGroupId(@Param("groupId") Integer groupId);

    // 조회수순 정렬 쿼리
    @Query("SELECT g FROM SocialGroupEntity g ORDER BY g.viewCount DESC")
    List<SocialGroupEntity> findAllOrderByViewCountDesc();

    // 북마크순 쿼리
    @Query("SELECT g FROM SocialGroupEntity g ORDER BY g.bookmarkCount DESC")
    List<SocialGroupEntity> findAllOrderByBookmarkCountDesc();

    // 유저가 생성한 그룹을 가져오는 메서드 - 나연 -
    List<SocialGroupEntity> findByGroupLeaderUserId(String userId);
}
