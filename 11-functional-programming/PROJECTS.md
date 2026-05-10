# Functional Programming Module - PROJECTS.md

---

# Mini-Project: Functional Utility Library

## Project Overview

**Duration**: 3-4 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Function Composition, Higher-Order Functions, Currying, Partial Application, Monads, Option/Either Types

This project implements a functional utility library demonstrating core functional programming concepts in Java.

---

## Project Structure

```
11-functional-programming/src/main/java/com/learning/
├── Main.java
├── function/
│   ├── FunctionUtils.java
│   ├── Tuple.java
│   └── Pair.java
├── functional/
│   ├── Option.java
│   ├── Either.java
│   └── Try.java
├── HOF/
│   └── HigherOrderFunctions.java
├── currying/
│   └── CurryingUtils.java
└── monad/
    └── MonadExamples.java
```

---

## Step 1: Functional Interfaces Helper

```java
// function/FunctionUtils.java
package com.learning.function;

import java.util.function.*;

public class FunctionUtils {
    
    public static <T, R> Function<T, R> compose(
            Function<T, R> f, Function<T, R> g) {
        return t -> {
            f.apply(t);
            return g.apply(t);
        };
    }
    
    public static <T, U, R> Function<T, R> andThen(
            Function<T, U> f, Function<U, R> g) {
        return f.andThen(g);
    }
    
    public static <T> Function<T, T> identity() {
        return t -> t;
    }
    
    public static <T> Supplier<T> memoize(Supplier<T> supplier) {
        T[] cache = (T[]) new Object[1];
        boolean[] computed = {false};
        
        return () -> {
            if (!computed[0]) {
                computed[0] = true;
                cache[0] = supplier.get();
            }
            return cache[0];
        };
    }
    
    public static <T, U> Function<T, U> memoizeFunction(
            Function<T, U> function) {
        return java.util.Collections.synchronizedMap(
            new java.util.HashMap<>())::computeIfAbsent;
    }
    
    public static <T, U, R> Function<T, Function<U, R>> curry(
            BiFunction<T, U, R> biFunction) {
        return t -> u -> biFunction.apply(t, u);
    }
    
    public static <T, U, R> BiFunction<T, U, R> uncurry(
            Function<T, Function<U, R>> curried) {
        return (t, u) -> curried.apply(t).apply(u);
    }
    
    public static <T, U, R> Function<T, R> partial(
            BiFunction<T, U, R> f, U value) {
        return t -> f.apply(t, value);
    }
    
    public static <T, U, R> Function<U, R> partialLeft(
            BiFunction<T, U, R> f, T value) {
        return u -> f.apply(value, u);
    }
}
```

---

## Step 2: Tuple Classes

```java
// function/Tuple.java
package com.learning.function;

import java.util.Objects;

public class Tuple2<T1, T2> {
    private final T1 _1;
    private final T2 _2;
    
    public Tuple2(T1 _1, T2 _2) {
        this._1 = _1;
        this._2 = _2;
    }
    
    public T1 _1() { return _1; }
    public T2 _2() { return _2; }
    
    public <U1, U2> Tuple2<U1, U2> map(
            Function<T1, U1> f1, Function<T2, U2> f2) {
        return new Tuple2<>(f1.apply(_1), f2.apply(_2));
    }
    
    public <U1> Tuple2<U1, T2> map1(Function<T1, U1> f) {
        return new Tuple2<>(f.apply(_1), _2);
    }
    
    public <U2> Tuple2<T1, U2> map2(Function<T2, U2> f) {
        return new Tuple2<>(_1, f.apply(_2));
    }
    
    public <R> R apply(BiFunction<T1, T2, R> f) {
        return f.apply(_1, _2);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple2)) return false;
        Tuple2<?, ?> that = (Tuple2<?, ?>) o;
        return Objects.equals(_1, that._1) && Objects.equals(_2, that._2);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(_1, _2);
    }
    
    @Override
    public String toString() {
        return "(" + _1 + ", " + _2 + ")";
    }
}

public class Tuple3<T1, T2, T3> {
    private final T1 _1;
    private final T2 _2;
    private final T3 _3;
    
    public Tuple3(T1 _1, T2 _2, T3 _3) {
        this._1 = _1;
        this._2 = _2;
        this._3 = _3;
    }
    
    public T1 _1() { return _1; }
    public T2 _2() { return _2; }
    public T3 _3() { return _3; }
    
    public <R> R apply(Function3<T1, T2, T3, R> f) {
        return f.apply(_1, _2, _3);
    }
    
    @Override
    public String toString() {
        return "(" + _1 + ", " + _2 + ", " + _3 + ")";
    }
}

@FunctionalInterface
public interface Function3<T1, T2, T3, R> {
    R apply(T1 t1, T2 t2, T3 t3);
}
```

---

## Step 3: Option/Maybe Monad

