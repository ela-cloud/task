package com.rental.model;

import lombok.Getter;

@Getter
public enum CarType {
    SEDAN("Sedan", 50.0),
    SUV("SUV", 80.0),
    VAN("Van", 100.0);

    private final String displayName;
    private final double dailyRate;

    CarType(String displayName, double dailyRate) {
        this.displayName = displayName;
        this.dailyRate = dailyRate;
    }
}