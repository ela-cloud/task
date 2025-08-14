package com.rental.repository;

import com.rental.model.Reservation;
import com.rental.model.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {

    List<Reservation> findAll();

    Optional<Reservation> findById(String id);

    List<Reservation> findByCarId(String carId);

    List<Reservation> findByCustomerEmail(String customerEmail);

    List<Reservation> findByStatus(ReservationStatus status);

    List<Reservation> findActiveReservationsForCar(String carId);

    List<Reservation> findOverlappingReservations(String carId, LocalDateTime start, LocalDateTime end);

    Reservation save(Reservation reservation);

    void deleteById(String id);
}