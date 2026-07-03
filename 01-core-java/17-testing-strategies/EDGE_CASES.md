# Module 17: Testing Strategies - Edge Cases & Pitfalls

---

## Pitfall 1: Fragile Tests

### ❌ Wrong
Writing tests that depend on exact internal implementation details rather than the external behavior.
```java
@Test
public void testListSize() {
    MyList list = new MyList();
    list.add("item");
    // Testing an internal private field via reflection (bad practice)
    assertEquals(1, getInternalArrayLength(list));
}
```

### ✅ Correct
Test public behavior and outputs.
```java
@Test
public void testListSize() {
    MyList list = new MyList();
    list.add("item");
    assertEquals(1, list.size());
}
```

---

## Pitfall 2: Mocking Everything

### ❌ Wrong
Over-mocking makes tests useless because you are just verifying that Mockito works.
```java
// Mocking simple DTOs or value objects
User mockUser = mock(User.class);
when(mockUser.getName()).thenReturn("John");
```

### ✅ Correct
Use real objects for Domain objects/Value objects. Only mock external dependencies or complex services.
```java
User realUser = new User("John");
```

---

## Pitfall 3: Order-Dependent Tests

### ❌ Wrong
Tests that rely on static state modified by previous tests.
```java
private static int counter = 0;

@Test
public void test1() {
    counter++;
    assertEquals(1, counter);
}

@Test
public void test2() {
    counter++;
    assertEquals(2, counter); // Fails if test2 runs before test1
}
```

### ✅ Correct
Ensure each test is fully isolated. Reset state in `@BeforeEach`.