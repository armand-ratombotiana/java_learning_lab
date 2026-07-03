# Module 20: Build Tools (Maven & Gradle) - Deep Dive

**Difficulty Level**: Intermediate  
**Prerequisites**: Modules 01-19  
**Estimated Reading Time**: 45-60 minutes  

---

## 📚 Table of Contents

1. [Introduction to Build Tools](#intro)
2. [Apache Maven Fundamentals](#maven)
3. [Maven Lifecycles and Phases](#maven-lifecycle)
4. [Gradle Fundamentals](#gradle)
5. [Comparing Maven and Gradle](#comparison)

---

## 1. Introduction to Build Tools <a name="intro"></a>
Build tools automate the creation of executable applications from source code. They handle compilation, linking, packaging, and dependency management. In the Java ecosystem, Maven and Gradle are the industry standards.

---

## 2. Apache Maven Fundamentals <a name="maven"></a>
Maven uses a declarative approach via an XML file named `pom.xml` (Project Object Model). It strictly enforces a standard project structure (e.g., `src/main/java`, `src/test/java`).

```xml
<!-- Example pom.xml snippet -->
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>my-app</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <dependencies>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>5.10.0</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

---

## 3. Maven Lifecycles and Phases <a name="maven-lifecycle"></a>
Maven defines three built-in lifecycles:
1. **default**: Handles project deployment (`validate`, `compile`, `test`, `package`, `verify`, `install`, `deploy`).
2. **clean**: Handles project cleaning (`clean`).
3. **site**: Handles the creation of project site documentation.

Executing a phase (like `mvn install`) automatically executes all preceding phases in that lifecycle.

---

## 4. Gradle Fundamentals <a name="gradle"></a>
Gradle is a build automation tool that uses a Groovy or Kotlin-based Domain Specific Language (DSL) rather than XML. It is highly customizable and often faster due to its build cache and incremental builds.

```groovy
// Example build.gradle snippet
plugins {
    id 'java'
}

group = 'com.example'
version = '1.0-SNAPSHOT'

repositories {
    mavenCentral()
}

dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.10.0'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.10.0'
}

test {
    useJUnitPlatform()
}
```

---

## 5. Comparing Maven and Gradle <a name="comparison"></a>
- **Maven**: Strict conventions, XML-based, predictable, easy to understand. Best for standard projects.
- **Gradle**: Highly flexible, script-based (Groovy/Kotlin), optimized for performance (caching, daemon). Best for large, complex builds (e.g., Android development).