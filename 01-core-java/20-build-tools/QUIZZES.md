# Module 20: Build Tools (Maven & Gradle) - Quizzes

---

## Q1: Maven Lifecycles
In Apache Maven, what happens when you execute the command `mvn install`?

A) Only the install phase is executed.
B) Maven executes all phases in the default lifecycle up to and including the install phase (e.g., compile, test, package, verify, install).
C) Maven downloads the internet.
D) It cleans the `target` directory and then installs.

**Answer**: B
**Explanation**: Executing a phase in a Maven lifecycle triggers all preceding phases within that lifecycle automatically.

---

## Q2: Dependency Management
How does Maven uniquely identify a library or artifact?

A) By its file name
B) By a combination of `groupId`, `artifactId`, and `version` (GAV coordinates)
C) By its URL
D) By the package name

**Answer**: B
**Explanation**: The combination of `groupId`, `artifactId`, and `version` forms the unique coordinates (GAV) for any artifact in Maven repositories.

---

## Q3: Gradle vs Maven
Which of the following is a primary characteristic that distinguishes Gradle from Maven?

A) Gradle uses XML for configuration.
B) Maven supports incremental builds better than Gradle.
C) Gradle uses a Groovy or Kotlin DSL and provides highly optimized incremental build and caching features.
D) Maven is strictly used for Android development.

**Answer**: C
**Explanation**: Gradle uses a domain-specific language (DSL) based on Groovy or Kotlin instead of XML, and is widely known for its performance optimizations like the build cache and incremental builds.