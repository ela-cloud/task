package com.rental.controller;

import com.rental.dto.ReservationRequest;
import com.rental.dto.ReservationResponse;
import com.rental.model.Car;
import com.rental.model.CarType;
import com.rental.service.CarRentalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/car-rental")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CarRentalController {

    private final CarRentalService carRentalService;

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> home() {
        return ResponseEntity.ok(Map.of(
            "message", "🚗 Car Rental System API",
            "version", "1.0.0",
            "status", "running",
            "endpoints", "/api/car-rental/cars, /api/car-rental/reservations"
        ));
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservation(
        @Valid @RequestBody ReservationRequest request) {
        log.info("Creating reservation for {} - {}", request.getCustomerName(), request.getCarType());

        ReservationResponse response = carRentalService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/reservations/{id}")
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable String id) {
        Optional<ReservationResponse> reservation = carRentalService.getReservation(id);

        return reservation
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Map<String, String>> cancelReservation(@PathVariable String id) {
        boolean cancelled = carRentalService.cancelReservation(id);

        if (cancelled) {
            return ResponseEntity.ok(Map.of("message", "Reservation cancelled successfully"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/reservations/customer/{email}")
    public ResponseEntity<List<ReservationResponse>> getCustomerReservations(
        @PathVariable String email) {
        List<ReservationResponse> reservations = carRentalService.getReservationsForCustomer(email);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/cars")
    public ResponseEntity<Map<CarType, List<Car>>> getAllCars() {
        Map<CarType, List<Car>> carsByType = Map.of(
            CarType.SEDAN, carRentalService.getCarsByType(CarType.SEDAN),
            CarType.SUV, carRentalService.getCarsByType(CarType.SUV),
            CarType.VAN, carRentalService.getCarsByType(CarType.VAN)
        );
        return ResponseEntity.ok(carsByType);
    }

    @GetMapping("/cars/{carType}")
    public ResponseEntity<List<Car>> getCarsByType(@PathVariable CarType carType) {
        List<Car> cars = carRentalService.getCarsByType(carType);
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/cars/available")
    public ResponseEntity<List<Car>> getAvailableCars(
        @RequestParam CarType carType,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime) {

        List<Car> availableCars = carRentalService.getAvailableCars(carType, startDateTime, endDateTime);
        return ResponseEntity.ok(availableCars);
    }

    @GetMapping("/availability")
    public ResponseEntity<Map<CarType, Long>> getAvailability(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime) {

        Map<CarType, Long> availability = carRentalService.getAvailabilityByType(startDateTime, endDateTime);
        return ResponseEntity.ok(availability);
    }
}