```java
// functional/Option.java
package com.learning.functional;

import java.util.function.*;

public abstract class Option<T> {
    private Option() {}
    
    public static <T> Option<T> of(T value) {
        return value != null ? new Some<>(value) : new None<>();
    }
    
    public static <T> Option<T> empty() {
        return new None<>();
    }
    
    public abstract boolean isPresent();
    public abstract T get();
    public abstract T orElse(T defaultValue);
    public abstract <R> Option<R> map(Function<T, R> f);
    public abstract <R> Option<R> flatMap(Function<T, Option<R>> f);
    public abstract void ifPresent(Consumer<T> consumer);
    public abstract Option<T> filter(Predicate<T> p);
    public abstract <R> R fold(Function<None<T>, R> ifEmpty, 
                         Function<T, R> ifPresent);
    
    public static final class Some<T> extends Option<T> {
        private final T value;
        
        public Some(T value) {
            this.value = value;
        }
        
        @Override
        public boolean isPresent() { return true; }
        
        @Override
        public T get() { return value; }
        
        @Override
        public T orElse(T defaultValue) { return value; }
        
        @Override
        public <R> Option<R> map(Function<T, R> f) {
            return new Some<>(f.apply(value));
        }
        
        @Override
        public <R> Option<R> flatMap(Function<T, Option<R>> f) {
            return f.apply(value);
        }
        
        @Override
        public void ifPresent(Consumer<T> consumer) {
            consumer.accept(value);
        }
        
        @Override
        public Option<T> filter(Predicate<T> p) {
            return p.test(value) ? this : new None<>();
        }
        
        @Override
        public <R> R fold(Function<None<T>, R> ifEmpty, 
                        Function<T, R> ifPresent) {
            return ifPresent.apply(value);
        }
        
        @Override
        public String toString() { return "Some(" + value + ")"; }
    }
    
    public static final class None<T> extends Option<T> {
        private None() {}
        
        @Override
        public boolean isPresent() { return false; }
        
        @Override
        public T get() { 
            throw new IllegalStateException("No value present"); 
        }
        
        @Override
        public T orElse(T defaultValue) { return defaultValue; }
        
        @Override
        public <R> Option<R> map(Function<T, R> f) {
            return new None<>();
        }
        
        @Override
        public <R> Option<R> flatMap(Function<T, Option<R>> f) {
            return new None<>();
        }
        
        @Override
        public void ifPresent(Consumer<T> consumer) {}
        
        @Override
        public Option<T> filter(Predicate<T> p) {
            return this;
        }
        
        @Override
        public <R> R fold(Function<None<T>, R> ifEmpty, 
                        Function<T, R> ifPresent) {
            return ifEmpty.apply((None<T>) this);
        }
        
        @Override
        public String toString() { return "None"; }
    }
}
```

---

## Step 4: Either Type (Sum Type)

```java
// functional/Either.java
package com.learning.functional;

import java.util.function.*;

public abstract class Either<L, R> {
    private Either() {}
    
    public abstract boolean isLeft();
    public abstract boolean isRight();
    public abstract L getLeft();
    public abstract R getRight();
    public abstract <R2> Either<L, R2> map(Function<R, R2> f);
    public abstract <L2> Either<L2, R> mapLeft(Function<L, L2> f);
    public abstract <R2> Either<L, R2> flatMap(Function<R, Either<L, R2>> f);
    public abstract <L2> Either<L2, R> flatMapLeft(Function<L, Either<L2, R>> f);
    public abstract <R2> R2 fold(Function<L, R2> f, Function<R, R2> g);
    
    public static <L, R> Either<L, R> left(L value) {
        return new Left<>(value);
    }
    
    public static <L, R> Either<L, R> right(R value) {
        return new Right<>(value);
    }
    
    public static <L, R> Either<L, R> ofLeft(Optional<L> opt) {
        return opt.<Either<L, R>>map(Either::left)
            .orElseGet(() -> new Right<>(null));
    }
    
    public static <L, R> Either<L, R> ofRight(Optional<R> opt) {
        return opt.<Either<L, R>>map(Either::right)
            .orElseGet(() -> new Left<>(null));
    }
    
    public static final class Left<L, R> extends Either<L, R> {
        private final L value;
        
        public Left(L value) {
            this.value = value;
        }
        
        @Override
        public boolean isLeft() { return true; }
        
        @Override
        public boolean isRight() { return false; }
        
        @Override
        public L getLeft() { return value; }
        
        @Override
        public R getRight() { 
            throw new IllegalStateException("Called getRight on Left"); 
        }
        
        @Override
        public <R2> Either<L, R2> map(Function<R, R2> f) {
            return new Left<>(value);
        }
        
        @Override
        public <L2> Either<L2, R> mapLeft(Function<L, L2> f) {
            return new Left<>(f.apply(value));
        }
        
        @Override
        public <R2> Either<L, R2> flatMap(Function<R, Either<L, R2>> f) {
            return new Left<>(value);
        }
        
        @Override
        public <L2> Either<L2, R> flatMapLeft(Function<L, Either<L2, R>> f) {
            return f.apply(value);
        }
        
        @Override
        public <R2> R2 fold(Function<L, R2> f, Function<R, R2> g) {
            return f.apply(value);
        }
        
        @Override
        public String toString() { return "Left(" + value + ")"; }
    }
    
    public static final class Right<L, R> extends Either<L, R> {
        private final R value;
        
        public Right(R value) {
            this.value = value;
        }
        
        @Override
        public boolean isLeft() { return false; }
        
        @Override
        public boolean isRight() { return true; }
        
        @Override
        public L getLeft() { 
            throw new IllegalStateException("Called getLeft on Right"); 
        }
        
        @Override
        public R getRight() { return value; }
        
        @Override
        public <R2> Either<L, R2> map(Function<R, R2> f) {
            return new Right<>(f.apply(value));
        }
        
        @Override
        public <L2> Either<L2, R> mapLeft(Function<L, L2> f) {
            return new Right<>(value);
        }
        
        @Override
        public <R2> Either<L, R2> flatMap(Function<R, Either<L, R2>> f) {
            return f.apply(value);
        }
        
        @Override
        public <L2> Either<L2, R> flatMapLeft(Function<L, Either<L2, R>> f) {
            return new Right<>(value);
        }
        
        @Override
        public <R2> R2 fold(Function<L, R2> f, Function<R, R2> g) {
            return g.apply(value);
        }
        
        @Override
        public String toString() { return "Right(" + value + ")"; }
    }
}
```

