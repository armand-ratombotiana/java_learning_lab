package com.learning.r2dbc;

public class R2DBCLab {

    public static void main(String[] args) {
        System.out.println("=== R2DBC Lab ===\n");

        System.out.println("1. R2DBC Concept:");
        System.out.println("   Reactive Relational Database Connectivity");
        System.out.println("   Non-blocking database access with reactive streams");

        System.out.println("\n2. Spring Data R2DBC Example:");
        System.out.println("   @Table(\"users\")");
        System.out.println("   record User(@Id Long id, String name, String email) {}");
        System.out.println("   ");
        System.out.println("   interface UserRepository extends R2dbcRepository<User, Long> {}");
        System.out.println("   ");
        System.out.println("   Mono<User> save(User user) { return repository.save(user); }");
        System.out.println("   Flux<User> findAll() { return repository.findAll(); }");

        System.out.println("\n3. Supported Databases:");
        System.out.println("   - PostgreSQL, MySQL, SQL Server");
        System.out.println("   - H2, MariaDB, Oracle");

        System.out.println("\n=== R2DBC Lab Complete ===");
    }
}