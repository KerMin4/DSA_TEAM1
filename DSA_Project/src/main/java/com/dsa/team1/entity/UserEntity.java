package com.dsa.team1.entity;

import java.time.LocalDateTime;

import org.hibernate.usertype.UserType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.dsa.team1.entity.enums.JoinMethod;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "User")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    // Real name
    @Column(name = "username", nullable = false)
    private String username;
    
    @Column(name = "password", nullable = false)
    private String password;
    
    //Nickname
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;
    private String email;
    
    @Column(columnDefinition = "text")
    private String interests;
    
    @Column(name = "preferred_location")
    private String preferredLocation;

    @Enumerated(EnumType.STRING)
    @Column(name = "join_method", nullable = false)
    private JoinMethod joinMethod;

    @Column(name = "profile_image")
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;

    @CreatedDate
	@Column(name = "created_at", columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime createdAt;
    
    @LastModifiedDate
	@Column(name = "updated_at", columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime updatedAt;

}