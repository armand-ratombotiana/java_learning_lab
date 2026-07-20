package com.javalab.05;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class MainImplementationTest {
    private MainImplementation service;
    @BeforeEach void setUp() { service = new MainImplementation(); }
    @Test @DisplayName("Should filter adults using AssertJ")
    void testFilterAdults() {
        var people = List.of(
            new MainImplementation.Person("Alice", 25, List.of("reading")),
            new MainImplementation.Person("Bob", 17, List.of("gaming")),
            new MainImplementation.Person("Charlie", 30, List.of("hiking")));
        var adults = service.filterAdults(people);
        assertThat(adults).hasSize(2).extracting(MainImplementation.Person::getName).contains("Alice", "Charlie");
    }
    @Test @DisplayName("Should find oldest person")
    void testFindOldest() {
        var people = List.of(
            new MainImplementation.Person("Alice", 25, List.of()),
            new MainImplementation.Person("Bob", 40, List.of()),
            new MainImplementation.Person("Charlie", 30, List.of()));
        assertThat(service.findOldest(people)).get().extracting(MainImplementation.Person::getName).isEqualTo("Bob");
    }
}
