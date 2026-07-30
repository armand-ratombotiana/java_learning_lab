package com.architecture.deep.lab01;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class EventDrivenLab {
    public static void main(String[] args) {
        var eventStore = new EventStore();
        var idempotencyRegistry = new IdempotencyRegistry();
        var eventBus = new EventBus();
        var accountProjection = new AccountProjection();

        eventBus.subscribe(AccountEvent.class, accountProjection);

        var accountId = UUID.randomUUID();

        var cmd1 = new CreateAccountCommand(accountId, "Alice", 1000);
        var cmd2 = new DepositMoneyCommand(accountId, 500);
        var cmd3 = new WithdrawMoneyCommand(accountId, 200);

        var handler = new AccountCommandHandler(eventStore, eventBus, idempotencyRegistry);

        handler.handle(cmd1);
        handler.handle(cmd2);
        handler.handle(cmd3);

        System.out.println("Final balance: " + accountProjection.getBalance(accountId));

        var replayed = eventStore.replay(accountId);
        System.out.println("Replayed " + replayed.size() + " events for account " + accountId);
    }
}

sealed interface AccountEvent permits AccountCreated, MoneyDeposited, MoneyWithdrawn {
    UUID eventId();
    UUID aggregateId();
    Instant timestamp();
    int version();
}

record AccountCreated(UUID eventId, UUID aggregateId, Instant timestamp, int version, String owner, long initialBalance) implements AccountEvent {}
record MoneyDeposited(UUID eventId, UUID aggregateId, Instant timestamp, int version, long amount) implements AccountEvent {}
record MoneyWithdrawn(UUID eventId, UUID aggregateId, Instant timestamp, int version, long amount) implements AccountEvent {}

record CreateAccountCommand(UUID aggregateId, String owner, long initialBalance) {}
record DepositMoneyCommand(UUID aggregateId, long amount) {}
record WithdrawMoneyCommand(UUID aggregateId, long amount) {}

class AccountCommandHandler {
    private final EventStore eventStore;
    private final EventBus eventBus;
    private final IdempotencyRegistry idempotencyRegistry;
    private long sequence = 0;

    AccountCommandHandler(EventStore eventStore, EventBus eventBus, IdempotencyRegistry idempotencyRegistry) {
        this.eventStore = eventStore;
        this.eventBus = eventBus;
        this.idempotencyRegistry = idempotencyRegistry;
    }

    void handle(CreateAccountCommand cmd) {
        var event = new AccountCreated(
            UUID.randomUUID(), cmd.aggregateId(), Instant.now(), nextVersion(cmd.aggregateId()),
            cmd.owner(), cmd.initialBalance()
        );
        storeAndPublish(event);
    }

    void handle(DepositMoneyCommand cmd) {
        var event = new MoneyDeposited(
            UUID.randomUUID(), cmd.aggregateId(), Instant.now(), nextVersion(cmd.aggregateId()),
            cmd.amount()
        );
        storeAndPublish(event);
    }

    void handle(WithdrawMoneyCommand cmd) {
        var event = new MoneyWithdrawn(
            UUID.randomUUID(), cmd.aggregateId(), Instant.now(), nextVersion(cmd.aggregateId()),
            cmd.amount()
        );
        storeAndPublish(event);
    }

    private void storeAndPublish(AccountEvent event) {
        if (idempotencyRegistry.alreadyProcessed(event.eventId())) return;
        eventStore.append(event);
        eventBus.publish(event);
        idempotencyRegistry.markProcessed(event.eventId());
    }

    private int nextVersion(UUID aggregateId) {
        return (int) ++sequence;
    }
}

class EventStore {
    private final Map<UUID, List<AccountEvent>> store = new ConcurrentHashMap<>();

    void append(AccountEvent event) {
        store.computeIfAbsent(event.aggregateId(), k -> new CopyOnWriteArrayList<>()).add(event);
    }

    List<AccountEvent> replay(UUID aggregateId) {
        return store.getOrDefault(aggregateId, List.of());
    }
}

class EventBus {
    private final Map<Class<?>, List<Consumer<?>>> subscribers = new ConcurrentHashMap<>();

    <T> void subscribe(Class<T> eventType, Consumer<T> handler) {
        subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(handler);
    }

    @SuppressWarnings("unchecked")
    <T> void publish(T event) {
        var handlers = subscribers.get(event.getClass());
        if (handlers != null) {
            handlers.forEach(h -> ((Consumer<T>) h).accept(event));
        }
    }
}

class AccountProjection implements Consumer<AccountEvent> {
    private final Map<UUID, Long> balances = new ConcurrentHashMap<>();

    long getBalance(UUID accountId) {
        return balances.getOrDefault(accountId, 0L);
    }

    @Override
    public void accept(AccountEvent event) {
        switch (event) {
            case AccountCreated e -> balances.put(e.aggregateId(), e.initialBalance());
            case MoneyDeposited e -> balances.merge(e.aggregateId(), e.amount(), Long::sum);
            case MoneyWithdrawn e -> balances.merge(e.aggregateId(), -e.amount(), Long::sum);
        }
    }
}

class IdempotencyRegistry {
    private final Set<UUID> processed = ConcurrentHashMap.newKeySet();

    boolean alreadyProcessed(UUID eventId) {
        return processed.contains(eventId);
    }

    void markProcessed(UUID eventId) {
        processed.add(eventId);
    }
}
