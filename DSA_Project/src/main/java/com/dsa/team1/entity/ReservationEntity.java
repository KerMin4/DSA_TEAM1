package com.dsa.team1.entity;

import java.time.LocalDateTime;

import org.hibernate.usertype.UserType;
import org.springframework.data.annotation.CreatedDate;

import com.dsa.team1.entity.enums.JoinMethod;
import com.dsa.team1.entity.enums.PaymentStatus;
import com.dsa.team1.entity.enums.ReservationStatus;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Page Place 
 * Function Reservation 
 * @version CreateTable_9
 */
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Reservation")
public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Integer reservationId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private PlaceEntity place;

    @CreatedDate
	@Column(name = "reservation_date", columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime reservationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status;

    private Double price;
    
	@Column(name = "event_date")
    private LocalDateTime eventDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

}