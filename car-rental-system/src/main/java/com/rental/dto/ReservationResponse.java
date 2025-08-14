package com.rental.dto;

import com.rental.model.CarType;
import com.rental.model.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {

    private String reservationId;
    private String carId;
    private String licensePlate;
    private CarType carType;
    private String customerName;
    private String customerEmail;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private int durationDays;
    private double totalCost;
    private ReservationStatus status;
    private LocalDateTime createdAt;
}