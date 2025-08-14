package com.rental.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Builder.Default
    private String id = UUID.randomUUID().toString();

    private String carId;
    private String customerName;
    private String customerEmail;
    private LocalDateTime startDateTime;
    private int durationDays;
    private double totalCost;

    @Builder.Default
    private ReservationStatus status = ReservationStatus.ACTIVE;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public LocalDateTime getEndDateTime() {
        return startDateTime.plusDays(durationDays);
    }

    public boolean overlapsWithPeriod(LocalDateTime periodStart, LocalDateTime periodEnd) {
        if (status != ReservationStatus.ACTIVE) {
            return false;
        }

        LocalDateTime reservationEnd = getEndDateTime();

        return startDateTime.isBefore(periodEnd) && reservationEnd.isAfter(periodStart);
    }
}