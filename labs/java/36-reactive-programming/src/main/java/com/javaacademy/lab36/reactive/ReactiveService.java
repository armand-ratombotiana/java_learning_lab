package com.javaacademy.lab36.reactive;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.List;

public class ReactiveService {

    private final List<String> mockUsers = List.of("Alice", "Bob", "Charlie");
    private final List<String> mockOrders = List.of("ORD-001", "ORD-002", "ORD-003");

    public Mono<String> getUserById(int id) {
        if (id >= 0 && id < mockUsers.size()) {
            return Mono.just(mockUsers.get(id))
                .delayElement(Duration.ofMillis(50));
        }
        return Mono.error(new IllegalArgumentException("User not found: " + id));
    }

    public Flux<String> getAllUsers() {
        return Flux.fromIterable(mockUsers)
            .delayElements(Duration.ofMillis(30));
    }

    public Flux<String> getOrdersForUser(String userId) {
        return Flux.fromIterable(mockOrders)
            .map(order -> order + "-" + userId)
            .delayElements(Duration.ofMillis(20));
    }

    public Flux<String> getUserDashboard(int userId) {
        return getUserById(userId)
            .flatMapMany(user ->
                Flux.merge(
                    getOrdersForUser(user),
                    Flux.just("Profile loaded for " + user)
                )
            );
    }

    public Mono<List<String>> collectAllData() {
        return Flux.merge(
            getAllUsers(),
            Flux.fromIterable(mockOrders)
        ).collectList();
    }

    public Mono<String> getFirstAvailableUser() {
        return getAllUsers()
            .next()
            .defaultIfEmpty("No users available");
    }
}
