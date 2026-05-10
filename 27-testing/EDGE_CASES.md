# Testing Edge Cases and Error Scenarios

## Null Handling

### Null Pointer Exceptions

```java
class NullHandlingTest {
    
    @Test
    @DisplayName("Should handle null input gracefully")
    void shouldHandleNullInput() {
        Validator validator = new Validator();
        
        assertThrows(NullPointerException.class, 
            () -> validator.validate(null));
    }
    
    @Test
    @DisplayName("Should handle null in collections")
    void shouldHandleNullInCollections() {
        List<String> list = Arrays.asList("a", null, "b");
        
        assertDoesNotThrow(() -> {
            list.stream()
                .filter(Objects::nonNull)
                .count();
        });
    }
    
    @Test
    @DisplayName("Should handle null return from mock")
    void shouldHandleNullFromMock() {
        UserService mockService = mock(UserService.class);
        when(mockService.findById("999")).thenReturn(null);
        
        User result = mockService.findById("999");
        
        assertNull(result);
    }
}
```

### Optional Handling

```java
class OptionalHandlingTest {
    
    @Test
    @DisplayName("Should handle empty Optional")
    void shouldHandleEmptyOptional() {
        Optional<User> user = Optional.empty();
        
        assertTrue(user.isEmpty());
        assertFalse(user.isPresent());
    }
    
    @Test
    @DisplayName("Should use Optional.orElse")
    void shouldUseOrElse() {
        Optional<String> name = Optional.empty();
        String result = name.orElse("default");
        
        assertEquals("default", result);
    }
    
    @Test
    @DisplayName("Should use Optional.orElseGet")
    void shouldUseOrElseGet() {
        Optional<String> name = Optional.empty();
        
        String result = name.orElseGet(() -> {
            // Expensive computation
            return "computed";
        });
        
        assertEquals("computed", result);
    }
    
    @Test
    @DisplayName("Should use Optional.orElseThrow")
    void shouldUseOrElseThrow() {
        Optional<String> name = Optional.empty();
        
        assertThrows(ResourceNotFoundException.class,
            () -> name.orElseThrow(ResourceNotFoundException::new));
    }
}
```

## Boundary Conditions

### Numeric Overflow/Underflow

```java
class NumericBoundaryTest {
    
    @Test
    @DisplayName("Should handle Integer.MAX_VALUE")
    void shouldHandleMaxInteger() {
        int max = Integer.MAX_VALUE;
        
        assertThrows(ArithmeticException.class, 
            () -> Math.addExact(max, 1));
    }
    
    @Test
    @DisplayName("Should handle Integer.MIN_VALUE")
    void shouldHandleMinInteger() {
        int min = Integer.MIN_VALUE;
        
        assertThrows(ArithmeticException.class, 
            () -> Math.subtractExact(min, 1));
    }
    
    @Test
    @DisplayName("Should handle Long.MAX_VALUE")
    void shouldHandleMaxLong() {
        long max = Long.MAX_VALUE;
        
        assertThrows(ArithmeticException.class, 
            () -> Math.incrementExact(max));
    }
    
    @Test
    @DisplayName("Should handle floating point precision")
    void shouldHandleFloatingPoint() {
        double result = 0.1 + 0.2;
        
        assertEquals(0.3, result, 0.0001);
    }
    
    @Test
    @DisplayName("Should handle BigDecimal comparison")
    void shouldHandleBigDecimalComparison() {
        BigDecimal a = new BigDecimal("1.00");
        BigDecimal b = new BigDecimal("1.0");
        
        assertEquals(0, a.compareTo(b));
        assertNotEquals(a, b); // Different scale
    }
}
```

### Collection Boundaries

```java
class CollectionBoundaryTest {
    
    @Test
    @DisplayName("Should handle empty list")
    void shouldHandleEmptyList() {
        List<String> list = Collections.emptyList();
        
        assertTrue(list.isEmpty());
        assertThrows(IndexOutOfBoundsException.class,
            () -> list.get(0));
    }
    
    @Test
    @DisplayName("Should handle single element list")
    void shouldHandleSingleElementList() {
        List<String> list = List.of("single");
        
        assertEquals(1, list.size());
        assertEquals("single", list.get(0));
    }
    
    @Test
    @DisplayName("Should handle large collections")
    void shouldHandleLargeCollections() {
        List<Integer> large = IntStream.range(0, 1_000_000)
            .boxed()
            .collect(Collectors.toList());
        
        assertEquals(1_000_000, large.size());
    }
    
    @Test
    @DisplayName("Should handle negative index")
    void shouldHandleNegativeIndex() {
        List<String> list = List.of("a", "b");
        
        assertThrows(IndexOutOfBoundsException.class,
            () -> list.get(-1));
    }
    
    @Test
    @DisplayName("Should handle index beyond size")
    void shouldHandleIndexBeyondSize() {
        List<String> list = List.of("a", "b");
        
        assertThrows(IndexOutOfBoundsException.class,
            () -> list.get(10));
    }
}
```

