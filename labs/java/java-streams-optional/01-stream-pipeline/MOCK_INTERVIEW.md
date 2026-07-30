# MOCK_INTERVIEW — Stream Pipeline

## Scenario
Given a list of transactions, find the top‑3 highest‑value transactions from the last 30 days.

## Interviewer Notes
- Candidate should chain `filter()`, `sorted()`, `limit()`, `collect()`
- Discuss early filtering for performance
- Edge case: fewer than 3 transactions

## Expected Solution Sketch
```java
List<Transaction> top3 = transactions.stream()
    .filter(t -> t.date().isAfter(LocalDate.now().minusDays(30)))
    .sorted(Comparator.comparing(Transaction::amount).reversed())
    .limit(3)
    .toList();
```

## Follow‑Up
- What if `transactions` is empty?
- How would you handle `null` values in `amount`?
