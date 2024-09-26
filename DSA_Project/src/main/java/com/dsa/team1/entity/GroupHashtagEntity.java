package com.dsa.team1.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "GroupHashtag")
public class GroupHashtagEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hashtagId;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private SocialGroupEntity socialGroup;

    private String name;

    // Getters and Setters
}
