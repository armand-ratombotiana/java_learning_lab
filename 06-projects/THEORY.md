# Java Projects Theory

## 1. Maven Fundamentals

### Maven Lifecycle

Maven defines a standard build lifecycle:

```
validate → compile → test → package → integration-test → verify → install → deploy
```

- **validate**: Validate project structure
- **compile**: Compile source code
- **test**: Run unit tests
- **package**: Package into JAR/WAR
- **integration-test**: Run integration tests
- **verify**: Run verification checks
- **install**: Install to local repository
- **deploy**: Deploy to remote repository

### Project Structure

```
project/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/App.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       ├── java/
│       │   └── com/example/AppTest.java
│       └── resources/
│           └── test.yml
└── target/
```

### POM Configuration

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.example</groupId>
    <artifactId>my-project</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <name>My Project</name>
    <description>A sample project</description>
    
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.10.0</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

## 2. Gradle Fundamentals

### Build Script Structure

```groovy
plugins {
    id 'java'
    id 'application'
}

group = 'com.example'
version = '1.0.0'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.apache.commons:commons-lang3:3.12.0'
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.0'
}

application {
    mainClass = 'com.example.App'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

### Gradle Tasks

```bash
gradle build        # Build project
gradle test         # Run tests
gradle run          # Run application
gradle clean        # Clean build
gradle dependencies # List dependencies
gradle tasks        # List available tasks
```

### Gradle Wrapper

```bash
gradle wrapper --gradle-version=8.5
```

Generates:
- `gradlew` (Unix)
- `gradlew.bat` (Windows)
- `gradle/wrapper/gradle-wrapper.jar`

## 3. Multi-module Projects

### Maven Multi-module Structure

```
parent/
├── pom.xml
├── module-a/
│   └── pom.xml
└── module-b/
    └── pom.xml
```

### Parent POM

```xml
<project>
    <groupId>com.example</groupId>
    <artifactId>parent</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>
    
    <modules>
        <module>module-a</module>
        <module>module-b</module>
    </modules>
    
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>com.example</groupId>
                <artifactId>module-a</artifactId>
                <version>${project.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

### Child Module POM

```xml
<project>
    <parent>
        <groupId>com.example</groupId>
        <artifactId>parent</artifactId>
        <version>1.0.0</version>
    </parent>
    
    <artifactId>module-a</artifactId>
    
    <dependencies>
        <dependency>
            <groupId>com.example</groupId>
            <artifactId>module-b</artifactId>
        </dependency>
    </dependencies>
</project>
```

## 4. Dependency Management

### Dependency Scopes

| Scope | Description | Usage |
|-------|-------------|-------|
| compile | Default - available in all classpaths | Production code |
| provided | Available at compile, not packaged | APIs expected at runtime |
| runtime | Not in compile, available at runtime | Implementations |
| test | Only for test compilation/execution | JUnit, Mockito |
| system | Explicit system path | Rarely used |

### Dependency Conflicts

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>library</artifactId>
    <version>2.0.0</version>
    <exclusions>
        <exclusion>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

### Version Management

```xml
<properties>
    <spring.version>6.1.0</spring.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-core</artifactId>
        <version>${spring.version}</version>
    </dependency>
</dependencies>
```

## 5. Build Optimization

### Incremental Compilation

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <useIncrementalCompilation>true</useIncrementalCompilation>
    </configuration>
</plugin>
```

### Parallel Execution

```bash
mvn -T 4 clean install  # Use 4 threads
mvn -T 1C clean install # 1 thread per core
```

### Build Cache

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <useBuildCache>true</useBuildCache>
    </configuration>
</plugin>
```

## 6. Testing Best Practices

### Test Structure

```
src/test/java/
└── com/example/
    └── service/
        ├── UserServiceTest.java        # Unit tests
        └── UserServiceIntegrationTest.java  # Integration tests
```

### Test Organization

```java
class MyServiceTest {
    
    @BeforeEach
    void setUp() {
        // Setup before each test
    }
    
    @Test
    void testMethod_Scenario_ExpectedResult() {
        // Test implementation
    }
    
    @ParameterizedTest
    @CsvSource({
        "input1, expected1",
        "input2, expected2"
    })
    void testWithParams(String input, String expected) {
        // Parameterized test
    }
    
    @Test
    void testException() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.method(null);
        });
    }
}
```

### Test Configuration

```java
@ExtendWith(MockitoExtension.class)
class ServiceTest {
    @Mock
    private Repository repository;
    
    @InjectMocks
    private Service service;
    
    @Test
    void test() {
        when(repository.find()).thenReturn(Optional.of(entity));
        Result result = service.process();
        assertNotNull(result);
    }
}
```

## 7. Project Best Practices

### Code Organization

```
src/main/java/com/example/{module}/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── service/         # Business logic
├── repository/      # Data access
├── model/           # Domain models
├── dto/             # Data transfer objects
└── exception/       # Custom exceptions
```

### Naming Conventions

- Classes: `PascalCase` (UserService, OrderController)
- Methods: `camelCase` (findById, processOrder)
- Variables: `camelCase` (userName, orderList)
- Constants: `UPPER_SNAKE_CASE` (MAX_RETRY_COUNT)
- Packages: `lowercase` (com.example.service)

### Documentation Standards

```java
/**
 * Processes user orders and updates inventory.
 * 
 * @param orderId the unique identifier of the order
 * @return the processed order with status
 * @throws OrderNotFoundException if order doesn't exist
 * @throws InsufficientInventoryException if stock is unavailable
 */
public Order processOrder(Long orderId) {...}
```

## 8. Build Profiles

### Maven Profiles

```xml
<profiles>
    <profile>
        <id>dev</id>
        <properties>
            <env>development</env>
        </properties>
    </profile>
    <profile>
        <id>prod</id>
        <properties>
            <env>production</env>
        </properties>
    </profile>
</profiles>
```

### Activating Profiles

```bash
mvn package -Pprod
mvn package -Pdev -DskipTests
```

## 9. Plugin Development

### Custom Maven Plugin

```java
@Mojo(name = "custom-goal")
public class CustomMojo extends AbstractMojo {
    
    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;
    
    @Parameter(property = "message")
    private String message;
    
    public void execute() throws MojoExecutionException {
        getLog().info("Custom message: " + message);
    }
}
```

## 10. CI/CD Integration

### GitHub Actions

```yaml
name: Build
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
      - run: mvn clean verify
      - run: mvn package -DskipTests
```

## Summary

Key concepts for Java projects:
- Use Maven or Gradle for build automation
- Organize code using standard directory structure
- Implement multi-module projects for large applications
- Manage dependencies with appropriate scopes
- Write comprehensive unit and integration tests
- Use build profiles for environment-specific configuration
- Integrate with CI/CD systems for automated builds