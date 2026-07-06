package com.javaacademy.lab40.bestpractices;

import java.util.*;
import java.time.*;

public class DefensiveCopyExample {

    private final String name;
    private final Date birthDate;
    private final List<String> tags;
    private final int[] scores;

    public DefensiveCopyExample(String name, Date birthDate, List<String> tags, int[] scores) {
        this.name = Objects.requireNonNull(name);
        this.birthDate = new Date(birthDate.getTime());
        this.tags = new ArrayList<>(tags);
        this.scores = scores.clone();
    }

    public String getName() { return name; }

    public Date getBirthDate() {
        return new Date(birthDate.getTime());
    }

    public List<String> getTags() {
        return Collections.unmodifiableList(tags);
    }

    public int[] getScores() {
        return scores.clone();
    }

    public static DefensiveCopyExample createFromUserInput(String name, int year, int month, int day, String... tags) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        return new DefensiveCopyExample(name, cal.getTime(), Arrays.asList(tags), new int[]{0});
    }

    public boolean isAdult() {
        LocalDate birth = LocalDate.of(birthDate.getYear() + 1900, birthDate.getMonth() + 1, birthDate.getDate());
        return Period.between(birth, LocalDate.now()).getYears() >= 18;
    }
}
