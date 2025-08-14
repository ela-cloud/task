package com.rental.repository;

import com.rental.model.Car;
import com.rental.model.CarType;

import java.util.List;
import java.util.Optional;

public interface CarRepository {

    List<Car> findAll();

    Optional<Car> findById(String id);

    List<Car> findByCarType(CarType carType);

    List<Car> findAvailableByCarType(CarType carType);

    Car save(Car car);

    void deleteById(String id);

    long countByCarType(CarType carType);

    long countAvailableByCarType(CarType carType);
}