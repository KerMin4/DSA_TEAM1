package com.dsa.team1.entity;

import java.sql.Timestamp;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.transaction.Transaction;

/*
 * Table for SocialGroup of Socialing
 */

@Entity
@Table(name = "SocialGroup")
public class SocialGroupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    @Column(name = "group_name")
    private String groupName;

    private String description;

    @Column(name = "profile_image")
    private String profileImage;

    private String interest;
    private String location;

    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(name = "group_leader_id")
    private UserEntity groupLeader;

    @Enumerated(EnumType.STRING)
    @Column(name = "join_method")
    private JoinMethod joinMethod;

    @Column(name = "member_limit")
    private Integer memberLimit;

    @Column(name = "event_date")
    private Timestamp eventDate;

    @OneToMany(mappedBy = "socialGroup")
    private Set<UserGroupEntity> userGroups;

    @OneToMany(mappedBy = "socialGroup")
    private Set<TrendEntity> trends;

    @OneToMany(mappedBy = "socialGroup")
    private Set<TransactionEntity> transactions;

    // Getters and Setters

    public enum JoinMethod {
        AUTO, APPROVAL
    }
}
