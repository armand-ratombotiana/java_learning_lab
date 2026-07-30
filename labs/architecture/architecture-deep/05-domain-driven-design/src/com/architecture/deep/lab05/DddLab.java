package com.architecture.deep.lab05;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DddLab {
    public static void main(String[] args) {
        var repo = new PolicyRepositoryImpl();
        var eventBus = new DomainEventBus();

        eventBus.subscribe(PolicyIssued.class, e ->
            System.out.println("Domain Event: Policy " + e.policyId() + " issued to " + e.holderName()));

        var holder = new PolicyHolder("H001", "Alice", new Address("123 Main St", "Springfield", "12345"));
        var coverage = new Coverage("C001", "Auto Insurance", new Money(50000, "USD"), new DateRange(
            LocalDate.now(), LocalDate.now().plusYears(1)));

        var policy = Policy.issue("P001", holder, coverage);
        policy.addCoverage(new Coverage("C002", "Liability", new Money(100000, "USD"),
            new DateRange(LocalDate.now(), LocalDate.now().plusYears(1))));

        for (var event : policy.domainEvents()) {
            eventBus.publish(event);
        }
        repo.save(policy);

        var loaded = repo.findById("P001");
        System.out.println("Loaded policy: " + loaded.map(p -> p.id() + " | " + p.status()).orElse("not found"));
    }
}

record PolicyHolder(String holderId, String name, Address address) {}
record Address(String street, String city, String zip) {}
record Money(long amount, String currency) {}
record DateRange(LocalDate start, LocalDate end) {}
record Coverage(String coverageId, String type, Money limit, DateRange period) {}

record PolicyIssued(String policyId, String holderName, LocalDate issuedDate) implements DomainEvent {}
record CoverageAdded(String policyId, String coverageType) implements DomainEvent {}

sealed interface DomainEvent permits PolicyIssued, CoverageAdded {}

class Policy {
    private final String id;
    private final PolicyHolder holder;
    private final List<Coverage> coverages;
    private final String status;
    private final List<DomainEvent> events = new ArrayList<>();

    private Policy(String id, PolicyHolder holder, List<Coverage> coverages, String status) {
        this.id = id;
        this.holder = holder;
        this.coverages = new ArrayList<>(coverages);
        this.status = status;
    }

    static Policy issue(String id, PolicyHolder holder, Coverage initialCoverage) {
        var policy = new Policy(id, holder, List.of(initialCoverage), "ACTIVE");
        policy.events.add(new PolicyIssued(id, holder.name(), LocalDate.now()));
        return policy;
    }

    void addCoverage(Coverage coverage) {
        coverages.add(coverage);
        events.add(new CoverageAdded(id, coverage.type()));
    }

    String id() { return id; }
    String status() { return status; }
    List<DomainEvent> domainEvents() { return List.copyOf(events); }
}

interface PolicyRepository {
    void save(Policy policy);
    Optional<Policy> findById(String id);
}

class PolicyRepositoryImpl implements PolicyRepository {
    private final Map<String, Policy> store = new ConcurrentHashMap<>();
    public void save(Policy policy) { store.put(policy.id(), policy); }
    public Optional<Policy> findById(String id) { return Optional.ofNullable(store.get(id)); }
}

class DomainEventBus {
    private final Map<Class<?>, List<java.util.function.Consumer<?>>> subscribers = new ConcurrentHashMap<>();

    <T> void subscribe(Class<T> type, java.util.function.Consumer<T> handler) {
        subscribers.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).add(handler);
    }

    @SuppressWarnings("unchecked")
    void publish(DomainEvent event) {
        var handlers = subscribers.get(event.getClass());
        if (handlers != null) {
            handlers.forEach(h -> ((java.util.function.Consumer<DomainEvent>) h).accept(event));
        }
    }
}
