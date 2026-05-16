# Java Projects Exercises

## Exercise 1: Maven Project Setup

Create a new Maven project with proper structure.

**Requirements:**
- GroupId: `com.learning.lab`
- ArtifactId: `inventory-system`
- Java version: 17
- Packaging: JAR

**Solution:**
```bash
mvn archetype:generate -DgroupId=com.learning.lab -DartifactId=inventory-system -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
```

Or create manually:
```xml
<!-- pom.xml -->
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning.lab</groupId>
    <artifactId>inventory-system</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
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
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.3.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <mainClass>com.learning.lab.App</mainClass>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

## Exercise 2: Multi-module Project

Create a multi-module project with parent and two child modules.

**Requirements:**
- Parent module: `parent-project`
- Child modules: `core` and `service`
- Core module contains shared utilities
- Service module depends on core

**Structure:**
```
parent-project/
├── pom.xml (packaging=pom)
├── core/
│   └── pom.xml
└── service/
    └── pom.xml
```

**Parent pom.xml:**
```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning.lab</groupId>
    <artifactId>parent-project</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>
    
    <modules>
        <module>core</module>
        <module>service</module>
    </modules>
    
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
    </properties>
</project>
```

**core/pom.xml:**
```xml
<project>
    <parent>
        <groupId>com.learning.lab</groupId>
        <artifactId>parent-project</artifactId>
        <version>1.0.0</version>
    </parent>
    <artifactId>core</artifactId>
</project>
```

**service/pom.xml:**
```xml
<project>
    <parent>
        <groupId>com.learning.lab</groupId>
        <artifactId>parent-project</artifactId>
        <version>1.0.0</version>
    </parent>
    <artifactId>service</artifactId>
    <dependencies>
        <dependency>
            <groupId>com.learning.lab</groupId>
            <artifactId>core</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</project>
```

## Exercise 3: Dependency Management

Configure dependencies with appropriate scopes and handle version conflicts.

**Requirements:**
- Main library at compile scope
- Database driver at runtime scope
- Testing framework at test scope
- Exclude conflicting transitive dependency

**Solution:**
```xml
<dependencies>
    <!-- Compile scope (default) -->
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
        <version>3.12.0</version>
    </dependency>
    
    <!-- Runtime scope - available but not compiled -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>2.2.224</version>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Test scope -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>
    
    <!-- With exclusions -->
    <dependency>
        <groupId>com.example</groupId>
        <artifactId>library</artifactId>
        <version>1.0.0</version>
        <exclusions>
            <exclusion>
                <groupId>org.slf4j</groupId>
                <artifactId>slf4j-api</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
</dependencies>
```

## Exercise 4: Build Profiles

Configure environment-specific builds using profiles.

**Requirements:**
- Development profile with logging enabled
- Production profile with optimized settings
- Test profile for running tests

**Solution:**
```xml
<profiles>
    <profile>
        <id>dev</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <properties>
            <env>development</env>
            <log.level>DEBUG</log.level>
        </properties>
    </profile>
    
    <profile>
        <id>prod</id>
        <properties>
            <env>production</env>
            <log.level>WARN</log.level>
            <optimize>true</optimize>
        </properties>
    </profile>
    
    <profile>
        <id>test</id>
        <properties>
            <env>test</env>
        </properties>
    </profile>
</profiles>
```

**application.yml:**
```yaml
logging:
  level:
    root: ${log.level:INFO}
```

**Usage:**
```bash
mvn clean package -Pprod
```

## Exercise 5: Custom Plugin Usage

Configure common build plugins for a production-ready project.

**Requirements:**
- Source formatting with spotless
- Code analysis with spotbugs
- JAR with dependencies
- Source JAR generation

**Solution:**
```xml
<build>
    <plugins>
        <!-- Format code -->
        <plugin>
            <groupId>com.diffplug.spotless</groupId>
            <artifactId>spotless-maven-plugin</artifactId>
            <version>6.21.0</version>
            <executions>
                <execution>
                    <goals>
                        <goal>check</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
        
        <!-- Analyze code -->
        <plugin>
            <groupId>com.github.spotbugs</groupId>
            <artifactId>spotbugs-maven-plugin</artifactId>
            <version>4.7.3.6</version>
            <executions>
                <execution>
                    <goals>
                        <goal>check</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
        
        <!-- Fat JAR -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>3.5.1</version>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                    <configuration>
                        <transformers>
                            <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                <mainClass>com.learning.lab.App</mainClass>
                            </transformer>
                        </transformers>
                    </configuration>
                </execution>
            </executions>
        </plugin>
        
        <!-- Source JAR -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-source-plugin</artifactId>
            <version>3.3.0</version>
            <executions>
                <execution>
                    <id>attach-sources</id>
                    <goals>
                        <goal>jar-no-fork</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

## Exercise 6: Unit Testing

Write comprehensive unit tests with mocking.

**Requirements:**
- Test service class with mocked repository
- Verify method calls
- Test exception scenarios

**Solution:**
```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    private UserService userService;
    
    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }
    
    @Test
    void findById_WhenUserExists_ReturnsUser() {
        User expectedUser = new User(1L, "john");
        when(userRepository.findById(1L)).thenReturn(Optional.of(expectedUser));
        
        User result = userService.findById(1L);
        
        assertNotNull(result);
        assertEquals("john", result.getUsername());
        verify(userRepository).findById(1L);
    }
    
    @Test
    void findById_WhenUserNotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(UserNotFoundException.class, () -> {
            userService.findById(1L);
        });
    }
    
    @Test
    void save_WhenValidUser_ReturnsSavedUser() {
        User newUser = new User(null, "jane");
        User savedUser = new User(1L, "jane");
        when(userRepository.save(newUser)).thenReturn(savedUser);
        
        User result = userService.save(newUser);
        
        assertNotNull(result.getId());
        assertEquals("jane", result.getUsername());
        verify(userRepository).save(newUser);
    }
    
    @Test
    void delete_WhenUserExists_CallsRepository() {
        doNothing().when(userRepository).deleteById(1L);
        
        userService.delete(1L);
        
        verify(userRepository).deleteById(1L);
    }
}
```

## Exercise 7: Integration Testing

Create integration test with test configuration.

**Requirements:**
- Use test-specific application.yml
- Test repository layer
- Clean up data after tests

**Solution:**
```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryIntegrationTest {
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void save_PersistsUser() {
        User user = new User();
        user.setUsername("testuser");
        
        User saved = userRepository.save(user);
        
        assertNotNull(saved.getId());
        assertEquals("testuser", saved.getUsername());
    }
    
    @Test
    void findByUsername_WhenExists_ReturnsUser() {
        User user = new User();
        user.setUsername("searchable");
        userRepository.save(user);
        
        Optional<User> found = userRepository.findByUsername("searchable");
        
        assertTrue(found.isPresent());
        assertEquals("searchable", found.get().getUsername());
    }
    
    @Test
    void findByActive_ReturnsActiveUsers() {
        User active = new User();
        active.setUsername("active");
        active.setActive(true);
        userRepository.save(active);
        
        List<User> activeUsers = userRepository.findByActiveTrue();
        
        assertFalse(activeUsers.isEmpty());
    }
}
```

**application-test.yml:**
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```