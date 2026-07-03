# Module 56: Jakarta EE - Mini Project

**Project Name**: Pure Jakarta REST API (No Spring)  
**Difficulty Level**: Advanced  
**Estimated Time**: 3 hours

---

## 🎯 Objective
Build a lightweight, purely standard-compliant enterprise web application without using Spring Boot. You will use JAX-RS for routing, CDI for Dependency Injection, and JPA for database access, deployed on an application server like WildFly or OpenLiberty.

## 📝 Requirements

### Core Features

1. **Project Configuration (`pom.xml`)**:
   - Create a Maven project with packaging type `war`.
   - Add the single `jakarta.jakartaee-api` dependency with scope `provided` (the app server provides the actual implementations at runtime).

2. **JPA Configuration (`persistence.xml`)**:
   - Create `src/main/resources/META-INF/persistence.xml`.
   - Define a persistence unit for an H2 or PostgreSQL database. Set the schema generation to `drop-and-create`.

3. **Domain & Data Access Layer**:
   - Create an `@Entity` class `Task` with `id` and `title`.
   - Create a `TaskRepository` class. Use CDI to inject the EntityManager: `@PersistenceContext EntityManager em;`.
   - Implement `@Transactional` methods for `save(Task t)` and `findAll()`.

4. **The REST Endpoint (JAX-RS)**:
   - Create a class extending `Application` annotated with `@ApplicationPath("/api")` to bootstrap JAX-RS.
   - Create a `TaskResource` class annotated with `@Path("/tasks")`.
   - Use CDI `@Inject` to inject the `TaskRepository`.
   - Implement `@GET` and `@POST` methods. Ensure they produce and consume `MediaType.APPLICATION_JSON`.

5. **Deployment**:
   - Download a Jakarta EE compatible server (e.g., WildFly).
   - Compile the project: `mvn clean package`.
   - Copy the generated `.war` file to the server's deployments folder and start the server.
   - Test the API using `curl` or Postman.

---

## 💡 Solution Blueprint

1. **JAX-RS Bootstrap**:
   ```java
   import jakarta.ws.rs.ApplicationPath;
   import jakarta.ws.rs.core.Application;

   @ApplicationPath("/api")
   public class RestApplication extends Application {
       // Activates JAX-RS automatically
   }
   ```

2. **The Repository (JPA + CDI)**:
   ```java
   import jakarta.enterprise.context.RequestScoped;
   import jakarta.persistence.EntityManager;
   import jakarta.persistence.PersistenceContext;
   import jakarta.transaction.Transactional;

   @RequestScoped
   public class TaskRepository {
       @PersistenceContext
       private EntityManager em;

       @Transactional
       public void create(Task task) {
           em.persist(task);
       }

       public List<Task> findAll() {
           return em.createQuery("SELECT t FROM Task t", Task.class).getResultList();
       }
   }
   ```

3. **The Resource (Controller)**:
   ```java
   import jakarta.inject.Inject;
   import jakarta.ws.rs.*;
   import jakarta.ws.rs.core.MediaType;

   @Path("/tasks")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   public class TaskResource {

       @Inject
       private TaskRepository repository;

       @GET
       public List<Task> getTasks() {
           return repository.findAll();
       }

       @POST
       public void addTask(Task task) {
           repository.create(task);
       }
   }
   ```