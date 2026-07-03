# Why Lambdas Matter

- **Conciseness** — dramatically reduce boilerplate for functional patterns
- **Parallelism** — fundamental building block for Streams and parallel processing
- **Deferred execution** — pass behaviour, not just data
- **Readability** — intent is clearer than loop-based implementations
- **API design** — enables powerful, flexible libraries (e.g., `Comparator.comparing`)

```java
// Instead of:
Collections.sort(people, new Comparator<Person>() {
    public int compare(Person a, Person b) {
        return a.getName().compareTo(b.getName());
    }
});

// You write:
people.sort(Comparator.comparing(Person::getName));
```
