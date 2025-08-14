package com.rental.repository;

import com.rental.model.Reservation;
import com.rental.model.ReservationStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ReservationRepositoryImpl implements ReservationRepository {

    private final Map<String, Reservation> reservations = new ConcurrentHashMap<>();

    @Override
    public List<Reservation> findAll() {
        return new ArrayList<>(reservations.values());
    }

    @Override
    public Optional<Reservation> findById(String id) {
        return Optional.ofNullable(reservations.get(id));
    }

    @Override
    public List<Reservation> findByCarId(String carId) {
        return reservations.values().stream()
            .filter(reservation -> Objects.equals(reservation.getCarId(), carId))
            .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByCustomerEmail(String customerEmail) {
        return reservations.values().stream()
            .filter(reservation -> Objects.equals(reservation.getCustomerEmail(), customerEmail))
            .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByStatus(ReservationStatus status) {
        return reservations.values().stream()
            .filter(reservation -> reservation.getStatus() == status)
            .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findActiveReservationsForCar(String carId) {
        return reservations.values().stream()
            .filter(reservation -> Objects.equals(reservation.getCarId(), carId))
            .filter(reservation -> reservation.getStatus() == ReservationStatus.ACTIVE)
            .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findOverlappingReservations(String carId, LocalDateTime start, LocalDateTime end) {
        return reservations.values().stream()
            .filter(reservation -> Objects.equals(reservation.getCarId(), carId))
            .filter(reservation -> reservation.overlapsWithPeriod(start, end))
            .collect(Collectors.toList());
    }

    @Override
    public Reservation save(Reservation reservation) {
        if (reservation.getId() == null) {
            reservation.setId(UUID.randomUUID().toString());
        }
        reservations.put(reservation.getId(), reservation);
        log.debug("Saved reservation: {}", reservation.getId());
        return reservation;
    }

    @Override
    public void deleteById(String id) {
        Reservation removed = reservations.remove(id);
        if (removed != null) {
            log.debug("Deleted reservation: {}", id);
        }
    }
}