---

## Step 5: Try/Result Type

```java
// functional/Try.java
package com.learning.functional;

import java.util.function.*;

public abstract class Try<T> {
    private Try() {}
    
    public static <T> Try<T> of(Callable<T> callable) {
        try {
            return new Success<>(callable.call());
        } catch (Exception e) {
            return new Failure<>(e);
        }
    }
    
    public static <T> Try<T> ofRuntime(Supplier<T> supplier) {
        try {
            return new Success<>(supplier.get());
        } catch (Exception e) {
            return new Failure<>(e);
        }
    }
    
    public abstract boolean isSuccess();
    public abstract boolean isFailure();
    public abstract T get();
    public abstract Exception getException();
    public abstract <R> Try<R> map(Function<T, R> f);
    public abstract <R> Try<R> flatMap(Function<T, Try<R>> f);
    public abstract Try<T> recover(Function<Exception, T> f);
    public abstract Try<T> recoverWith(Function<Exception, Try<T>> f);
    public abstract void onFailure(Consumer<Exception> handler);
    public abstract <R> R fold(Function<Exception, R> f, Function<T, R> g);
    
    public static final class Success<T> extends Try<T> {
        private final T value;
        
        public Success(T value) {
            this.value = value;
        }
        
        @Override
        public boolean isSuccess() { return true; }
        
        @Override
        public boolean isFailure() { return false; }
        
        @Override
        public T get() { return value; }
        
        @Override
        public Exception getException() { 
            throw new IllegalStateException("No exception"); 
        }
        
        @Override
        public <R> Try<R> map(Function<T, R> f) {
            return new Success<>(f.apply(value));
        }
        
        @Override
        public <R> Try<R> flatMap(Function<T, Try<R>> f) {
            return f.apply(value);
        }
        
        @Override
        public Try<T> recover(Function<Exception, T> f) {
            return this;
        }
        
        @Override
        public Try<T> recoverWith(Function<Exception, Try<T>> f) {
            return this;
        }
        
        @Override
        public void onFailure(Consumer<Exception> handler) {}
        
        @Override
        public <R> R fold(Function<Exception, R> f, Function<T, R> g) {
            return g.apply(value);
        }
        
        @Override
        public String toString() { return "Success(" + value + ")"; }
    }
    
    public static final class Failure<T> extends Try<T> {
        private final Exception exception;
        
        public Failure(Exception exception) {
            this.exception = exception;
        }
        
        @Override
        public boolean isSuccess() { return false; }
        
        @Override
        public boolean isFailure() { return true; }
        
        @Override
        public T get() { 
            throw new IllegalStateException(exception); 
        }
        
        @Override
        public Exception getException() { return exception; }
        
        @Override
        public <R> Try<R> map(Function<T, R> f) {
            return new Failure<>(exception);
        }
        
        @Override
        public <R> Try<R> flatMap(Function<T, Try<R>> f) {
            return new Failure<>(exception);
        }
        
        @Override
        public Try<T> recover(Function<Exception, T> f) {
            return new Success<>(f.apply(exception));
        }
        
        @Override
        public Try<T> recoverWith(Function<Exception, Try<T>> f) {
            return f.apply(exception);
        }
        
        @Override
        public void onFailure(Consumer<Exception> handler) {
            handler.accept(exception);
        }
        
        @Override
        public <R> R fold(Function<Exception, R> f, Function<T, R> g) {
            return f.apply(exception);
        }
        
        @Override
        public String toString() { return "Failure(" + exception + ")"; }
    }
}
```

---

## Step 6: Currying Utilities

