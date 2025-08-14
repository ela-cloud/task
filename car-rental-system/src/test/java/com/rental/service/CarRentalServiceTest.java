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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarRentalServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private CarRentalServiceImpl carRentalService;

    private Car testCar;
    private ReservationRequest testRequest;
    private LocalDateTime futureDateTime;

    @BeforeEach
    void setUp() {
        testCar = Car.builder()
            .id("car-1")
            .licensePlate("ABC123")
            .carType(CarType.SEDAN)
            .brand("Toyota")
            .model("Camry")
            .year(2022)
            .available(true)
            .build();

        futureDateTime = LocalDateTime.now().plusDays(1);

        testRequest = ReservationRequest.builder()
            .carType(CarType.SEDAN)
            .customerName("John Doe")
            .customerEmail("john@example.com")
            .startDateTime(futureDateTime)
            .durationDays(3)
            .build();
    }

    @Test
    void createReservation_ShouldSucceed_WhenCarIsAvailable() {
        // Given
        when(carRepository.findByCarType(CarType.SEDAN)).thenReturn(List.of(testCar));
        when(reservationRepository.findOverlappingReservations(eq(testCar.getId()), any(), any()))
            .thenReturn(Collections.emptyList());
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation reservation = invocation.getArgument(0);
            reservation.setId("reservation-1");
            return reservation;
        });

        // When
        ReservationResponse response = carRentalService.createReservation(testRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getReservationId()).isEqualTo("reservation-1");
        assertThat(response.getCarId()).isEqualTo(testCar.getId());
        assertThat(response.getCarType()).isEqualTo(CarType.SEDAN);
        assertThat(response.getCustomerName()).isEqualTo("John Doe");
        assertThat(response.getTotalCost()).isEqualTo(150.0); // 3 days * 50.0 daily rate

        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void createReservation_ShouldThrowException_WhenNoCarAvailable() {
        // Given
        when(carRepository.findByCarType(CarType.SEDAN)).thenReturn(Collections.emptyList());

        // When & Then
        assertThatThrownBy(() -> carRentalService.createReservation(testRequest))
            .isInstanceOf(CarNotAvailableException.class)
            .hasMessageContaining("No Sedan available");
    }

    @Test
    void createReservation_ShouldThrowException_WhenCarIsAlreadyReserved() {
        // Given
        Reservation existingReservation = Reservation.builder()
            .carId(testCar.getId())
            .startDateTime(futureDateTime.minusDays(1))
            .durationDays(5)
            .status(ReservationStatus.ACTIVE)
            .build();

        when(carRepository.findByCarType(CarType.SEDAN)).thenReturn(List.of(testCar));
        when(reservationRepository.findOverlappingReservations(eq(testCar.getId()), any(), any()))
            .thenReturn(List.of(existingReservation));

        // When & Then
        assertThatThrownBy(() -> carRentalService.createReservation(testRequest))
            .isInstanceOf(CarNotAvailableException.class);
    }

    @Test
    void createReservation_ShouldThrowException_WhenStartDateInPast() {
        // Given
        testRequest.setStartDateTime(LocalDateTime.now().minusDays(1));

        // When & Then
        assertThatThrownBy(() -> carRentalService.createReservation(testRequest))
            .isInstanceOf(InvalidReservationException.class)
            .hasMessageContaining("Start date cannot be in the past");
    }

    @Test
    void createReservation_ShouldThrowException_WhenDurationIsZero() {
        // Given
        testRequest.setDurationDays(0);

        // When & Then
        assertThatThrownBy(() -> carRentalService.createReservation(testRequest))
            .isInstanceOf(InvalidReservationException.class)
            .hasMessageContaining("Duration must be positive");
    }

    @Test
    void getReservation_ShouldReturnReservation_WhenExists() {
        // Given
        Reservation reservation = Reservation.builder()
            .id("reservation-1")
            .carId(testCar.getId())
            .customerName("John Doe")
            .customerEmail("john@example.com")
            .startDateTime(futureDateTime)
            .durationDays(3)
            .totalCost(150.0)
            .build();

        when(reservationRepository.findById("reservation-1")).thenReturn(Optional.of(reservation));
        when(carRepository.findById(testCar.getId())).thenReturn(Optional.of(testCar));

        // When
        Optional<ReservationResponse> response = carRentalService.getReservation("reservation-1");

        // Then
        assertThat(response).isPresent();
        assertThat(response.get().getReservationId()).isEqualTo("reservation-1");
        assertThat(response.get().getCustomerName()).isEqualTo("John Doe");
    }

    @Test
    void getReservation_ShouldReturnEmpty_WhenNotExists() {
        // Given
        when(reservationRepository.findById("non-existent")).thenReturn(Optional.empty());

        // When
        Optional<ReservationResponse> response = carRentalService.getReservation("non-existent");

        // Then
        assertThat(response).isEmpty();
    }

    @Test
    void cancelReservation_ShouldSucceed_WhenReservationExists() {
        // Given
        Reservation reservation = Reservation.builder()
            .id("reservation-1")
            .status(ReservationStatus.ACTIVE)
            .build();

        when(reservationRepository.findById("reservation-1")).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        // When
        boolean result = carRentalService.cancelReservation("reservation-1");

        // Then
        assertThat(result).isTrue();
        verify(reservationRepository).save(argThat(r -> r.getStatus() == ReservationStatus.CANCELLED));
    }

    @Test
    void cancelReservation_ShouldReturnFalse_WhenReservationNotExists() {
        // Given
        when(reservationRepository.findById("non-existent")).thenReturn(Optional.empty());

        // When
        boolean result = carRentalService.cancelReservation("non-existent");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void cancelReservation_ShouldThrowException_WhenReservationNotActive() {
        // Given
        Reservation reservation = Reservation.builder()
            .id("reservation-1")
            .status(ReservationStatus.CANCELLED)
            .build();

        when(reservationRepository.findById("reservation-1")).thenReturn(Optional.of(reservation));

        // When & Then
        assertThatThrownBy(() -> carRentalService.cancelReservation("reservation-1"))
            .isInstanceOf(InvalidReservationException.class)
            .hasMessageContaining("Cannot cancel reservation that is not active");
    }

    @Test
    void getAvailableCars_ShouldReturnAvailableCars_ForGivenPeriod() {
        // Given
        Car car1 = Car.builder().id("car-1").carType(CarType.SEDAN).build();
        Car car2 = Car.builder().id("car-2").carType(CarType.SEDAN).build();

        when(carRepository.findByCarType(CarType.SEDAN)).thenReturn(List.of(car1, car2));
        when(reservationRepository.findOverlappingReservations(eq("car-1"), any(), any()))
            .thenReturn(Collections.emptyList());
        when(reservationRepository.findOverlappingReservations(eq("car-2"), any(), any()))
            .thenReturn(List.of(Reservation.builder().build())); // car-2 is reserved

        // When
        List<Car> availableCars = carRentalService.getAvailableCars(
            CarType.SEDAN, futureDateTime, futureDateTime.plusDays(3));

        // Then
        assertThat(availableCars).hasSize(1);
        assertThat(availableCars.get(0).getId()).isEqualTo("car-1");
    }

    @Test
    void getReservationsForCustomer_ShouldReturnCustomerReservations() {
        // Given
        LocalDateTime baseTime = LocalDateTime.now();

        Reservation reservation1 = Reservation.builder()
            .id("res-1")
            .carId("car-1")
            .customerEmail("john@example.com")
            .customerName("John Doe")
            .startDateTime(baseTime.minusDays(1))
            .durationDays(3)
            .totalCost(150.0)
            .createdAt(baseTime.minusDays(1))
            .build();

        Reservation reservation2 = Reservation.builder()
            .id("res-2")
            .carId("car-2")
            .customerEmail("john@example.com")
            .customerName("John Doe")
            .startDateTime(baseTime)
            .durationDays(2)
            .totalCost(100.0)
            .createdAt(baseTime)
            .build();

        when(reservationRepository.findByCustomerEmail("john@example.com"))
            .thenReturn(List.of(reservation1, reservation2));
        when(carRepository.findById("car-1")).thenReturn(Optional.of(testCar));
        when(carRepository.findById("car-2")).thenReturn(Optional.of(testCar));

        // When
        List<ReservationResponse> responses = carRentalService.getReservationsForCustomer("john@example.com");

        // Then
        assertThat(responses).hasSize(2);
        // Should be sorted by creation date descending
        assertThat(responses.get(0).getReservationId()).isEqualTo("res-2");
        assertThat(responses.get(1).getReservationId()).isEqualTo("res-1");
    }

    @Test
    void getAvailabilityByType_ShouldReturnCorrectCounts() {
        // Given
        when(carRepository.findByCarType(CarType.SEDAN)).thenReturn(List.of(testCar));
        when(carRepository.findByCarType(CarType.SUV)).thenReturn(Collections.emptyList());
        when(carRepository.findByCarType(CarType.VAN)).thenReturn(Collections.emptyList());

        when(reservationRepository.findOverlappingReservations(eq(testCar.getId()), any(), any()))
            .thenReturn(Collections.emptyList());

        // When
        Map<CarType, Long> availability = carRentalService.getAvailabilityByType(
            futureDateTime, futureDateTime.plusDays(3));

        // Then
        assertThat(availability).containsEntry(CarType.SEDAN, 1L);
        assertThat(availability).containsEntry(CarType.SUV, 0L);
        assertThat(availability).containsEntry(CarType.VAN, 0L);
    }
}