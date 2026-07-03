# Edge Cases & Pitfalls: Advanced Records

While Records eliminate a lot of boilerplate, their strict semantics introduce new pitfalls, particularly when interacting with legacy frameworks, mutable data, and reflection.

## 1. The Mutable Component Leak
*   **The Scenario**: You define a Record with a mutable component, such as an array or a `java.util.Date`.
    ```java
    public record Document(String title, byte[] content) {}
    ```
*   **The Pitfall**: Records guarantee that the *reference* is final, but they do not guarantee deep immutability. A caller can do `doc.content()[0] = 0;`, modifying the internal state of your Record.
*   **Mitigation**: You must use the compact constructor to make a defensive copy on the way in, and override the accessor to make a defensive copy on the way out.
    ```java
    public record Document(String title, byte[] content) {
        public Document { content = content.clone(); } // Copy on way in
        @Override public byte[] content() { return content.clone(); } // Copy on way out
    }
    ```

## 2. JPA / Hibernate Entity Incompatibility
*   **The Scenario**: You want to use a Record as a JPA `@Entity` to map to a database table.
*   **The Pitfall**: This will fail. JPA (and Hibernate) strictly require entities to have a no-argument constructor and mutable setter methods (or at least non-final fields so reflection can inject values). Records have no no-arg constructor (by default) and all fields are strictly `final`.
*   **Mitigation**: You cannot use Records for JPA Entities. You *can*, however, use Records for JPA Projections (DTOs) to read data out of the database efficiently: `SELECT new com.example.UserDTO(u.name, u.age) FROM User u`.

## 3. JSON Deserialization Issues (Jackson)
*   **The Scenario**: You use Jackson to deserialize a JSON payload into a Record.
*   **The Pitfall**: Older versions of Jackson (pre-2.12) required a no-argument constructor to instantiate the object before using setters or reflection to populate the fields. Because Records lack a no-arg constructor, older Jackson versions will throw an exception.
*   **Mitigation**: Ensure you are using a modern version of Jackson (2.12+), which has native support for delegating to the Record's canonical constructor. If using an older framework, you might have to annotate the canonical constructor with `@JsonCreator`.

## 4. The Canonical Constructor Normalization Trap
*   **The Scenario**: You write a compact constructor to normalize data (e.g., trimming a string). You also write a custom constructor.
    ```java
    public record User(String name) {
        public User { name = name.trim(); } // Normalization
        
        public User() { 
            this.name = " Default "; // Direct field assignment? NO!
        }
    }
    ```
*   **The Pitfall**: The custom constructor above will not compile. In a Record, *all* custom constructors MUST delegate to the canonical constructor using `this(...)`. This is a strict compiler rule to ensure that your normalization/validation logic in the compact constructor is *never bypassed*.
*   **Mitigation**: Always delegate: `public User() { this(" Default "); }`. The string will then correctly pass through the compact constructor and be trimmed.

## 5. Reflection and `setAccessible(true)`
*   **The Scenario**: A malicious actor or a poorly written framework attempts to use reflection to modify a field in your Record.
    ```java
    Field nameField = User.class.getDeclaredField("name");
    nameField.setAccessible(true);
    nameField.set(myUserRecord, "Hacked");
    ```
*   **The Pitfall**: In standard Java classes, this reflection hack works, bypassing encapsulation. However, the JVM has been specifically hardened against this for Records.
*   **Mitigation**: None needed! This is a feature. The JVM will throw an `IllegalAccessException: Cannot set a record component`. Records provide absolute guarantees about their state that cannot be subverted even by deep reflection.