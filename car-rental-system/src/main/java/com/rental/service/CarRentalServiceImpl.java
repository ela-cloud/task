package com.rental.service;

import com.rental.dto.ReservationRequest;
import com.rental.dto.ReservationResponse;
import com.rental.exception.CarNotAvailableException;
import com.rental.exception.InvalidReservationException;
import com.rental.model.Car;
import com.rental.model.CarType;
import com.rental.model.Reservation;
import com.rental.model.ReservationStatus;
import com.rental.repository.CarRepository;
import com.rental.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarRentalServiceImpl implements CarRentalService {

    private final CarRepository carRepository;
    private final ReservationRepository reservationRepository;

    @Override
    public ReservationResponse createReservation(ReservationRequest request) {
        log.info("Creating reservation for car type: {} from {} for {} days",
            request.getCarType(), request.getStartDateTime(), request.getDurationDays());

        validateReservationRequest(request);

        LocalDateTime endDateTime = request.getStartDateTime().plusDays(request.getDurationDays());

        Optional<Car> availableCar = findAvailableCarForPeriod(
            request.getCarType(),
            request.getStartDateTime(),
            endDateTime
        );

        if (availableCar.isEmpty()) {
            throw new CarNotAvailableException(
                String.format("No %s available for the requested period: %s to %s",
                    request.getCarType().getDisplayName(),
                    request.getStartDateTime(),
                    endDateTime)
            );
        }

        Car car = availableCar.get();
        double totalCost = calculateTotalCost(car, request.getDurationDays());

        Reservation reservation = Reservation.builder()
            .carId(car.getId())
            .customerName(request.getCustomerName())
            .customerEmail(request.getCustomerEmail())
            .startDateTime(request.getStartDateTime())
            .durationDays(request.getDurationDays())
            .totalCost(totalCost)
            .build();

        Reservation savedReservation = reservationRepository.save(reservation);

        log.info("Successfully created reservation: {} for car: {}",
            savedReservation.getId(), car.getId());

        return mapToReservationResponse(savedReservation, car);
    }

    @Override
    public Optional<ReservationResponse> getReservation(String reservationId) {
        return reservationRepository.findById(reservationId)
            .map(reservation -> {
                Car car = carRepository.findById(reservation.getCarId())
                    .orElseThrow(() -> new IllegalStateException("Car not found for reservation"));
                return mapToReservationResponse(reservation, car);
            });
    }

    @Override
    public boolean cancelReservation(String reservationId) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);

        if (reservationOpt.isEmpty()) {
            return false;
        }

        Reservation reservation = reservationOpt.get();

        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new InvalidReservationException("Cannot cancel reservation that is not active");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        log.info("Cancelled reservation: {}", reservationId);
        return true;
    }

    @Override
    public List<ReservationResponse> getReservationsForCustomer(String customerEmail) {
        return reservationRepository.findByCustomerEmail(customerEmail).stream()
            .map(reservation -> {
                Car car = carRepository.findById(reservation.getCarId())
                    .orElseThrow(() -> new IllegalStateException("Car not found for reservation"));
                return mapToReservationResponse(reservation, car);
            })
            .sorted(Comparator.comparing(ReservationResponse::getCreatedAt).reversed())
            .collect(Collectors.toList());
    }

    @Override
    public List<Car> getAvailableCars(CarType carType, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Car> carsOfType = carRepository.findByCarType(carType);

        return carsOfType.stream()
            .filter(car -> isCarAvailableForPeriod(car.getId(), startDateTime, endDateTime))
            .collect(Collectors.toList());
    }

    @Override
    public List<Car> getCarsByType(CarType carType) {
        return carRepository.findByCarType(carType);
    }

    @Override
    public Map<CarType, Long> getAvailabilityByType(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return Arrays.stream(CarType.values())
            .collect(Collectors.toMap(
                carType -> carType,
                carType -> (long) getAvailableCars(carType, startDateTime, endDateTime).size()
            ));
    }

    private void validateReservationRequest(ReservationRequest request) {
        if (request.getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new InvalidReservationException("Start date cannot be in the past");
        }

        if (request.getDurationDays() <= 0) {
            throw new InvalidReservationException("Duration must be positive");
        }

        if (request.getDurationDays() > 365) {
            throw new InvalidReservationException("Duration cannot exceed 365 days");
        }
    }

    private Optional<Car> findAvailableCarForPeriod(CarType carType, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Car> carsOfType = carRepository.findByCarType(carType);

        return carsOfType.stream()
            .filter(car -> isCarAvailableForPeriod(car.getId(), startDateTime, endDateTime))
            .findFirst();
    }

    private boolean isCarAvailableForPeriod(String carId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Reservation> overlappingReservations = reservationRepository
            .findOverlappingReservations(carId, startDateTime, endDateTime);

        return overlappingReservations.isEmpty();
    }

    private double calculateTotalCost(Car car, int durationDays) {
        return car.getDailyRate() * durationDays;
    }

    private ReservationResponse mapToReservationResponse(Reservation reservation, Car car) {
        return ReservationResponse.builder()
            .reservationId(reservation.getId())
            .carId(car.getId())
            .licensePlate(car.getLicensePlate())
            .carType(car.getCarType())
            .customerName(reservation.getCustomerName())
            .customerEmail(reservation.getCustomerEmail())
            .startDateTime(reservation.getStartDateTime())
            .endDateTime(reservation.getEndDateTime())
            .durationDays(reservation.getDurationDays())
            .totalCost(reservation.getTotalCost())
            .status(reservation.getStatus())
            .createdAt(reservation.getCreatedAt())
            .build();
    }
}