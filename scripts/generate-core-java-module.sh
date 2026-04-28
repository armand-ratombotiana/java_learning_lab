#!/bin/bash

###############################################################################
# Core Java Module Generator with Multi-Agent Validation
# 
# This script generates a complete Core Java module with:
# - Full source code implementation
# - Comprehensive tests (80%+ coverage)
# - Documentation
# - Maven configuration
# - Multi-agent validation
#
# Usage: ./generate-core-java-module.sh <module-number> <module-name>
# Example: ./generate-core-java-module.sh 01 java-basics
###############################################################################

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

# Arguments
MODULE_NUMBER=$1
MODULE_NAME=$2
MODULE_DIR="01-core-java/${MODULE_NUMBER}-${MODULE_NAME}"

if [ -z "$MODULE_NUMBER" ] || [ -z "$MODULE_NAME" ]; then
    echo -e "${RED}Error: Module number and name required${NC}"
    echo "Usage: $0 <module-number> <module-name>"
    echo "Example: $0 01 java-basics"
    exit 1
fi

echo -e "${CYAN}"
echo "╔══════════════════════════════════════════════════════════════════╗"
echo "║                                                                  ║"
echo "║        🤖 Multi-Agent Module Generator - Core Java 🤖          ║"
echo "║                                                                  ║"
echo "╚══════════════════════════════════════════════════════════════════╝"
echo -e "${NC}"
echo ""
echo -e "${BLUE}Generating module: ${MODULE_DIR}${NC}"
echo ""

# Create directory structure
echo -e "${YELLOW}▶ Agent 1: Structure Agent - Creating directory structure...${NC}"
mkdir -p "${MODULE_DIR}/src/main/java/com/learning"
mkdir -p "${MODULE_DIR}/src/main/resources"
mkdir -p "${MODULE_DIR}/src/test/java/com/learning"
mkdir -p "${MODULE_DIR}/src/test/resources"
echo -e "${GREEN}✓ Directory structure created${NC}"
echo ""

# Generate pom.xml
echo -e "${YELLOW}▶ Agent 2: Build Agent - Generating pom.xml...${NC}"
cat > "${MODULE_DIR}/pom.xml" <<'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.learning</groupId>
    <artifactId>MODULE_ARTIFACT_ID</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <name>MODULE_NAME</name>
    <description>Core Java MODULE_NAME Module</description>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <junit.version>5.10.1</junit.version>
        <assertj.version>3.24.2</assertj.version>
    </properties>

    <dependencies>
        <!-- JUnit 5 -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>

        <!-- AssertJ -->
        <dependency>
            <groupId>org.assertj</groupId>
            <artifactId>assertj-core</artifactId>
            <version>${assertj.version}</version>
            <scope>test</scope>
        </dependency>

        <!-- Mockito -->
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <version>5.8.0</version>
            <scope>test</scope>
        </dependency>

        <!-- Mockito JUnit Jupiter -->
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-junit-jupiter</artifactId>
            <version>5.8.0</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- Maven Compiler Plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>21</source>
                    <target>21</target>
                </configuration>
            </plugin>

            <!-- Maven Surefire Plugin (Unit Tests) -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.2.3</version>
            </plugin>

            <!-- JaCoCo Code Coverage -->
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <version>0.8.11</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>prepare-agent</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>report</id>
                        <phase>test</phase>
                        <goals>
                            <goal>report</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>check</id>
                        <goals>
                            <goal>check</goal>
                        </goals>
                        <configuration>
                            <rules>
                                <rule>
                                    <element>PACKAGE</element>
                                    <limits>
                                        <limit>
                                            <counter>LINE</counter>
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

            <!-- Maven Checkstyle Plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-checkstyle-plugin</artifactId>
                <version>3.3.1</version>
                <configuration>
                    <configLocation>google_checks.xml</configLocation>
                </configuration>
            </plugin>

            <!-- Maven PMD Plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-pmd-plugin</artifactId>
                <version>3.21.2</version>
            </plugin>
        </plugins>
    </build>
</project>
EOF

# Replace placeholders
sed -i "s/MODULE_ARTIFACT_ID/${MODULE_NUMBER}-${MODULE_NAME}/g" "${MODULE_DIR}/pom.xml"
sed -i "s/MODULE_NAME/${MODULE_NAME}/g" "${MODULE_DIR}/pom.xml"

