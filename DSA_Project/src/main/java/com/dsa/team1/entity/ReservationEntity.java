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

@Entity
@Table(name = "Reservation")
public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private PlaceEntity place;

    @Column(name = "reservation_date")
    private Timestamp reservationDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "number_of_people")
    private Integer numberOfPeople;

    @Column(name = "price")
    private Double price;  // 총 결제 금액

    @Column(name = "event_date")
    private Timestamp eventDate;  // 플레이스의 이벤트 날짜

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    
    public enum Status {
        PENDING, CONFIRMED, CANCELED
    }

    public enum PaymentStatus {
        PENDING, COMPLETED, FAILED
    }
}

