package com.java.streams.optional.lab02;

import java.util.*;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.*;

record Transaction(String currency, double amount) {}

public class StreamCollectorsLab {

    public static void main(String[] args) {
        List<Transaction> txns = Arrays.asList(
                new Transaction("USD", 150.0),
                new Transaction("EUR", 200.0),
                new Transaction("USD", 50.0),
                new Transaction("GBP", 300.0),
                new Transaction("EUR", 100.0));

        // groupingBy — sum amounts per currency
        Map<String, Double> sumByCurrency = txns.stream()
                .collect(groupingBy(Transaction::currency,
                         summingDouble(Transaction::amount)));
        System.out.println("Sum by currency: " + sumByCurrency);

        // partitioningBy — above/below 150
        Map<Boolean, List<Transaction>> partitioned = txns.stream()
                .collect(partitioningBy(t -> t.amount() > 150));
        System.out.println("Above 150: " + partitioned.get(true));
        System.out.println("Below 150: " + partitioned.get(false));

        // teeing — count + total in one pass
        Map.Entry<Long, Double> stats = txns.stream()
                .collect(teeing(counting(),
                         summingDouble(Transaction::amount),
                         Map::entry));
        System.out.println("Count: " + stats.getKey()
                + ", Total: " + stats.getValue());

        // toMap with merge function
        Map<String, String> currencyNames = Map.of(
                "USD", "US Dollar", "EUR", "Euro", "GBP", "Pound");
        System.out.println("Currency names: " + currencyNames);
    }
}
