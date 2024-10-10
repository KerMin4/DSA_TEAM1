package com.dsa.team1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dsa.team1.entity.InterestEntity;
import com.dsa.team1.entity.UserEntity;

public interface InterestRepository extends JpaRepository<InterestEntity, Long> {

    // 사용자와 연관된 관심사 삭제
    void deleteByUser(UserEntity user);
}
