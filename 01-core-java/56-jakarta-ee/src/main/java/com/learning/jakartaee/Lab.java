package com.learning.jakartaee;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class Lab {

    @interface Entity {
        String tableName() default "";
    }

    @interface Id {}

    @interface GeneratedValue {
        String strategy() default "AUTO";
    }

    @interface Column {
        String name() default "";
    }

    @interface Inject {}
    @interface Path {
        String value();
    }
    @interface GET {}
    @interface POST {}
    @interface Produces {
        String value() default "application/json";
    }

    @Entity(tableName = "users")
    record User(@Id @GeneratedValue Long id, @Column(name = "user_name") String name, String email) {}

    static class EntityManager {
        private final Map<Class<?>, List<Object>> database = new ConcurrentHashMap<>();

        void persist(Object entity) {
            database.computeIfAbsent(entity.getClass(), k -> new CopyOnWriteArrayList<>()).add(entity);
        }

        @SuppressWarnings("unchecked")
        <T> List<T> findAll(Class<T> type) {
            return (List<T>) database.getOrDefault(type, List.of());
        }

        <T> long count(Class<T> type) { return findAll(type).size(); }
    }

    @Path("/api/users")
    static class UserResource {
        private final EntityManager em = new EntityManager();

        @GET
        @Produces("application/json")
        List<User> getUsers() {
            return em.findAll(User.class);
        }

        @POST
        User createUser(String name, String email) {
            var user = new User((long) (em.count(User.class) + 1), name, email);
            em.persist(user);
            return user;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Jakarta EE Lab ===\n");

        jakartaEeOverview();
        dependencyInjection();
        jpaExample();
        restApiExample();
        cdiEvents();
        ejbConcepts();
    }

    static void jakartaEeOverview() {
        System.out.println("--- Jakarta EE Overview ---");
        System.out.println("""
  Jakarta EE (formerly Java EE) = enterprise Java standard
  Key specifications:
  - Jakarta Servlet: HTTP request handling
  - Jakarta Faces (JSF): component-based web UI
  - Jakarta REST (JAX-RS): RESTful web services
  - Jakarta Persistence (JPA): ORM
  - Jakarta CDI: dependency injection
  - Jakarta EJB: distributed components
  - Jakarta Messaging (JMS): message queues
  - Jakarta Validation: bean validation

  Implementations: GlassFish, WildFly, Payara, TomEE
  Spring Boot is NOT Jakarta EE (uses subset of specs)
    """);
    }

    static void dependencyInjection() {
        System.out.println("\n--- CDI Dependency Injection ---");
        System.out.println("""
  @Inject: inject dependency (field, constructor, setter)
  @Named: named bean (alternative to @Component)
  @ApplicationScoped: singleton per application
  @RequestScoped: per HTTP request
  @SessionScoped: per HTTP session
  @Dependent: new instance per injection point
  @Produces: factory method
  @Disposes: cleanup callback

  Comparison:
  Spring:  @Autowired, @Component, @Scope
  Jakarta: @Inject, @Named, @javax.enterprise.context.*
    """);
    }

    static void jpaExample() {
        System.out.println("\n--- Jakarta Persistence (JPA) ---");
        var em = new EntityManager();

        em.persist(new User(1L, "Alice", "alice@test.com"));
        em.persist(new User(2L, "Bob", "bob@test.com"));

        System.out.println("  Users in database:");
        em.findAll(User.class).forEach(u -> System.out.println("    " + u));

        System.out.println("""
  JPA annotations:
  @Entity, @Table, @Id, @GeneratedValue
  @Column, @ManyToOne, @OneToMany, @JoinColumn
  @Cacheable (2nd level cache)

  EntityManager:
  persist() - make managed
  find() - load by PK
  createQuery() - JPQL
  merge() - update detached
  remove() - delete

  Relationships: LAZY vs EAGER loading
    """);
    }

    static void restApiExample() {
        System.out.println("\n--- Jakarta REST (JAX-RS) ---");
        var resource = new UserResource();

        resource.createUser("Alice", "alice@test.com");
        resource.createUser("Bob", "bob@test.com");

        System.out.println("  GET /api/users ->");
        resource.getUsers().forEach(u -> System.out.println("    " + u));

        System.out.println("""
  JAX-RS annotations:
  @Path, @GET, @POST, @PUT, @DELETE
  @Produces, @Consumes
  @PathParam, @QueryParam, @HeaderParam
  @Context (inject request/response/uri)

  Return types:
  Response (status + entity)
  Entity<T> (generic response)
  StreamingOutput (large responses)
    """);
    }

    static void cdiEvents() {
        System.out.println("\n--- CDI Events ---");
        System.out.println("""
  Event-driven communication between beans:
  @Inject Event<OrderEvent> orderEvent;

  Firing:
  orderEvent.fire(new OrderEvent(123));

  Observing:
  void onOrder(@Observes OrderEvent event) { ... }

  Event qualifiers:
  @Any, @Named("eventType")
  Fire async: Event.fireAsync()

  Transactional observers:
  @Observes(during = TransactionPhase.AFTER_SUCCESS)
  Executes only if transaction commits
    """);
    }

    static void ejbConcepts() {
        System.out.println("\n--- EJB Concepts ---");
        System.out.println("""
  Session Beans:
  @Stateless - no conversational state (pooled)
  @Stateful - conversational state (per client)
  @Singleton - shared across all clients

  @Asynchronous: async method invocation
  @Schedule: timer-based execution (cron-like)
  @TransactionAttribute: declarative TX management

  EJB vs Spring:
  EJB: Container-managed, distributed (RMI/IIOP)
  Spring: POJO-based, simpler programming model

  Modern Jakarta EE prefers CDI @ApplicationScoped
  EJBs are less commonly used in new projects
    """);
    }
}
