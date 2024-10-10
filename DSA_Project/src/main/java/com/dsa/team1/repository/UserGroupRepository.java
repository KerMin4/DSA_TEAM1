package com.dsa.team1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dsa.team1.entity.UserGroupEntity;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroupEntity, Integer> {

	List<UserGroupEntity> findByUser_UserId(String userId);
	
	@Query("SELECT COUNT(u) FROM UserGroupEntity u WHERE u.group.groupId = :groupId AND u.status = com.dsa.team1.entity.enums.UserGroupStatus.APPROVED")
	int countActiveMembersByGroupId(@Param("groupId") Integer groupId);


}
