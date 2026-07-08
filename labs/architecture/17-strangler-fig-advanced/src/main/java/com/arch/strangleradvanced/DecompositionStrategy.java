package com.arch.strangleradvanced;

import java.util.*;
import java.util.function.Predicate;

public class DecompositionStrategy {
    private final List<DecompositionUnit> units = new ArrayList<>();

    public void addUnit(String name, Predicate<String> matcher, int priority, String domain) {
        units.add(new DecompositionUnit(name, matcher, priority, domain));
        units.sort(Comparator.comparingInt(DecompositionUnit::priority));
    }

    public Optional<DecompositionUnit> findUnit(String feature) {
        return units.stream().filter(u -> u.matcher().test(feature)).findFirst();
    }

    public List<DecompositionUnit> getUnitsByDomain(String domain) {
        return units.stream().filter(u -> u.domain().equals(domain)).toList();
    }

    public List<DecompositionUnit> getAllUnits() { return List.copyOf(units); }
}

record DecompositionUnit(String name, Predicate<String> matcher, int priority, String domain) {}
