package com.dsa.team1.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.dsa.team1.entity.enums.JoinMethod;
import com.dsa.team1.entity.enums.UserType;

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

/**
 * Page		Join, Login, etc
 * Function	User Information
 * @version CreateTable_9
 */
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "User")
public class UserEntity {
    @Id
    @Column(name = "user_id")
    private String userId;

    // Real name
    @Column(name = "username")
    private String userName;
    
    @Column(name = "password", nullable = false)
    private String password;
    
    //Nickname
    @Column(name = "name", length = 50)
    private String name;
    
    private Integer birth;
    private Integer gender;
    
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;
    private String email;
    
    @Column(name = "preferred_location")
    private String preferredLocation;


    @Column(name = "join_method")
    private String joinMethod;

    @Column(name = "profile_image")
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", columnDefinition = "ENUM('user', 'vendor') default 'user'")
    private UserType userType;

    @CreatedDate
	@Column(name = "created_at", columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime createdAt;
    
    @LastModifiedDate
	@Column(name = "updated_at", columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime updatedAt;

}