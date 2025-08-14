package com.rental.repository;

import com.rental.model.Car;
import com.rental.model.CarType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class CarRepositoryImpl implements CarRepository {

    private final Map<String, Car> cars = new ConcurrentHashMap<>();

    @PostConstruct
    public void initializeData() {
        List<Car> initialCars = Arrays.asList(
            Car.builder().licensePlate("ABC123").carType(CarType.SEDAN).brand("Toyota").model("Camry").year(2022).build(),
            Car.builder().licensePlate("DEF456").carType(CarType.SEDAN).brand("Honda").model("Accord").year(2023).build(),
            Car.builder().licensePlate("GHI789").carType(CarType.SEDAN).brand("BMW").model("320i").year(2022).build(),

            Car.builder().licensePlate("JKL012").carType(CarType.SUV).brand("Toyota").model("RAV4").year(2023).build(),
            Car.builder().licensePlate("MNO345").carType(CarType.SUV).brand("Honda").model("CR-V").year(2022).build(),
            Car.builder().licensePlate("PQR678").carType(CarType.SUV).brand("BMW").model("X3").year(2023).build(),

            Car.builder().licensePlate("STU901").carType(CarType.VAN).brand("Ford").model("Transit").year(2022).build(),
            Car.builder().licensePlate("VWX234").carType(CarType.VAN).brand("Mercedes").model("Sprinter").year(2023).build()
        );

        initialCars.forEach(car -> cars.put(car.getId(), car));
        log.info("Initialized {} cars in repository", cars.size());
    }

    @Override
    public List<Car> findAll() {
        return new ArrayList<>(cars.values());
    }

    @Override
    public Optional<Car> findById(String id) {
        return Optional.ofNullable(cars.get(id));
    }

    @Override
    public List<Car> findByCarType(CarType carType) {
        return cars.values().stream()
            .filter(car -> car.getCarType() == carType)
            .collect(Collectors.toList());
    }

    @Override
    public List<Car> findAvailableByCarType(CarType carType) {
        return cars.values().stream()
            .filter(car -> car.getCarType() == carType && car.isAvailable())
            .collect(Collectors.toList());
    }

    @Override
    public Car save(Car car) {
        if (car.getId() == null) {
            car.setId(UUID.randomUUID().toString());
        }
        cars.put(car.getId(), car);
        log.debug("Saved car: {}", car.getId());
        return car;
    }

    @Override
    public void deleteById(String id) {
        Car removed = cars.remove(id);
        if (removed != null) {
            log.debug("Deleted car: {}", id);
        }
    }

    @Override
    public long countByCarType(CarType carType) {
        return cars.values().stream()
            .filter(car -> car.getCarType() == carType)
            .count();
    }

    @Override
    public long countAvailableByCarType(CarType carType) {
        return cars.values().stream()
            .filter(car -> car.getCarType() == carType && car.isAvailable())
            .count();
    }
}