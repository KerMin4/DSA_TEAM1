package com.dsa.team1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsa.team1.entity.MemberHashtagEntity;

@Repository
public interface MemberHashtagRepository extends JpaRepository<MemberHashtagEntity, Integer> {

	   List<MemberHashtagEntity> findByUser_UserId(String userId);
}
