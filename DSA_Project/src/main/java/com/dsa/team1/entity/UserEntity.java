package com.dsa.team1.entity;

import java.sql.Timestamp;
import java.util.Set;

import javax.management.Notification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/*
 * user information
 */

@Entity
@Table(name = "User")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String username;
    private String password;
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    private String email;
    private String interests;

    @Column(name = "preferred_location")
    private String preferredLocation;

    @Enumerated(EnumType.STRING)
    @Column(name = "registration_type")
    private RegistrationType registrationType;

    @Column(name = "profile_image")
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type")
    private UserType userType;

    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "groupLeader")
    private Set<SocialGroupEntity> socialGroups;

    @OneToMany(mappedBy = "user")
    private Set<UserGroupEntity> userGroups;

    @OneToMany(mappedBy = "user")
    private Set<NotificationEntity> notifications;

    @OneToMany(mappedBy = "vendor")
    private Set<PlaceEntity> places;

    @OneToMany(mappedBy = "user")
    private Set<UserPlaceEntity> userPlaces;

    @OneToMany(mappedBy = "user")
    private Set<MemberHashtagEntity> memberHashtags;

    @OneToMany(mappedBy = "leader")
    private Set<TransactionEntity> transactions;

    // Getters and Setters

    public enum UserType {
        USER, VENDOR
    }

    public enum RegistrationType {
        WEBSITE, KAKAO
    }
}

