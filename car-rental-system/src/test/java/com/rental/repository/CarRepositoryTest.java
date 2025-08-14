package com.rental.repository;

import com.rental.model.Car;
import com.rental.model.CarType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class CarRepositoryTest {

    private CarRepositoryImpl carRepository;

    @BeforeEach
    void setUp() {
        carRepository = new CarRepositoryImpl();
        // Clear any existing data and don't initialize default data for tests
    }

    @Test
    void save_ShouldPersistCar() {
        // Given
        Car car = Car.builder()
            .licensePlate("TEST123")
            .carType(CarType.SEDAN)
            .brand("Toyota")
            .model("Camry")
            .year(2022)
            .build();

        // When
        Car savedCar = carRepository.save(car);

        // Then
        assertThat(savedCar.getId()).isNotNull();
        assertThat(savedCar.getLicensePlate()).isEqualTo("TEST123");
        assertThat(savedCar.getCarType()).isEqualTo(CarType.SEDAN);
    }

    @Test
    void findById_ShouldReturnCar_WhenExists() {
        // Given
        Car car = Car.builder()
            .licensePlate("TEST123")
            .carType(CarType.SEDAN)
            .brand("Toyota")
            .model("Camry")
            .year(2022)
            .build();
        Car savedCar = carRepository.save(car);

        // When
        Optional<Car> foundCar = carRepository.findById(savedCar.getId());

        // Then
        assertThat(foundCar).isPresent();
        assertThat(foundCar.get().getId()).isEqualTo(savedCar.getId());
        assertThat(foundCar.get().getLicensePlate()).isEqualTo("TEST123");
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        // When
        Optional<Car> foundCar = carRepository.findById("non-existent-id");

        // Then
        assertThat(foundCar).isEmpty();
    }

    @Test
    void findByCarType_ShouldReturnCarsOfSpecificType() {
        // Given
        Car sedan1 = Car.builder()
            .licensePlate("SEDAN1")
            .carType(CarType.SEDAN)
            .brand("Toyota")
            .model("Camry")
            .year(2022)
            .build();

        Car sedan2 = Car.builder()
            .licensePlate("SEDAN2")
            .carType(CarType.SEDAN)
            .brand("Honda")
            .model("Accord")
            .year(2023)
            .build();

        Car suv = Car.builder()
            .licensePlate("SUV1")
            .carType(CarType.SUV)
            .brand("Toyota")
            .model("RAV4")
            .year(2022)
            .build();

        carRepository.save(sedan1);
        carRepository.save(sedan2);
        carRepository.save(suv);

        // When
        List<Car> sedans = carRepository.findByCarType(CarType.SEDAN);
        List<Car> suvs = carRepository.findByCarType(CarType.SUV);
        List<Car> vans = carRepository.findByCarType(CarType.VAN);

        // Then
        assertThat(sedans).hasSize(2);
        assertThat(sedans).allMatch(car -> car.getCarType() == CarType.SEDAN);

        assertThat(suvs).hasSize(1);
        assertThat(suvs.get(0).getCarType()).isEqualTo(CarType.SUV);

        assertThat(vans).isEmpty();
    }

    @Test
    void findAvailableByCarType_ShouldReturnOnlyAvailableCars() {
        // Given
        Car availableCar = Car.builder()
            .licensePlate("AVAILABLE")
            .carType(CarType.SEDAN)
            .brand("Toyota")
            .model("Camry")
            .year(2022)
            .available(true)
            .build();

        Car unavailableCar = Car.builder()
            .licensePlate("UNAVAILABLE")
            .carType(CarType.SEDAN)
            .brand("Honda")
            .model("Accord")
            .year(2023)
            .available(false)
            .build();

        carRepository.save(availableCar);
        carRepository.save(unavailableCar);

        // When
        List<Car> availableSedans = carRepository.findAvailableByCarType(CarType.SEDAN);

        // Then
        assertThat(availableSedans).hasSize(1);
        assertThat(availableSedans.get(0).getLicensePlate()).isEqualTo("AVAILABLE");
        assertThat(availableSedans.get(0).isAvailable()).isTrue();
    }

    @Test
    void deleteById_ShouldRemoveCar() {
        // Given
        Car car = Car.builder()
            .licensePlate("TO_DELETE")
            .carType(CarType.SEDAN)
            .brand("Toyota")
            .model("Camry")
            .year(2022)
            .build();
        Car savedCar = carRepository.save(car);

        // Verify car exists
        assertThat(carRepository.findById(savedCar.getId())).isPresent();

        // When
        carRepository.deleteById(savedCar.getId());

        // Then
        assertThat(carRepository.findById(savedCar.getId())).isEmpty();
    }

    @Test
    void countByCarType_ShouldReturnCorrectCount() {
        // Given
        carRepository.save(Car.builder().carType(CarType.SEDAN).licensePlate("S1").build());
        carRepository.save(Car.builder().carType(CarType.SEDAN).licensePlate("S2").build());
        carRepository.save(Car.builder().carType(CarType.SUV).licensePlate("U1").build());

        // When
        long sedanCount = carRepository.countByCarType(CarType.SEDAN);
        long suvCount = carRepository.countByCarType(CarType.SUV);
        long vanCount = carRepository.countByCarType(CarType.VAN);

        // Then
        assertThat(sedanCount).isEqualTo(2);
        assertThat(suvCount).isEqualTo(1);
        assertThat(vanCount).isEqualTo(0);
    }

    @Test
    void countAvailableByCarType_ShouldReturnCorrectCount() {
        // Given
        carRepository.save(Car.builder()
            .carType(CarType.SEDAN)
            .licensePlate("S1")
            .available(true)
            .build());
        carRepository.save(Car.builder()
            .carType(CarType.SEDAN)
            .licensePlate("S2")
            .available(false)
            .build());
        carRepository.save(Car.builder()
            .carType(CarType.SUV)
            .licensePlate("U1")
            .available(true)
            .build());

        // When
        long availableSedanCount = carRepository.countAvailableByCarType(CarType.SEDAN);
        long availableSuvCount = carRepository.countAvailableByCarType(CarType.SUV);

        // Then
        assertThat(availableSedanCount).isEqualTo(1);
        assertThat(availableSuvCount).isEqualTo(1);
    }

    @Test
    void findAll_ShouldReturnAllCars() {
        // Given
        carRepository.save(Car.builder().carType(CarType.SEDAN).licensePlate("S1").build());
        carRepository.save(Car.builder().carType(CarType.SUV).licensePlate("U1").build());
        carRepository.save(Car.builder().carType(CarType.VAN).licensePlate("V1").build());

        // When
        List<Car> allCars = carRepository.findAll();

        // Then
        assertThat(allCars).hasSize(3);
        assertThat(allCars.stream().map(Car::getLicensePlate))
            .containsExactlyInAnyOrder("S1", "U1", "V1");
    }
}