package com.learning.axon;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class Lab {
    // Event
    interface Event {}
    record AccountCreatedEvent(String id, String owner, double balance) implements Event {}
    record MoneyDepositedEvent(String id, double amount) implements Event {}
    record MoneyWithdrawnEvent(String id, double amount) implements Event {}

    // Command
    interface Command {}
    record CreateAccountCommand(String id, String owner, double balance) implements Command {}
    record DepositMoneyCommand(String id, double amount) implements Command {}
    record WithdrawMoneyCommand(String id, double amount) implements Command {}

    // Query
    record AccountQuery(String id) {}

    // Event Store (Event Sourcing)
    static class EventStore {
        final Map<String, List<Event>> events = new ConcurrentHashMap<>();
        void append(String aggregateId, Event event) {
            events.computeIfAbsent(aggregateId, k -> new CopyOnWriteArrayList<>()).add(event);
        }
        List<Event> read(String aggregateId) {
            return events.getOrDefault(aggregateId, List.of());
        }
    }

    // Aggregate
    static class AccountAggregate {
        String id;
        String owner;
        double balance;
        boolean active;

        AccountAggregate(List<Event> history) {
            history.forEach(this::apply);
        }

        AccountAggregate() {}

        List<Event> handle(CreateAccountCommand cmd) {
            if (active) throw new IllegalStateException("Already exists");
            return List.of(new AccountCreatedEvent(cmd.id(), cmd.owner(), cmd.balance()));
        }

        List<Event> handle(DepositMoneyCommand cmd) {
            if (!active) throw new IllegalStateException("Account closed");
            return List.of(new MoneyDepositedEvent(cmd.id(), cmd.amount()));
        }

        List<Event> handle(WithdrawMoneyCommand cmd) {
            if (!active) throw new IllegalStateException("Account closed");
            if (balance < cmd.amount()) throw new IllegalArgumentException("Insufficient funds");
            return List.of(new MoneyWithdrawnEvent(cmd.id(), cmd.amount()));
        }

        void apply(Event event) {
            switch (event) {
                case AccountCreatedEvent e -> { this.id = e.id(); this.owner = e.owner(); this.balance = e.balance(); this.active = true; }
                case MoneyDepositedEvent e -> this.balance += e.amount();
                case MoneyWithdrawnEvent e -> this.balance -= e.amount();
            }
        }

        String summary() { return id + ":" + owner + ":$" + balance + " active=" + active; }
    }

    // Command Bus
    static class CommandBus {
        final Map<Class<?>, Function<Command, List<Event>>> handlers = new HashMap<>();
        final EventStore eventStore = new EventStore();

        <C extends Command> void register(Class<C> type, Function<C, List<Event>> handler) {
            handlers.put(type, cmd -> handler.apply((C) cmd));
        }

        List<Event> dispatch(Command command) {
            var handler = handlers.get(command.getClass());
            if (handler == null) throw new IllegalArgumentException("No handler for " + command.getClass().getSimpleName());
            var events = handler.apply(command);
            events.forEach(e -> {
                var aggId = switch (e) {
                    case AccountCreatedEvent ace -> ace.id();
                    case MoneyDepositedEvent mde -> mde.id();
                    case MoneyWithdrawnEvent mwe -> mwe.id();
                };
                eventStore.append(aggId, e);
            });
            return events;
        }
    }

    // Query Handler
    static class QueryHandler {
        final EventStore eventStore;
        QueryHandler(EventStore eventStore) { this.eventStore = eventStore; }

        AccountAggregate handle(AccountQuery query) {
            var events = eventStore.read(query.id());
            if (events.isEmpty()) throw new NoSuchElementException("Account not found: " + query.id());
            return new AccountAggregate(events);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Axon Framework: CQRS & Event Sourcing ===\n");

        var bus = new CommandBus();
        bus.register(CreateAccountCommand.class, cmd -> new AccountAggregate().handle(cmd));
        bus.register(DepositMoneyCommand.class, cmd -> {
            var agg = new AccountAggregate(bus.eventStore.read(cmd.id()));
            return agg.handle(cmd);
        });
        bus.register(WithdrawMoneyCommand.class, cmd -> {
            var agg = new AccountAggregate(bus.eventStore.read(cmd.id()));
            return agg.handle(cmd);
        });

        var queries = new QueryHandler(bus.eventStore);

        // Command flow
        System.out.println("--- Command Side (Writes) ---");
        var cmds = List.of(
            new CreateAccountCommand("ACC-001", "Alice", 1000),
            new DepositMoneyCommand("ACC-001", 500),
            new WithdrawMoneyCommand("ACC-001", 200)
        );
        for (var cmd : cmds) {
            var events = bus.dispatch(cmd);
            System.out.println("  Command: " + cmd.getClass().getSimpleName());
            for (var e : events) System.out.println("    Event: " + e.getClass().getSimpleName() + " " + e);
        }

        // Query flow
        System.out.println("\n--- Query Side (Read) ---");
        var alice = queries.handle(new AccountQuery("ACC-001"));
        System.out.println("  Current state: " + alice.summary());

        // Event replay
        System.out.println("\n--- Event Store (Audit Log) ---");
        for (var e : bus.eventStore.read("ACC-001")) {
            System.out.println("  " + e.getClass().getSimpleName() + ": " + e);
        }

        // Multiple accounts
        System.out.println("\n--- Multiple Accounts ---");
        bus.dispatch(new CreateAccountCommand("ACC-002", "Bob", 2500));
        bus.dispatch(new DepositMoneyCommand("ACC-002", 1000));
        bus.dispatch(new WithdrawMoneyCommand("ACC-002", 500));
        var bob = queries.handle(new AccountQuery("ACC-002"));
        System.out.println("  Bob: " + bob.summary());
    }
}
