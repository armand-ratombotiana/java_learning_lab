# Module 34: Code Quality & Static Analysis - Mini Project

**Project Name**: Automated Quality Gates Pipeline  
**Difficulty Level**: Intermediate  
**Estimated Time**: 2-3 hours

---

## 🎯 Objective
Configure a Maven project to automatically enforce code quality standards, generate code coverage reports, and fail the build if minimum quality thresholds are not met using Checkstyle, SpotBugs, and JaCoCo.

## 📝 Requirements

### Core Features

1. **Project Setup**:
   - Create a simple Maven project containing a few Java classes with intentional flaws (e.g., unused variables, missing Javadoc, poor formatting).
   - Add a few JUnit tests, but intentionally leave some methods untested to simulate poor code coverage.

2. **Checkstyle Integration**:
   - Add the `maven-checkstyle-plugin` to your `pom.xml`.
   - Configure it to use the `google_checks.xml` standard.
   - Set the plugin to execute during the `validate` or `verify` phase.
   - Configure it to **fail the build** (`<failsOnError>true</failsOnError>`) if there are formatting violations.

3. **SpotBugs Integration**:
   - Add the `spotbugs-maven-plugin` to your `pom.xml`.
   - Set it to execute during the `verify` phase.
   - Configure it to fail the build if high-priority bugs are detected.

4. **JaCoCo Code Coverage**:
   - Add the `jacoco-maven-plugin` to your `pom.xml`.
   - Configure the `prepare-agent` goal to attach the JaCoCo agent.
   - Configure the `check` goal during the `verify` phase. Set a rule that requires a minimum of **80% instruction coverage** and **80% branch coverage**.
   - Ensure the build fails if the coverage falls below this threshold.

5. **Execution & Fixing**:
   - Run `mvn clean verify`. The build should fail spectacularly.
   - Iteratively fix the Checkstyle warnings (formatting), SpotBugs errors, and write missing tests until `mvn clean verify` passes with a `BUILD SUCCESS`.

---

## 💡 Solution Blueprint

**`pom.xml` configurations**:

```xml
<build>
    <plugins>
        <!-- Checkstyle -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-checkstyle-plugin</artifactId>
            <version>3.3.0</version>
            <executions>
                <execution>
                    <phase>validate</phase>
                    <goals><goal>check</goal></goals>
                </execution>
            </executions>
            <configuration>
                <configLocation>google_checks.xml</configLocation>
                <failsOnError>true</failsOnError>
                <consoleOutput>true</consoleOutput>
            </configuration>
        </plugin>

        <!-- SpotBugs -->
        <plugin>
            <groupId>com.github.spotbugs</groupId>
            <artifactId>spotbugs-maven-plugin</artifactId>
            <version>4.7.3.5</version>
            <executions>
                <execution>
                    <phase>verify</phase>
                    <goals><goal>check</goal></goals>
                </execution>
            </executions>
        </plugin>

        <!-- JaCoCo -->
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.10</version>
            <executions>
                <execution>
                    <goals><goal>prepare-agent</goal></goals>
                </execution>
                <execution>
                    <id>check</id>
                    <goals><goal>check</goal></goals>
                    <configuration>
                        <rules>
                            <rule>
                                <element>BUNDLE</element>
                                <limits>
                                    <limit>
                                        <counter>INSTRUCTION</counter>
                                        <value>COVEREDRATIO</value>
                                        <minimum>0.80</minimum>
                                    </limit>
                                </limits>
                            </rule>
                        </rules>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```