## Concurrency Edge Cases

### Race Conditions

```java
class RaceConditionTest {
    
    @Test
    @DisplayName("Should detect race condition")
    void shouldDetectRaceCondition() throws Exception {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        int threadCount = 100;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    startLatch.await();
                    map.merge("counter", 1, Integer::sum);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            }).start();
        }
        
        startLatch.countDown();
        endLatch.await(10, TimeUnit.SECONDS);
        
        // Result may vary due to race condition
        System.out.println("Final count: " + map.get("counter"));
    }
    
    @Test
    @DisplayName("Should handle concurrent reads and writes")
    void shouldHandleConcurrentReadsWrites() throws Exception {
        AtomicReference<String> ref = new AtomicReference<>("initial");
        CountDownLatch latch = new CountDownLatch(2);
        
        Thread writer = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                ref.set("value-" + i);
            }
            latch.countDown();
        });
        
        Thread reader = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                String value = ref.get();
                assertNotNull(value);
            }
            latch.countDown();
        });
        
        writer.start();
        reader.start();
        latch.await(10, TimeUnit.SECONDS);
        
        assertNotNull(ref.get());
    }
}
```

### Deadlock Scenarios

```java
class DeadlockTest {
    
    @Test
    @DisplayName("Should detect potential deadlock")
    void shouldDetectPotentialDeadlock() throws InterruptedException {
        Object lock1 = new Object();
        Object lock2 = new Object();
        
        Thread t1 = new Thread(() -> {
            synchronized (lock1) {
                try {
                    Thread.sleep(100);
                    synchronized (lock2) {
                        System.out.println("Thread 1 acquired both locks");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        
        Thread t2 = new Thread(() -> {
            synchronized (lock2) {
                try {
                    Thread.sleep(100);
                    synchronized (lock1) {
                        System.out.println("Thread 2 acquired both locks");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        
        t1.start();
        t2.start();
        
        t1.join(5000);
        t2.join(5000);
        
        assertFalse(t1.isAlive() || t2.isAlive(), 
            "Potential deadlock detected");
    }
}
```

## Error Handling

### Exception Testing

```java
class ExceptionHandlingTest {
    
    @Test
    @DisplayName("Should catch specific exception")
    void shouldCatchSpecificException() {
        assertThrows(IllegalArgumentException.class, 
            () -> {
                throw new IllegalArgumentException("Invalid input");
            });
    }
    
    @Test
    @DisplayName("Should verify exception message")
    void shouldVerifyExceptionMessage() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> { throw new IllegalArgumentException("Invalid: test"); }
        );
        
        assertTrue(ex.getMessage().contains("Invalid"));
    }
    
    @Test
    @DisplayName("Should handle nested exceptions")
    void shouldHandleNestedExceptions() {
        Throwable rootCause = new RuntimeException("Root cause");
        Exception intermediate = new Exception("Intermediate", rootCause);
        RuntimeException top = new RuntimeException("Top level", intermediate);
        
        assertEquals("Root cause", top.getCause().getCause().getMessage());
    }
    
    @Test
    @DisplayName("Should handle exception chaining")
    void shouldHandleExceptionChaining() {
        try {
            throw new ServiceException("Service failed", 
                new IOException("Network error"));
        } catch (ServiceException e) {
            assertTrue(e.getCause() instanceof IOException);
        }
    }
}
```

### Error Recovery

```java
class ErrorRecoveryTest {
    
    @Test
    @DisplayName("Should recover from transient failure")
    void shouldRecoverFromTransientFailure() {
        RetryableService service = new RetryableService(3);
        
        String result = service.executeWithRetry(() -> "success");
        
        assertEquals("success", result);
    }
    
    @Test
    @DisplayName("Should handle multiple retries before failure")
    void shouldHandleMultipleRetries() {
        AtomicInteger attempt = new AtomicInteger(0);
        
        assertThrows(RuntimeException.class, () -> {
            if (attempt.incrementAndGet() < 3) {
                throw new TransientException("Temporary failure");
            }
            throw new PermanentException("Failed after retries");
        });
        
        assertEquals(3, attempt.get());
    }
}
```

