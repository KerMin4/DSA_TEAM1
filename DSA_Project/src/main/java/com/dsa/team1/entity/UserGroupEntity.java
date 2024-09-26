package com.dsa.team1.entity;

import java.sql.Timestamp;

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

/*
 * User - SocialGroup Mapping Table
 * Managing ManyToMany Relationship between User and SocialGroup
 * Managing Role of each members
 */

@Entity
@Table(name = "UserGroup")
public class UserGroupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userGroupId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private SocialGroupEntity socialGroup;

    @Column(name = "joined_at")
    private Timestamp joinedAt;

    @Enumerated(EnumType.STRING)
    private Status status;

    // Getters and Setters

    public enum Status {
        PENDING, APPROVED, REJECTED
    }
}
