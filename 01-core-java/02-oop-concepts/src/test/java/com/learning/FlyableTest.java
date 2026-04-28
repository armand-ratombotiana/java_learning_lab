package com.learning;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.*;

/**
 * Test suite for interfaces with Flyable implementations.
 * Tests interface contract and multiple implementations.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
@DisplayName("Interface and Flyable Tests")
class FlyableTest {

    private Flyable bird;
    private Flyable airplane;

    @BeforeEach
    void setUp() {
        bird = new Bird("Eagle");
        airplane = new Airplane("Boeing 747");
    }

    @Test
    @DisplayName("Should create bird implementing Flyable")
    void testBirdCreation() {
        assertThat(bird).isInstanceOf(Flyable.class);
        assertThat(bird).isInstanceOf(Bird.class);
    }

    @Test
    @DisplayName("Should create airplane implementing Flyable")
    void testAirplaneCreation() {
        assertThat(airplane).isInstanceOf(Flyable.class);
        assertThat(airplane).isInstanceOf(Airplane.class);
    }

    @Test
    @DisplayName("Bird should have max altitude")
    void testBirdMaxAltitude() {
        assertThat(bird.getMaxAltitude()).isEqualTo(3000);
    }

    @Test
    @DisplayName("Airplane should have max altitude")
    void testAirplaneMaxAltitude() {
        assertThat(airplane.getMaxAltitude()).isEqualTo(12000);
    }

    @Test
    @DisplayName("Different flyable objects should have different altitudes")
    void testDifferentAltitudes() {
        assertThat(bird.getMaxAltitude()).isNotEqualTo(airplane.getMaxAltitude());
    }

    @Test
    @DisplayName("Bird should have species")
    void testBirdSpecies() {
        Bird b = (Bird) bird;
        assertThat(b.getSpecies()).isEqualTo("Eagle");
    }

    @Test
    @DisplayName("Bird should have wing span")
    void testBirdWingSpan() {
        Bird b = (Bird) bird;
        assertThat(b.getWingSpan()).isEqualTo(100);
    }

    @Test
    @DisplayName("Airplane should have model")
    void testAirplaneModel() {
        Airplane a = (Airplane) airplane;
        assertThat(a.getModel()).isEqualTo("Boeing 747");
    }

    @Test
    @DisplayName("Airplane should have passenger capacity")
    void testAirplaneCapacity() {
        Airplane a = (Airplane) airplane;
        assertThat(a.getPassengerCapacity()).isEqualTo(200);
    }

    @Test
    @DisplayName("Should create bird with custom wing span")
    void testBirdWithCustomWingSpan() {
        Bird customBird = new Bird("Sparrow", 15);
        assertThat(customBird.getWingSpan()).isEqualTo(15);
    }

    @Test
    @DisplayName("Should create airplane with custom capacity")
    void testAirplaneWithCustomCapacity() {
        Airplane customAirplane = new Airplane("Airbus A380", 853);
        assertThat(customAirplane.getPassengerCapacity()).isEqualTo(853);
    }

    @Test
    @DisplayName("Test bird toString representation")
    void testBirdToString() {
        String str = bird.toString();
        assertThat(str)
            .contains("Bird")
            .contains("Eagle");
    }

    @Test
    @DisplayName("Test airplane toString representation")
    void testAirplaneToString() {
        String str = airplane.toString();
        assertThat(str)
            .contains("Airplane")
            .contains("Boeing 747");
    }

    @Test
    @DisplayName("Multiple types can implement Flyable interface")
    void testPolymorphicBehavior() {
        Flyable[] flyables = {
            new Bird("Hawk"),
            new Airplane("Cessna 172"),
            new Bird("Parrot", 80)
        };

        assertThat(flyables).hasSize(3);
        assertThat(flyables[0]).isInstanceOf(Bird.class);
        assertThat(flyables[1]).isInstanceOf(Airplane.class);
        assertThat(flyables[2]).isInstanceOf(Bird.class);
    }
}