## Time-Based Edge Cases

```java
class TimeBasedTest {
    
    @Test
    @DisplayName("Should handle timezone differences")
    void shouldHandleTimezoneDifferences() {
        ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime pst = utc.withZoneSameInstant(ZoneId.of("America/Los_Angeles"));
        
        assertNotEquals(utc.getHour(), pst.getHour());
    }
    
    @Test
    @DisplayName("Should handle daylight saving time")
    void shouldHandleDaylightSavingTime() {
        LocalDate march = LocalDate.of(2024, 3, 10);
        LocalDate november = LocalDate.of(2024, 11, 3);
        
        ZoneId america = ZoneId.of("America/New_York");
        
        ZonedDateTime marchTime = march.atStartOfDay(america);
        ZonedDateTime novemberTime = november.atStartOfDay(america);
        
        assertNotEquals(marchTime.getOffset(), novemberTime.getOffset());
    }
    
    @Test
    @DisplayName("Should handle leap year")
    void shouldHandleLeapYear() {
        LocalDate feb29_2024 = LocalDate.of(2024, 2, 29);
        LocalDate feb28_2024 = LocalDate.of(2024, 2, 28);
        
        assertTrue(Year.of(2024).isLeap());
        assertEquals(29, feb29_2024.getDayOfMonth());
    }
    
    @Test
    @DisplayName("Should handle epoch time boundaries")
    void shouldHandleEpochBoundaries() {
        Instant epoch = Instant.EPOCH;
        Instant max = Instant.MAX;
        Instant min = Instant.MIN;
        
        assertTrue(epoch.isBefore(max));
        assertTrue(epoch.isAfter(min));
    }
}
```

## String Edge Cases

```java
class StringEdgeCasesTest {
    
    @Test
    @DisplayName("Should handle empty string")
    void shouldHandleEmptyString() {
        String empty = "";
        
        assertTrue(empty.isEmpty());
        assertEquals(0, empty.length());
    }
    
    @Test
    @DisplayName("Should handle whitespace strings")
    void shouldHandleWhitespaceStrings() {
        String whitespace = "   ";
        
        assertFalse(whitespace.isEmpty());
        assertTrue(whitespace.isBlank());
    }
    
    @Test
    @DisplayName("Should handle Unicode characters")
    void shouldHandleUnicode() {
        String emoji = "👨‍👩‍👧‍👦";
        String chinese = "中文";
        
        assertEquals(1, emoji.length());
        assertEquals(2, chinese.length());
    }
    
    @Test
    @DisplayName("Should handle very long strings")
    void shouldHandleLongStrings() {
        String longString = "x".repeat(1_000_000);
        
        assertEquals(1_000_000, longString.length());
    }
    
    @Test
    @DisplayName("Should handle null bytes in strings")
    void shouldHandleNullBytes() {
        String withNull = "test\u0000value";
        
        assertTrue(withNull.contains("\u0000"));
        assertEquals(9, withNull.length());
    }
}
```

## Network and I/O Edge Cases

```java
class NetworkEdgeCasesTest {
    
    @Test
    @DisplayName("Should handle connection timeout")
    void shouldHandleConnectionTimeout() {
        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(100))
            .build();
        
        assertThrows(HttpTimeoutException.class, () -> {
            client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://192.0.2.1/"))
                .GET()
                .build(), HttpResponse.BodyHandlers.ofString());
        });
    }
    
    @Test
    @DisplayName("Should handle malformed URLs")
    void shouldHandleMalformedUrls() {
        assertThrows(MalformedURLException.class, () -> 
            new URL("htp://invalid-schema"));
    }
    
    @Test
    @DisplayName("Should handle file encoding")
    void shouldHandleFileEncoding() throws IOException {
        String content = "Héllo Wörld";
        Path tempFile = Files.createTempFile("test", ".txt");
        
        Files.writeString(tempFile, content, StandardCharsets.UTF_8);
        String read = Files.readString(tempFile, StandardCharsets.UTF_8);
        
        assertEquals(content, read);
        
        Files.deleteIfExists(tempFile);
    }
}
```

## Best Practices for Edge Cases

1. **Think Boundary**: Test min, max, zero, empty values
2. **Think Null**: Test null, empty collections, empty strings
3. **Think Invalid**: Test invalid inputs, malformed data
4. **Think Concurrent**: Test race conditions, thread safety
5. **Think Time**: Test time zones, leap years, timeouts
6. **Think Error**: Test exception paths, error recovery
7. **Document Edge Cases**: Add comments explaining edge case coverage