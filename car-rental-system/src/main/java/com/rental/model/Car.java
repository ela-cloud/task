package com.rental.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Car {

    @Builder.Default
    private String id = UUID.randomUUID().toString();

    private String licensePlate;
    private CarType carType;
    private String brand;
    private String model;
    private int year;

    @Builder.Default
    private boolean available = true;

    public double getDailyRate() {
        return carType.getDailyRate();
    }
}