```java
// currying/CurryingUtils.java
package com.learning.currying;

import java.util.function.*;

public class CurryingUtils {
    
    public static <A, B, R> Function<A, Function<B, R>> curry(
            BiFunction<A, B, R> f) {
        return a -> b -> f.apply(a, b);
    }
    
    public static <A, B, C, R> Function<A, Function<B, Function<C, R>>> 
            curry(Function3<A, B, C, R> f) {
        return a -> b -> c -> f.apply(a, b, c);
    }
    
    public static <A, B, R> BiFunction<A, B, R> uncurry(
            Function<A, Function<B, R>> f) {
        return (a, b) -> f.apply(a).apply(b);
    }
    
    public static <A, B> Function<A, B> partial(
            BiFunction<A, B, B> f, A value) {
        return a -> f.apply(value, a);
    }
    
    public static <T> Function<T, T> swap() {
        return Function.identity();
    }
    
    public static <T> UnaryOperator<T> identity() {
        return t -> t;
    }
    
    public static <T> BinaryOperator<T> constant(T value) {
        return (a, b) -> value;
    }
    
    public static <T extends Comparable<T>> BinaryOperator<T> min() {
        return (a, b) -> a.compareTo(b) < 0 ? a : b;
    }
    
    public static <T extends Comparable<T>> BinaryOperator<T> max() {
        return (a, b) -> a.compareTo(b) > 0 ? a : b;
    }
}
```

---

## Step 7: Higher-Order Functions

```java
// HOF/HigherOrderFunctions.java
package com.learning.HOF;

import java.util.function.*;
import java.util.*;

public class HigherOrderFunctions {
    
    public static <T> Function<T, Boolean> constantly(Boolean value) {
        return t -> value;
    }
    
    public static <T> Predicate<T> negate(Predicate<T> p) {
        return t -> !p.test(t);
    }
    
    public static <T> Predicate<T> and(Predicate<T> p1, Predicate<T> p2) {
        return t -> p1.test(t) && p2.test(t);
    }
    
    public static <T> Predicate<T> or(Predicate<T> p1, Predicate<T> p2) {
        return t -> p1.test(t) || p2.test(t);
    }
    
    public static <T> List<Function<T, T>> iterates(
            Function<T, T> f, int n) {
        List<Function<T, T>> result = new ArrayList<>();
        Function<T, T> current = Function.identity();
        
        for (int i = 0; i < n; i++) {
            final Function<T, T> fn = current;
            result.add(fn);
            current = current.andThen(f);
        }
        
        return result;
    }
    
    public static <T> List<T> unfold(T seed, 
            Function<T, Optional<T>> f) {
        List<T> result = new ArrayList<>();
        Optional<T> current = Optional.of(seed);
        
        while (current.isPresent()) {
            result.add(current.get());
            current = f.apply(current.get());
        }
        
        return result;
    }
    
    public static <T, R> Function<T, R> fix(
            Function<Function<T, R>, Function<T, R>> f) {
        return t -> f.apply(x -> fix(f).apply(t)).apply(t);
    }
    
    public static <T, U, R> Function<T, R> flip(
            BiFunction<T, U, R> f) {
        return t -> u -> f.apply(u, t);
    }
}
```

---

## Step 8: Main Application

```java
// Main.java
package com.learning;

import com.learning.functional.*;
import com.learning.currying.*;
import com.learning.function.*;
import java.util.*;
import java.util.function.*;

public class Main {
    public static void main(String[] args) {
        demonstrateOption();
        demonstrateEither();
        demonstrateTry();
        demonstrateCurrying();
    }
    
    private static void demonstrateOption() {
        System.out.println("=== Option Examples ===");
        
        Option<String> some = Option.of("Hello");
        Option<String> none = Option.empty();
        
        System.out.println("some.map(String::toUpperCase): " + 
            some.map(String::toUpperCase));
        System.out.println("none.map(String::toUpperCase): " + 
            none.map(String::toUpperCase));
        
        System.out.println("some.orElse('default'): " + 
            some.orElse("default"));
        System.out.println("none.orElse('default'): " + 
            none.orElse("default"));
        
        System.out.println("some.filter(s -> s.length() > 3): " + 
            some.filter(s -> s.length() > 3));
        System.out.println("some.filter(s -> s.length() > 10): " + 
            some.filter(s -> s.length() > 10));
        
        some.ifPresent(System.out::println);
    }
    
    private static void demonstrateEither() {
        System.out.println("\n=== Either Examples ===");
        
        Either<String, Integer> left = Either.left("Error");
        Either<String, Integer> right = Either.right(42);
        
        System.out.println("left.isLeft(): " + left.isLeft());
        System.out.println("right.isRight(): " + right.isRight());
        
        System.out.println("right.map(x -> x * 2): " + 
            right.map(x -> x * 2));
        
        System.out.println("right.mapLeft(String::toUpperCase): " + 
            right.mapLeft(String::toUpperCase));
        
        System.out.println("right.fold(e -> e, x -> x * 2): " + 
            right.fold(e -> e, x -> x * 2));
    }
    
    private static void demonstrateTry() {
        System.out.println("\n=== Try Examples ===");
        
        Try<String> success = Try.of(() -> "Hello");
        Try<String> failure = Try.of(() -> {
            throw new RuntimeException("Error");
        });
        
        System.out.println("success.isSuccess(): " + success.isSuccess());
        System.out.println("failure.isFailure(): " + failure.isFailure());
        
        System.out.println("success.map(String::toUpperCase): " + 
            success.map(String::toUpperCase));
        
        System.out.println("failure.recover(e -> 'Recovered'): " + 
            failure.recover(e -> "Recovered"));
        
        failure.onFailure(e -> System.out.println("Error: " + e.getMessage()));
    }
    
    private static void demonstrateCurrying() {
        System.out.println("\n=== Currying Examples ===");
        
        BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
        
        Function<Integer, Function<Integer, Integer>> curriedAdd = 
            CurryingUtils.curry(add);
        
        System.out.println("curriedAdd.apply(5).apply(3): " + 
            curriedAdd.apply(5).apply(3));
        
        Function<Integer, Function<Integer, Integer>> multiply = 
            CurryingUtils.curry((a, b) -> a * b);
        
        System.out.println("multiply.apply(4).apply(5): " + 
            multiply.apply(4).apply(5));
        
        Function<Integer, Integer> triple = multiply.apply(3);
        System.out.println("triple.apply(7): " + triple.apply(7));
    }
}
```

