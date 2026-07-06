package com.javaacademy.lab31.testing;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IntegrationTest {

    private Path tempDir;
    private Calculator calculator;
    private File logFile;

    @BeforeAll
    void setupEnvironment() throws IOException {
        tempDir = Files.createTempDirectory("integration-test-");
        logFile = tempDir.resolve("test-results.log").toFile();
        calculator = new Calculator();
    }

    @AfterAll
    void cleanup() throws IOException {
        Files.walk(tempDir)
            .sorted(java.util.Comparator.reverseOrder())
            .forEach(path -> {
                try { Files.deleteIfExists(path); }
                catch (IOException ignored) { }
            });
    }

    @BeforeEach
    void logTestStart(TestInfo info) {
        appendLog("START: " + info.getDisplayName());
    }

    @AfterEach
    void logTestEnd(TestInfo info) {
        appendLog("END: " + info.getDisplayName());
    }

    private void appendLog(String message) {
        try (FileWriter fw = new FileWriter(logFile, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(java.time.LocalTime.now() + " " + message);
        } catch (IOException e) {
            fail("Could not write to log file: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Integration: Full calculator workflow")
    void calculatorWorkflow() {
        double a = calculator.add(10, 5);
        double b = calculator.subtract(a, 3);
        double c = calculator.multiply(b, 2);
        double d = calculator.divide(c, 4);
        assertEquals(6.0, d, 1e-9);
    }

    @Test
    @DisplayName("Integration: Log file is created and contains entries")
    void logFileCreated() {
        assertTrue(logFile.exists(), "Log file should exist");
        assertTrue(logFile.length() > 0, "Log file should have content");
    }

    @Test
    @DisplayName("Integration: Multiple operations with state")
    void multipleOperations() {
        assertEquals(Calculator.Operation.ADD, Calculator.Operation.valueOf("ADD"));
        assertEquals(4, calculator.factorial(4));
        assertEquals(120, calculator.factorial(5));
        assertThrows(ArithmeticException.class, () -> calculator.divide(1, 0));
    }

    @Test
    @DisplayName("Integration: File system interaction works")
    void fileSystemInteraction() throws IOException {
        Path dataFile = tempDir.resolve("data.txt");
        Files.writeString(dataFile, "42,73,15");
        String content = Files.readString(dataFile);
        String[] parts = content.split(",");
        int sum = 0;
        for (String part : parts) sum += Integer.parseInt(part.trim());
        assertEquals(130, sum);
    }
}
