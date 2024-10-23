package com.dsa.team1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsa.team1.entity.BookmarkEntity;
import com.dsa.team1.entity.PlaceEntity;
import com.dsa.team1.entity.SocialGroupEntity;
import com.dsa.team1.entity.UserEntity;

@Repository
public interface BookmarkRepository extends JpaRepository<BookmarkEntity, Integer> {

	Optional<BookmarkEntity> findByUserAndGroup(UserEntity user, SocialGroupEntity group);
	
	// 북마크 상태 확인 (존재 여부만 체크)
    boolean existsByUserAndGroup(UserEntity user, SocialGroupEntity group);
    
    List<BookmarkEntity> findByUser(UserEntity user);

	void deleteByGroup(SocialGroupEntity group);

	Boolean existsByUser_UserIdAndPlace_PlaceId(String id, Integer placeId);

	Optional<BookmarkEntity> findByUserAndPlace(UserEntity userEntity, PlaceEntity placeEntity);
  
}

