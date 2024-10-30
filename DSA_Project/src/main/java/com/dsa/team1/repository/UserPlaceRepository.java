package com.dsa.team1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsa.team1.dto.UserPlaceDTO;
import com.dsa.team1.entity.UserPlaceEntity;

@Repository
public interface UserPlaceRepository extends JpaRepository<UserPlaceEntity, Integer> {

	Optional<UserPlaceEntity> findByUser_UserIdAndPlace_PlaceId(String userId, Integer placeId);

	boolean existsByUser_UserIdAndPlace_PlaceId(String userId, Integer placeId);

	int countActiveMembersByPlace_PlaceId(Integer placeId);

	List<UserPlaceEntity> findByUser_UserId(String id);

}
