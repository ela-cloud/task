package com.rental.service;

import com.rental.dto.ReservationRequest;
import com.rental.dto.ReservationResponse;
import com.rental.model.Car;
import com.rental.model.CarType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CarRentalService {

    /**
     * Creates a new reservation for the specified car type and time period
     * @param request the reservation request details
     * @return the created reservation response
     * @throws com.rental.exception.CarNotAvailableException if no cars are available
     * @throws com.rental.exception.InvalidReservationException if the request is invalid
     */
    ReservationResponse createReservation(ReservationRequest request);

    /**
     * Retrieves a reservation by its ID
     * @param reservationId the reservation ID
     * @return the reservation if found
     */
    Optional<ReservationResponse> getReservation(String reservationId);

    /**
     * Cancels a reservation
     * @param reservationId the reservation ID to cancel
     * @return true if cancelled successfully, false if not found
     */
    boolean cancelReservation(String reservationId);

    /**
     * Lists all reservations for a customer
     * @param customerEmail the customer's email
     * @return list of reservations
     */
    List<ReservationResponse> getReservationsForCustomer(String customerEmail);

    /**
     * Gets all available cars of a specific type for a given time period
     * @param carType the car type
     * @param startDateTime start of the period
     * @param endDateTime end of the period
     * @return list of available cars
     */
    List<Car> getAvailableCars(CarType carType, LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     * Gets all cars of a specific type
     * @param carType the car type
     * @return list of cars
     */
    List<Car> getCarsByType(CarType carType);

    /**
     * Gets availability count for each car type
     * @param startDateTime start of the period
     * @param endDateTime end of the period
     * @return map of car type to available count
     */
    java.util.Map<CarType, Long> getAvailabilityByType(LocalDateTime startDateTime, LocalDateTime endDateTime);
}