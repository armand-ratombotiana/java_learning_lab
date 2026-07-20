package com.javalab.05;

import java.util.*;
import java.util.stream.Collectors;

public class MainImplementation {
    
    public static class Person {
        private final String name;
        private final int age;
        private final List<String> hobbies;
        
        public Person(String name, int age, List<String> hobbies) {
            this.name = name; this.age = age; this.hobbies = hobbies;
        }
        
        public String getName() { return name; }
        public int getAge() { return age; }
        public List<String> getHobbies() { return hobbies; }
    }
    
    public List<Person> filterAdults(List<Person> people) {
        return people.stream().filter(p -> p.getAge() >= 18).collect(Collectors.toList());
    }
    
    public Map<Integer, List<Person>> groupByAge(List<Person> people) {
        return people.stream().collect(Collectors.groupingBy(Person::getAge));
    }
    
    public Optional<Person> findOldest(List<Person> people) {
        return people.stream().max(Comparator.comparingInt(Person::getAge));
    }
}
