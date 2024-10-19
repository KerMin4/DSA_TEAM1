package com.dsa.team1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dsa.team1.entity.PhotoEntity;
import com.dsa.team1.entity.PostEntity;
import com.dsa.team1.entity.enums.PostType;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Integer> {

	List<PostEntity> findByGroup_GroupId(Integer groupId);

	// 그룹 ID와 PostType으로 포스트 목록을 조회하는 메서드
    List<PostEntity> findByGroup_GroupIdAndPostType(Integer groupId, PostType postType);
    

}
