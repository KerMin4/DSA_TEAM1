
package com.dsa.team1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dsa.team1.entity.InterestEntity;
import com.dsa.team1.entity.UserEntity;

public interface InterestRepository extends JpaRepository<InterestEntity, Long> {
	@Query("SELECT i FROM InterestEntity i WHERE i.user.userId = :userId")
	List<String> findByUserId(String userId);

	List<InterestEntity> findByUser_UserId(String userId);

	List<InterestEntity> findByUser(UserEntity userEntity);
}