---

## Build Instructions

```bash
cd 11-functional-programming
javac -d target/classes -sourcepath src/main/java src/main/java/com/learning/**/*.java
java -cp target/classes com.learning.Main
```

---

# Real-World Project: Functional E-Commerce Validation Library

## Project Overview

**Duration**: 8+ hours  
**Difficulty**: Advanced  
**Concepts Used**: Advanced Functional Programming, Validation Monad, Railway-Oriented Programming, State Monads, Writer Monads, Reader Monads

This project implements a comprehensive functional validation library for e-commerce applications.

---

## Project Structure

```
11-functional-programming/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── validation/
│   │   ├── Validator.java
│   │   ├── ValidationResult.java
│   │   ├── Rule.java
│   │   └── validators/
│   ├── ecommerce/
│   │   ├── ProductValidator.java
│   │   ├── CustomerValidator.java
│   │   ├── OrderValidator.java
│   │   └── PaymentValidator.java
│   ├── monad/
│   │   ├── State.java
│   │   ├── Reader.java
│   │   └── Writer.java
│   ├── model/
│   │   ├── Product.java
│   │   ├── Customer.java
│   │   ├── Order.java
│   │   └── Payment.java
│   └── service/
│       └── ValidationService.java
└── src/main/resources/
```

---

## POM.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>functional-programming</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
</project>
```

---

## Validation Result Type

```java
// validation/ValidationResult.java
package com.learning.validation;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public abstract class ValidationResult<T> {
    private ValidationResult() {}
    
    public static <T> ValidationResult<T> valid(T value) {
        return new Valid<>(value);
    }
    
    public static <T> ValidationResult<T> invalid(List<ValidationError> errors) {
        return new Invalid<>(errors);
    }
    
    public static <T> ValidationResult<T> invalid(ValidationError error) {
        return new Invalid<>(List.of(error));
    }
    
    public abstract boolean isValid();
    public abstract T getValue();
    public abstract List<ValidationError> getErrors();
    public abstract <R> ValidationResult<R> map(Function<T, R> f);
    public abstract <R> ValidationResult<R> flatMap(
            Function<T, ValidationResult<R>> f);
    public abstract ValidationResult<T> combine(
            ValidationResult<T> other, BiFunction<T, T, T> combiner);
    public abstract <R> R fold(
            Function<T, R> f, Function<List<ValidationError>, R> g);
    
    public static final class Valid<T> extends ValidationResult<T> {
        private final T value;
        
        public Valid(T value) {
            this.value = value;
        }
        
        @Override
        public boolean isValid() { return true; }
        
        @Override
        public T getValue() { return value; }
        
        @Override
        public List<ValidationError> getErrors() { 
            return Collections.emptyList(); 
        }
        
        @Override
        public <R> ValidationResult<R> map(Function<T, R> f) {
            return new Valid<>(f.apply(value));
        }
        
        @Override
        public <R> ValidationResult<R> flatMap(
                Function<T, ValidationResult<R>> f) {
            return f.apply(value);
        }
        
        @Override
        public ValidationResult<T> combine(
                ValidationResult<T> other, BiFunction<T, T, T> combiner) {
            return other.fold(
                errors -> new Invalid<>(errors),
                value2 -> new Valid<>(combiner.apply(this.value, value2)));
        }
        
        @Override
        public <R> R fold(
                Function<T, R> f, 
                Function<List<ValidationError>, R> g) {
            return f.apply(value);
        }
        
        @Override
        public String toString() { return "Valid(" + value + ")"; }
    }
    
    public static final class Invalid<T> extends ValidationResult<T> {
        private final List<ValidationError> errors;
        
        public Invalid(List<ValidationError> errors) {
            this.errors = new ArrayList<>(errors);
        }
        
        @Override
        public boolean isValid() { return false; }
        
        @Override
        public T getValue() { 
            throw new IllegalStateException("No value"); 
        }
        
        @Override
        public List<ValidationError> getErrors() { 
            return Collections.unmodifiableList(errors); 
        }
        
        @Override
        public <R> ValidationResult<R> map(Function<T, R> f) {
            return new Invalid<>(errors);
        }
        
        @Override
        public <R> ValidationResult<R> flatMap(
                Function<T, ValidationResult<R>> f) {
            return new Invalid<>(errors);
        }
        
        @Override
        public ValidationResult<T> combine(
                ValidationResult<T> other, BiFunction<T, T, T> combiner) {
            List<ValidationError> combined = new ArrayList<>(errors);
            combined.addAll(other.getErrors());
            return new Invalid<>(combined);
        }
        
        @Override
        public <R> R fold(
                Function<T, R> f, 
                Function<List<ValidationError>, R> g) {
            return g.apply(errors);
        }
        
        @Override
        public String toString() { return "Invalid(" + errors + ")"; }
    }
}

