package com.javaacademy.lab40.bestpractices;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class BestPracticesTest {

    @Test @DisplayName("Builder creates object with all fields")
    void builderCreatesObject() {
        BuilderExample p = BuilderExample.builder()
            .firstName("John").lastName("Doe").age(30)
            .email("john@test.com").phone("555-0100")
            .build();
        assertEquals("John", p.getFirstName());
        assertEquals("Doe", p.getLastName());
        assertEquals(30, p.getAge());
    }

    @Test @DisplayName("Builder throws on missing required field")
    void builderMissingField() {
        assertThrows(NullPointerException.class,
            () -> BuilderExample.builder().firstName("John").build());
    }

    @Test @DisplayName("Equals and hashCode contract")
    void equalsHashCode() {
        EqualsHashCodeExample p1 = new EqualsHashCodeExample("Alice", 25, "alice@test.com");
        EqualsHashCodeExample p2 = new EqualsHashCodeExample("Alice", 25, "alice@test.com");
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test @DisplayName("Defensive copy protects internal state")
    void defensiveCopy() {
        Date date = new Date();
        List<String> tags = new ArrayList<>(List.of("a", "b"));
        int[] scores = {10, 20, 30};
        DefensiveCopyExample obj = new DefensiveCopyExample("Test", date, tags, scores);
        date.setTime(0);
        tags.add("c");
        scores[0] = 99;
        assertNotEquals(0, obj.getBirthDate().getTime());
        assertEquals(2, obj.getTags().size());
        assertEquals(10, obj.getScores()[0]);
    }

    @Test @DisplayName("Enum singleton is truly singleton")
    void enumSingleton() {
        EnumSingleton s1 = EnumSingleton.getInstance();
        EnumSingleton s2 = EnumSingleton.getInstance();
        assertSame(s1, s2);
    }

    @Test @DisplayName("Enum singleton maintains state")
    void enumSingletonState() {
        EnumSingleton s = EnumSingleton.getInstance();
        s.setConfigValue("custom-config");
        assertEquals("custom-config", EnumSingleton.getInstance().getConfigValue());
    }

    @Test @DisplayName("Try-with-resources closes resources")
    void tryWithResources() throws Exception {
        TryWithResourcesExample ex = new TryWithResourcesExample();
        java.nio.file.Path tmp = java.nio.file.Files.createTempFile("test", ".txt");
        ex.writeToFile(tmp.toString(), "hello");
        String content = ex.readFile(tmp.toString());
        assertEquals("hello", content);
        java.nio.file.Files.deleteIfExists(tmp);
    }

    @Test @DisplayName("Defensive copy isAdult works")
    void defensiveCopyAdult() {
        DefensiveCopyExample adult = DefensiveCopyExample.createFromUserInput("Adult", 2000, 1, 1);
        DefensiveCopyExample minor = DefensiveCopyExample.createFromUserInput("Minor", 2020, 1, 1);
        assertTrue(adult.isAdult());
        assertFalse(minor.isAdult());
    }

    @Test @DisplayName("Equals hashCode asymmetry detection")
    void equalsAsymmetry() {
        EqualsHashCodeExample a = new EqualsHashCodeExample("A", 1, "a@a.com");
        EqualsHashCodeExample b = new EqualsHashCodeExample("A", 1, "a@a.com");
        assertTrue(a.equals(b) && b.equals(a));
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test @DisplayName("Enum singleton thread-safe getInstance")
    void enumThreadSafe() {
        ExecutorService exec = java.util.concurrent.Executors.newFixedThreadPool(10);
        java.util.concurrent.Future<EnumSingleton>[] futures = new java.util.concurrent.Future[10];
        for (int i = 0; i < 10; i++) {
            futures[i] = exec.submit(EnumSingleton::getInstance);
        }
        EnumSingleton expected = EnumSingleton.getInstance();
        for (var f : futures) {
            try { assertSame(expected, f.get()); }
            catch (Exception e) { fail(e); }
        }
        exec.shutdown();
    }
}
