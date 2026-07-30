package com.java.streams.optional.lab04;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;

public class OptionalDeepLab {

    record Address(String city) {}
    record Person(String name, Address address) {}

    public static void main(String[] args) {
        // flatMap chain
        Person p1 = new Person("Alice", new Address("London"));
        Person p2 = new Person("Bob", null);

        printCity(Optional.ofNullable(p1));
        printCity(Optional.ofNullable(p2));

        // Optional to Stream
        List<Optional<String>> optionals = List.of(
                Optional.of("a"), Optional.empty(), Optional.of("b"));
        List<String> flattened = optionals.stream()
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
        System.out.println("Flattened: " + flattened);

        // orElseThrow
        Optional<String> present = Optional.of("hello");
        System.out.println("orElseThrow: " + present.orElseThrow());

        // or() — alternative Optional
        Optional<String> fallback = present
                .or(() -> Optional.of("default"));
        System.out.println("or(): " + fallback);

        // OptionalInt
        OptionalInt optInt = OptionalInt.of(42);
        System.out.println("OptionalInt: " + optInt.orElseThrow());
    }

    static void printCity(Optional<Person> person) {
        String city = person
                .map(Person::address)
                .map(Address::city)
                .orElse("Unknown");
        System.out.println("City: " + city);
    }
}