public class ValidationError {
    private final String field;
    private final String message;
    private final ErrorCode code;
    
    public ValidationError(String field, String message, ErrorCode code) {
        this.field = field;
        this.message = message;
        this.code = code;
    }
    
    public String getField() { return field; }
    public String getMessage() { return message; }
    public ErrorCode getCode() { return code; }
    
    @Override
    public String toString() {
        return field + ": " + message + " (" + code + ")";
    }
    
    public enum ErrorCode {
        REQUIRED, INVALID_FORMAT, OUT_OF_RANGE, DUPLICATE, NOT_FOUND
    }
}
```

---

## Validator Framework

```java
// validation/Validator.java
package com.learning.validation;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class Validator<T> {
    private final List<Rule<T>> rules;
    
    private Validator(List<Rule<T>> rules) {
        this.rules = rules;
    }
    
    public static <T> Validator<T> create() {
        return new Validator<>(new ArrayList<>());
    }
    
    public Validator<T> addRule(Rule<T> rule) {
        List<Rule<T>> newRules = new ArrayList<>(rules);
        newRules.add(rule);
        return new Validator<>(newRules);
    }
    
    public Validator<T> addRule(Predicate<T> predicate, 
                          ValidationError error) {
        return addRule(t -> predicate.test(t) ? 
            ValidationResult.valid(t) : 
            ValidationResult.invalid(error));
    }
    
    public ValidationResult<T> validate(T value) {
        List<ValidationError> errors = rules.stream()
            .flatMap(rule -> rule.apply(value).getErrors().stream())
            .collect(Collectors.toList());
        
        return errors.isEmpty() ? 
            ValidationResult.valid(value) : 
            ValidationResult.invalid(errors);
    }
    
    public <R> Validator<R> transform(
            Function<T, R> f, 
            Validator<R> other) {
        return other;
    }
    
    public FunctionalInterface rule(String field) {
        return null;
    }
}

public interface Rule<T> extends Function<T, ValidationResult<T>> {}

public class Rules {
    public static <T> Rule<T> required(
            Function<T, String> getter, String field) {
        return t -> {
            String value = getter.apply(t);
            if (value == null || value.isEmpty()) {
                return ValidationResult.invalid(
                    new ValidationError(field, 
                        field + " is required", 
                        ValidationError.ErrorCode.REQUIRED));
            }
            return ValidationResult.valid(t);
        };
    }
    
    public static <T> Rule<T> minLength(
            Function<T, String> getter, int min, String field) {
        return t -> {
            String value = getter.apply(t);
            if (value != null && value.length() < min) {
                return ValidationResult.invalid(
                    new ValidationError(field, 
                        field + " must be at least " + min + " characters",
                        ValidationError.ErrorCode.OUT_OF_RANGE));
            }
            return ValidationResult.valid(t);
        };
    }
    
    public static <T> Rule<T> maxLength(
            Function<T, String> getter, int max, String field) {
        return t -> {
            String value = getter.apply(t);
            if (value != null && value.length() > max) {
                return ValidationResult.invalid(
                    new ValidationError(field, 
                        field + " must be at most " + max + " characters",
                        ValidationError.ErrorCode.OUT_OF_RANGE));
            }
            return ValidationResult.valid(t);
        };
    }
    
    public static <T> Rule<T> pattern(
            Function<T, String> getter, String regex, String field) {
        return t -> {
            String value = getter.apply(t);
            if (value != null && !value.matches(regex)) {
                return ValidationResult.invalid(
                    new ValidationError(field, 
                        field + " has invalid format",
                        ValidationError.ErrorCode.INVALID_FORMAT));
            }
            return ValidationResult.valid(t);
        };
    }
    
    public static <T> Rule<T> range(
            Function<T, Number> getter, int min, int max, String field) {
        return t -> {
            Number value = getter.apply(t);
            if (value != null) {
                int intValue = value.intValue();
                if (intValue < min || intValue > max) {
                    return ValidationResult.invalid(
                        new ValidationError(field, 
                            field + " must be between " + min + " and " + max,
                            ValidationError.ErrorCode.OUT_OF_RANGE));
                }
            }
            return ValidationResult.valid(t);
        };
    }
    
