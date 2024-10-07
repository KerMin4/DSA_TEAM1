package com.dsa.team1.dto;

import java.time.LocalDateTime;

import com.dsa.team1.entity.enums.PaymentStatus;
import com.dsa.team1.entity.enums.ReservationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
	
	private Integer reservationId;				// PK. Reservation ID
	private String userId;						// FK. User ID
	private Integer placeId;					// FK. Place ID
	private LocalDateTime reservationDate;		// When user has reservated
	private ReservationStatus status;			// Whether the reservation was confirmed
	private Double price;						// Price stated in the Place post
	private LocalDateTime eventDate;			// When the event will takes
	private PaymentStatus paymentStatus;		// Whether the payment is completed
}
