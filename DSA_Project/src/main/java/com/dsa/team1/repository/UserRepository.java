package com.dsa.team1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dsa.team1.entity.SocialGroupEntity;
import com.dsa.team1.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
	
    Optional<UserEntity> findByUserId(String userId);
    
    // 나연
    @Query("SELECT ug.group FROM UserGroupEntity ug WHERE ug.user.userId = :userId AND ug.status = 'APPROVED'")
    List<SocialGroupEntity> findApprovedGroupsByUserId(@Param("userId") String userId);

}


