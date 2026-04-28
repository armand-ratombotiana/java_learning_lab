package com.learning;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.*;

/**
 * Test suite for abstraction with Vehicle hierarchy.
 * Tests abstract class functionality and concrete implementations.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
@DisplayName("Abstraction and Vehicle Tests")
class VehicleTest {

    private Vehicle car;
    private Vehicle motorcycle;

    @BeforeEach
    void setUp() {
        car = new Car("Toyota", "Camry", 2023);
        motorcycle = new Motorcycle("Honda", "CBR", 2023);
    }

    @Test
    @DisplayName("Should create car from abstract Vehicle")
    void testCarCreation() {
        assertThat(car).isInstanceOf(Car.class);
        assertThat(car).isInstanceOf(Vehicle.class);
    }

    @Test
    @DisplayName("Should get car brand")
    void testCarBrand() {
        assertThat(car.getBrand()).isEqualTo("Toyota");
    }

    @Test
    @DisplayName("Should get car model")
    void testCarModel() {
        assertThat(car.getModel()).isEqualTo("Camry");
    }

    @Test
    @DisplayName("Should get car year")
    void testCarYear() {
        assertThat(car.getYear()).isEqualTo(2023);
    }

    @Test
    @DisplayName("Should get car max speed")
    void testCarMaxSpeed() {
        assertThat(car.getMaxSpeed()).isEqualTo(200);
    }

    @Test
    @DisplayName("Should create motorcycle from abstract Vehicle")
    void testMotorcycleCreation() {
        assertThat(motorcycle).isInstanceOf(Motorcycle.class);
        assertThat(motorcycle).isInstanceOf(Vehicle.class);
    }

    @Test
    @DisplayName("Should get motorcycle brand")
    void testMotorcycleBrand() {
        assertThat(motorcycle.getBrand()).isEqualTo("Honda");
    }

    @Test
    @DisplayName("Should get motorcycle max speed")
    void testMotorcycleMaxSpeed() {
        assertThat(motorcycle.getMaxSpeed()).isEqualTo(180);
    }

    @Test
    @DisplayName("Car and Motorcycle should have different max speeds")
    void testDifferentMaxSpeeds() {
        assertThat(car.getMaxSpeed()).isNotEqualTo(motorcycle.getMaxSpeed());
    }

    @Test
    @DisplayName("Vehicle toString should contain brand and model")
    void testVehicleToString() {
        String str = car.toString();
        assertThat(str)
            .contains("Car")
            .contains("Toyota")
            .contains("Camry")
            .contains("2023");
    }

    @Test
    @DisplayName("Car should have number of doors")
    void testCarNumberOfDoors() {
        assertThat(((Car) car).getNumberOfDoors()).isEqualTo(4);
    }

    @Test
    @DisplayName("Should be able to set number of doors")
    void testSetNumberOfDoors() {
        ((Car) car).setNumberOfDoors(2);
        assertThat(((Car) car).getNumberOfDoors()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should convert vehicles to Vehicle type")
    void testAbstractTypeUsage() {
        Vehicle[] vehicles = {
            new Car("BMW", "X5", 2023),
            new Motorcycle("Yamaha", "YZF", 2023)
        };

        assertThat(vehicles).hasSize(2);
        assertThat(vehicles[0]).isInstanceOf(Car.class);
        assertThat(vehicles[1]).isInstanceOf(Motorcycle.class);
    }
}
