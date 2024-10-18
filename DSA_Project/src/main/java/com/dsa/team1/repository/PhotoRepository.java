package com.dsa.team1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dsa.team1.entity.BookmarkEntity;
import com.dsa.team1.entity.PhotoEntity;
import com.dsa.team1.entity.SocialGroupEntity;
import com.dsa.team1.entity.UserEntity;

@Repository
public interface PhotoRepository extends JpaRepository<PhotoEntity, Integer> {

	List<PhotoEntity> findByGroup_GroupId(Integer groupId);

    List<PhotoEntity> findByPost_PostId(Integer postId);
    
}
