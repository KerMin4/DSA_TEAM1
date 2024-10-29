package com.dsa.team1.entity;

import java.time.LocalDateTime;

import org.hibernate.usertype.UserType;
import org.springframework.data.annotation.CreatedDate;

import com.dsa.team1.entity.enums.GroupJoinMethod;
import com.dsa.team1.entity.enums.Interest;
import com.dsa.team1.entity.enums.JoinMethod;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Page		Socialing
 * Function	Group CRUD
 * @version CreateTable_9
 */
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SocialGroup")
public class SocialGroupEntity {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Integer groupId;

    @Column(name = "group_name", nullable = false)
    private String groupName;
    
    @Column(columnDefinition = "text")
    private String description;
    
    @Column(name = "profile_image")
    private String profileImage;
    
    private String location;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "interest")
    private Interest interest;

    @ManyToOne
    @JoinColumn(name = "group_leader_id")
    private UserEntity groupLeader;

    @Enumerated(EnumType.STRING)
    @Column(name = "group_join_method")
    private GroupJoinMethod groupJoinMethod;

    // Maximum number of members
    @Column(name = "member_limit", nullable = false)
    private Integer memberLimit;
    
    @Column(name = "view_count", columnDefinition = "integer default 0")
    private Integer viewCount = 0;
    
    @Column(name = "bookmark_count", columnDefinition = "integer default 0")
    private Integer bookmarkCount = 0;
    
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;
    
    @CreatedDate
	@Column(name = "create_at", columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime createdAt;
    
}