    public static <T> Rule<T> email(Function<T, String> getter, String field) {
        return pattern(getter, "^[A-Za-z0-9+_.-]+@(.+)$", field);
    }
}
```

---

## State Monad

```java
// monad/State.java
package com.learning.monad;

import java.util.function.*;

public class State<S, A> {
    private final Function<S, Pair<S, A>> run;
    
    private State(Function<S, Pair<S, A>> run) {
        this.run = run;
    }
    
    public static <S, A> State<S, A> of(A value) {
        return new State<>(s -> new Pair<>(s, value));
    }
    
    public static <S, A> State<S, A> get() {
        return new State<>(s -> new Pair<>(s, s));
    }
    
    public static <S, A> State<S, A> set(S state) {
        return new State<>(s -> new Pair<>(state, (A) null));
    }
    
    public static <S, A> State<S, A> modify(Function<S, S> f) {
        return new State<>(s -> new Pair<>(f.apply(s), (A) null));
    }
    
    public Pair<S, A> run(S state) {
        return run.apply(state);
    }
    
    public A runS(S state) {
        return run.apply(state)._2;
    }
    
    public <R> State<S, R> map(Function<A, R> f) {
        return new State<>(s -> {
            Pair<S, A> p = run.apply(s);
            return new Pair<>(p._1, f.apply(p._2));
        });
    }
    
    public <R> State<S, R> flatMap(Function<A, State<S, R>> f) {
        return new State<>(s -> {
            Pair<S, A> p = run.apply(s);
            return f.apply(p._2).run(p._1);
        });
    }
    
    public <R> State<S, R> andThen(State<S, R> next) {
        return flatMap(a -> next);
    }
    
    public static <S> State<S, S> getState() {
        return get();
    }
    
    public static <S> State<S, Unit> put(S state) {
        return set(state);
    }
    
    public static <S> State<S, Unit> modify(Function<S, S> f) {
        return State.modify(f);
    }
    
    public static class Pair<S, A> {
        public final S _1;
        public final A _2;
        
        public Pair(S _1, A _2) {
            this._1 = _1;
            this._2 = _2;
        }
        
        @Override
        public String toString() {
            return "(" + _1 + ", " + _2 + ")";
        }
    }
    
    public static class Unit {}
}
```

---

## Reader Monad

```java
// monad/Reader.java
package com.learning.monad;

import java.util.function.*;

public class Reader<R, A> {
    private final Function<R, A> run;
    
    private Reader(Function<R, A> run) {
        this.run = run;
    }
    
    public static <R, A> Reader<R, A> of(A value) {
        return new Reader<>(r -> value);
    }
    
    public static <R, A> Reader<R, A> ask() {
        return new Reader<>(r -> r);
    }
    
    public A run(R env) {
        return run.apply(env);
    }
    
    public <R2> Reader<R2, A> local(Function<R2, R> f) {
        return new Reader<>(r2 -> run.apply(f.apply(r2)));
    }
    
    public <R2> Reader<R2, A> compose(
            Function<R2, R> toR, 
            Function<R, R2> fromR) {
        return new Reader<>(r2 -> run.apply(toR.apply(r2)));
    }
    
    public <R2> Reader<R, R2> andThen(Reader<A, R2> next) {
        return new Reader<>(r -> next.run(run.apply(r)));
    }
    
    public <B> Reader<R, B> map(Function<A, B> f) {
        return new Reader<>(r -> f.apply(run.apply(r)));
    }
    
    public <B> Reader<R, B> flatMap(Function<A, Reader<R, B>> f) {
        return new Reader<>(r -> f.apply(run.apply(r)).run(r));
    }
    
    public static <R> Reader<R, R> asks(Function<R, A> f) {
        return new Reader<>(f);
    }
}
```

---

## Writer Monad

```java
// monad/Writer.java
package com.learning.monad;

import java.util.function.*;
import java.util.*;

public class Writer<W, A> {
    private final Pair<W, A> output;
    
    private Writer(Pair<W, A> output) {
        this.output = output;
    }
    
    public static <W, A> Writer<W, A> of(A value) {
        return new Writer<>(new Pair<>(emptyLog(), value));
    }
    
    public static <W, A> Writer<W, A> of(Supplier<W> log, A value) {
        return new Writer<>(new Pair<>(log.get(), value));
    }
    
    public static <W, A> Writer<W, A> write(W log) {
        return new Writer<>(new Pair<>(log, (A) null));
    }
    
    public static <W, A> Writer<W, A> tell(W log, A value) {
        return new Writer<>(new Pair<>(log, value));
    }
    
    public A run() {
        return output._2;
    }
    
    public W written() {
        return output._1;
    }
    
    public <W2> Writer<W2, A> mapLog(Function<W, W2> f) {
        return new Writer<>(new Pair<>(f.apply(output._1), output._2));
    }
    
    public <B> Writer<W, B> map(Function<A, B> f) {
        return new Writer<>(new Pair<>(output._1, f.apply(output._2)));
    }
    
