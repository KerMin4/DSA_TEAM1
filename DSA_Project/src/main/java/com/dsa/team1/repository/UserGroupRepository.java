package com.dsa.team1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dsa.team1.entity.SocialGroupEntity;
import com.dsa.team1.entity.UserEntity;
import com.dsa.team1.entity.UserGroupEntity;
import com.dsa.team1.entity.enums.UserGroupStatus;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroupEntity, Integer> {

	List<UserGroupEntity> findByUser_UserId(String userId);
	
	@Query("SELECT COUNT(u) FROM UserGroupEntity u WHERE u.group.groupId = :groupId AND u.status = com.dsa.team1.entity.enums.UserGroupStatus.APPROVED")
	int countActiveMembersByGroupId(@Param("groupId") Integer groupId);
	
	@Query("SELECT u.user FROM UserGroupEntity u WHERE u.group.groupId = :groupId AND u.status = 'APPROVED'")
	List<UserEntity> findActiveMembersByGroupId(@Param("groupId") Integer groupId);

	@Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM UserGroupEntity u WHERE u.user.userId = :userId AND u.group.groupId = :groupId")
    boolean existsByUserAndGroupId(@Param("userId") String userId, @Param("groupId") Integer groupId);

	@Query("SELECT COUNT(u) > 0 FROM UserGroupEntity u WHERE u.user.userId = :userId AND u.group.groupId = :groupId AND u.status = :status")
    boolean existsByUserAndGroupIdAndStatus(@Param("userId") String userId, 
                                            @Param("groupId") Integer groupId, 
                                            @Param("status") UserGroupStatus status);

	@Query("SELECT u FROM UserGroupEntity u WHERE u.user.userId = :userId AND u.group.groupId = :groupId AND u.status = :status")
    Optional<UserGroupEntity> findByUserAndGroupIdAndStatus(@Param("userId") Integer userId, 
                                                            @Param("groupId") Integer groupId, 
                                                            @Param("status") UserGroupStatus status);

	boolean existsByUser_UserIdAndGroup_GroupId(String userId, Integer groupId);
	

}