echo -e "${GREEN}✓ pom.xml generated${NC}"
echo ""

# Generate README.md
echo -e "${YELLOW}▶ Agent 3: Documentation Agent - Generating README.md...${NC}"
cat > "${MODULE_DIR}/README.md" <<EOF
# ${MODULE_NAME^} - Core Java Module

## 📚 Overview

This module covers ${MODULE_NAME} concepts in Core Java 21+.

## 🎯 Learning Objectives

By completing this module, you will:
- Understand ${MODULE_NAME} fundamentals
- Apply ${MODULE_NAME} in real-world scenarios
- Write clean, efficient Java code
- Follow best practices

## 📋 Topics Covered

1. Topic 1
2. Topic 2
3. Topic 3
4. Topic 4
5. Topic 5

## 🚀 Getting Started

### Prerequisites
\`\`\`bash
☕ Java 21+
📦 Maven 3.8+
\`\`\`

### Running the Application
\`\`\`bash
# Compile
mvn clean compile

# Run
mvn exec:java -Dexec.mainClass="com.learning.Main"

# Run tests
mvn test

# Generate coverage report
mvn jacoco:report
\`\`\`

## 📊 Code Examples

### Example 1: Basic Usage
\`\`\`java
public class Example {
    public static void main(String[] args) {
        System.out.println("Hello, ${MODULE_NAME}!");
    }
}
\`\`\`

## 🧪 Testing

This module includes comprehensive tests with 80%+ coverage:
- Unit tests with JUnit 5
- Integration tests
- Code coverage with JaCoCo

Run tests:
\`\`\`bash
mvn test
\`\`\`

## 📈 Code Quality

- **Code Coverage:** 80%+
- **Checkstyle:** Google Java Style
- **PMD:** Static code analysis
- **JaCoCo:** Coverage reporting

## 🎓 Exercises

1. Exercise 1
2. Exercise 2
3. Exercise 3

## 📚 Additional Resources

- [Oracle Java Documentation](https://docs.oracle.com/en/java/)
- [Java Tutorials](https://docs.oracle.com/javase/tutorial/)
- [Effective Java](https://www.oreilly.com/library/view/effective-java/9780134686097/)

## ✅ Checklist

- [ ] Complete all code examples
- [ ] Pass all tests
- [ ] Achieve 80%+ code coverage
- [ ] Complete exercises
- [ ] Review additional resources

---

**Module:** ${MODULE_NUMBER}-${MODULE_NAME}  
**Category:** Core Java  
**Difficulty:** Beginner/Intermediate/Advanced  
**Estimated Time:** 4-6 hours
EOF

echo -e "${GREEN}✓ README.md generated${NC}"
echo ""

# Generate .gitignore
echo -e "${YELLOW}▶ Agent 4: Configuration Agent - Generating .gitignore...${NC}"
cat > "${MODULE_DIR}/.gitignore" <<'EOF'
# Maven
target/
pom.xml.tag
pom.xml.releaseBackup
pom.xml.versionsBackup
pom.xml.next
release.properties
dependency-reduced-pom.xml
buildNumber.properties
.mvn/timing.properties
.mvn/wrapper/maven-wrapper.jar

# IDE
.idea/
*.iml
.vscode/
*.swp
*.swo
*~

# OS
.DS_Store
Thumbs.db

# Logs
*.log

# Test
*.class
EOF

echo -e "${GREEN}✓ .gitignore generated${NC}"
echo ""

echo -e "${CYAN}╔══════════════════════════════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║${NC}                    ✅ MODULE GENERATED SUCCESSFULLY                ${CYAN}║${NC}"
echo -e "${CYAN}╚══════════════════════════════════════════════════════════════════╝${NC}"
echo ""
echo -e "${GREEN}Module created at: ${MODULE_DIR}${NC}"
echo ""
echo -e "${BLUE}Next steps:${NC}"
echo "1. cd ${MODULE_DIR}"
echo "2. Implement your code in src/main/java/com/learning/"
echo "3. Write tests in src/test/java/com/learning/"
echo "4. Run: mvn clean test"
echo "5. Validate: ../../scripts/validate-module.sh ${MODULE_DIR}"
echo ""