    public <B> Writer<W, B> flatMap(Function<A, Writer<W, B>> f) {
        Writer<W, B> result = f.apply(output._2);
        return new Writer<>(new Pair<>(combine(output._1, result.output._1), 
            result.output._2));
    }
    
    public static <W> W emptyLog() {
        if (W.class == String.class) {
            return (W) "";
        }
        return (W) Collections.emptyList();
    }
    
    public static <W> W combine(W w1, W w2) {
        if (w1 instanceof String && w2 instanceof String) {
            return (W) (((String) w1) + ((String) w2));
        }
        return w2;
    }
    
    public static class Pair<W, A> {
        public final W _1;
        public final A _2;
        
        public Pair(W _1, A _2) {
            this._1 = _1;
            this._2 = _2;
        }
        
        @Override
        public String toString() {
            return "(" + _1 + ", " + _2 + ")";
        }
    }
}
```

---

## E-Commerce Validators

```java
// ecommerce/ProductValidator.java
package com.learning.ecommerce;

import com.learning.validation.*;
import java.util.function.*;
import java.math.BigDecimal;
import com.learning.model.Product;

public class ProductValidator {
    
    public ValidationResult<Product> validate(Product product) {
        return Validator.<Product>create()
            .addRule(Rules.required(Product::getName, "name"))
            .addRule(Rules.minLength(Product::getName, 3, "name"))
            .addRule(Rules.maxLength(Product::getName, 200, "name"))
            .addRule(Rules.required(Product::getCategory, "category"))
            .addRule(Rules.minLength(Product::getCategory, 2, "category"))
            .addRule(Rules.range(Product::getPrice, 1, 1000000, "price"))
            .addRule(Rules.range(Product::getStockQuantity, 0, 1000000, "stockQuantity"))
            .validate(product);
    }
}

// ecommerce/CustomerValidator.java
package com.learning.ecommerce;

import com.learning.validation.*;
import com.learning.model.Customer;

public class CustomerValidator {
    
    public ValidationResult<Customer> validate(Customer customer) {
        return Validator.<Customer>create()
            .addRule(Rules.required(Customer::getName, "name"))
            .addRule(Rules.minLength(Customer::getName, 2, "name"))
            .addRule(Rules.required(Customer::getEmail, "email"))
            .addRule(Rules.email(Customer::getEmail, "email"))
            .validate(customer);
    }
}

// ecommerce/OrderValidator.java
package com.learning.ecommerce;

import com.learning.validation.*;
import com.learning.model.Order;

public class OrderValidator {
    
    public ValidationResult<Order> validate(Order order) {
        return Validator.<Order>create()
            .addRule(Rules.required(Order::getId, "orderId"))
            .addRule(Rules.required(Order::getCustomerId, "customerId"))
            .validate(order);
    }
}
```

---

## Main Application

```java
// Main.java
package com.learning;

import com.learning.validation.*;
import com.learning.ecommerce.*;
import com.learning.model.*;
import java.math.BigDecimal;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        testValidation();
        testMonads();
    }
    
    private static void testValidation() {
        ProductValidator validator = new ProductValidator();
        
        Product validProduct = new Product("1", "Laptop", "Electronics",
            new BigDecimal("999.99"), 100);
        
        Product invalidProduct = new Product("1", "AB", "E",
            new BigDecimal("-10"), -5);
        
        System.out.println("=== Validation Results ===");
        
        ValidationResult<Product> result1 = validator.validate(validProduct);
        System.out.println("Valid product: " + result1.isValid());
        
        ValidationResult<Product> result2 = validator.validate(invalidProduct);
        System.out.println("Invalid product: " + result2.isValid());
        
        if (!result2.isValid()) {
            System.out.println("Errors:");
            result2.getErrors().forEach(e -> System.out.println("  " + e));
        }
    }
    
    private static void testMonads() {
        System.out.println("\n=== State Monad ===");
        
        var state = State.<String, Integer>of(42)
            .map(x -> x * 2)
            .flatMap(x -> State.of(x + 10));
        
        var result = state.run("initial");
        System.out.println("State result: " + result._2 + ", state: " + result._1);
        
        System.out.println("\n=== Reader Monad ===");
        
        Reader<Map<String, String>, String> reader = Reader.<Map<String, String>, String>ask()
            .map(s -> "Hello, " + s.get("name"));
        
        Map<String, String> env = Map.of("name", "World");
        System.out.println("Reader result: " + reader.run(env));
        
        System.out.println("\n=== Writer Monad ===");
        
        var writer = Writer.<String, Integer>of(42)
            .map(x -> x * 2)
            .flatMap(x -> Writer.tell("Computed: " + x + ", ", x + 10));
        
        System.out.println("Writer result: " + writer.run() + ", logs: " + writer.written());
    }
}
```

---

## Build Instructions

```bash
cd 11-functional-programming
mvn clean compile
mvn exec:java -Dexec.mainClass="com.learning.Main"
```

This project demonstrates advanced functional programming patterns in Java, including comprehensive validation, state management, and reader/writer monads for building robust e-commerce applications.