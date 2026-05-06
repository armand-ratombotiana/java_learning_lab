# Lab 00: Environment Setup

## 📋 Lab Header

| Aspect | Details |
|--------|---------|
| **Difficulty** | Beginner |
| **Estimated Time** | 2 hours |
| **Real-World Context** | Setting up a professional Java development environment |
| **Prerequisites** | None |
| **Learning Type** | Setup & Configuration |

## 🎯 Learning Objectives

By completing this lab, you will be able to:

1. **Install and configure Java Development Kit (JDK)** on your system
2. **Set up Maven or Gradle** as your build tool
3. **Configure an Integrated Development Environment (IDE)** for Java development
4. **Verify your installation** and create your first Java project
5. **Understand the Java development workflow** and project structure

## 📚 Prerequisites

- A computer with Windows, macOS, or Linux
- Administrator access to install software
- Internet connection for downloading tools
- Basic command-line knowledge

## 🧠 Concept Theory

### What is Java Development?

Java development involves writing, compiling, and running Java code. To do this effectively, you need:

1. **Java Development Kit (JDK)**: Contains the compiler and runtime
2. **Build Tool**: Automates compilation, testing, and packaging
3. **IDE**: Provides a user-friendly interface for coding
4. **Version Control**: Tracks changes to your code

### Key Components

#### 1. Java Development Kit (JDK)

The JDK is the foundation of Java development. It includes:

- **javac**: Java compiler (converts .java files to .class files)
- **java**: Java runtime (executes compiled code)
- **javadoc**: Documentation generator
- **jdb**: Debugger
- **Standard Library**: Pre-built classes and utilities

**Versions**:
- **LTS (Long-Term Support)**: Java 8, 11, 17, 21 (recommended for production)
- **Current**: Latest version with newest features

**For this course**: Use **Java 17 LTS** or later

#### 2. Build Tools

Build tools automate the development process:

**Maven**:
- Uses XML configuration (pom.xml)
- Follows convention over configuration
- Large ecosystem of plugins
- Good for beginners

**Gradle**:
- Uses Groovy/Kotlin DSL
- More flexible and powerful
- Faster builds with caching
- Good for complex projects

**For this course**: We'll use **Maven** primarily, with Gradle templates available

#### 3. Integrated Development Environment (IDE)

An IDE provides:
- Code editor with syntax highlighting
- Debugging tools
- Build integration
- Testing support
- Refactoring tools

**Popular IDEs**:
- **IntelliJ IDEA** (recommended, free Community Edition)
- **Eclipse** (free, open-source)
- **VS Code** (lightweight, with extensions)
- **NetBeans** (free, open-source)

#### 4. Version Control

Git allows you to:
- Track code changes
- Collaborate with others
- Manage different versions
- Revert to previous states

### Development Workflow

```
┌─────────────────────────────────────────────────────┐
│ 1. Write Code (.java files)                         │
│    ↓                                                 │
│ 2. Compile (javac or Maven/Gradle)                 │
│    ↓                                                 │
│ 3. Create .class files (bytecode)                  │
│    ↓                                                 │
│ 4. Run (java command or IDE)                       │
│    ↓                                                 │
│ 5. JVM executes bytecode                           │
│    ↓                                                 │
│ 6. Output/Results                                   │
└─────────────────────────────────────────────────────┘
```

## 💻 Step-by-Step Setup Guide

### Step 1: Install Java Development Kit (JDK)

#### Windows

1. **Download JDK 17 LTS**
   - Visit [Oracle JDK Download](https://www.oracle.com/java/technologies/downloads/)
   - Select "JDK 17 LTS"
   - Choose Windows x64 installer

2. **Run the installer**
   - Double-click the downloaded .exe file
   - Follow the installation wizard
   - Accept default installation path (usually `C:\Program Files\Java\jdk-17`)

3. **Verify installation**
   ```bash
   java -version
   javac -version
   ```
   
   Expected output:
   ```
   java version "17.0.x" 2021-09-14 LTS
   Java(TM) SE Runtime Environment (build 17.0.x+...)
   Java HotSpot(TM) 64-Bit Server VM (build 17.0.x+...)
   ```

#### macOS

1. **Using Homebrew (recommended)**
   ```bash
   # Install Homebrew if not already installed
   /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
   
   # Install JDK 17
   brew install openjdk@17
   ```

2. **Set JAVA_HOME**
   ```bash
   # Add to ~/.zshrc or ~/.bash_profile
   export JAVA_HOME=$(/usr/libexec/java_home -v 17)
   export PATH=$JAVA_HOME/bin:$PATH
   ```

3. **Verify installation**
   ```bash
   java -version
   javac -version
   ```

#### Linux (Ubuntu/Debian)

1. **Install JDK 17**
   ```bash
   sudo apt update
   sudo apt install openjdk-17-jdk
   ```

2. **Verify installation**
   ```bash
   java -version
   javac -version
   ```

### Step 2: Install Maven

#### Windows

1. **Download Maven**
   - Visit [Maven Download](https://maven.apache.org/download.cgi)
   - Download "Binary zip archive"

2. **Extract to a folder**
   - Extract to `C:\Program Files\Apache\maven` (or your preferred location)

3. **Set environment variables**
   - Open System Properties → Environment Variables
   - Add `MAVEN_HOME`: `C:\Program Files\Apache\maven`
   - Add to `PATH`: `%MAVEN_HOME%\bin`

4. **Verify installation**
   ```bash
   mvn -version
   ```

#### macOS

```bash
# Using Homebrew
brew install maven

# Verify
mvn -version
```

#### Linux

```bash
# Ubuntu/Debian
sudo apt install maven

# Verify
mvn -version
```

### Step 3: Install an IDE

#### IntelliJ IDEA Community Edition (Recommended)

1. **Download**
   - Visit [IntelliJ IDEA Download](https://www.jetbrains.com/idea/download/)
   - Download Community Edition

2. **Install**
   - Run the installer and follow the wizard
   - Accept default settings

3. **First Launch**
   - Open IntelliJ IDEA
   - Configure JDK: File → Project Structure → SDKs
   - Select the JDK 17 installation

#### VS Code

1. **Download**
   - Visit [VS Code Download](https://code.visualstudio.com/)

2. **Install Extensions**
   - Extension Pack for Java (Microsoft)
   - Maven for Java (Microsoft)

3. **Configure JDK**
   - Open Command Palette (Ctrl+Shift+P)
   - Search "Java: Configure Runtime"
   - Select JDK 17

### Step 4: Install Git

#### Windows

1. **Download**
   - Visit [Git Download](https://git-scm.com/download/win)

2. **Run installer**
   - Accept default settings
   - Choose "Use Git from Git Bash only" or "Use Git from the Windows Command Prompt"

3. **Verify**
   ```bash
   git --version
   ```

#### macOS

```bash
# Using Homebrew
brew install git

# Verify
git --version
```

#### Linux

```bash
# Ubuntu/Debian
sudo apt install git

# Verify
git --version
```

### Step 5: Create Your First Project

#### Using Maven

```bash
# Create a new Maven project
mvn archetype:generate \
  -DgroupId=com.learning \
  -DartifactId=hello-world \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DinteractiveMode=false

# Navigate to project
cd hello-world

# Build the project
mvn clean install

# Run the application
mvn exec:java -Dexec.mainClass="com.learning.App"
```

#### Using IDE (IntelliJ IDEA)

1. **Create New Project**
   - File → New → Project
   - Select "Maven"
   - Choose JDK 17
   - Click Next

2. **Configure Project**
   - GroupId: `com.learning`
   - ArtifactId: `hello-world`
   - Version: `1.0-SNAPSHOT`
   - Click Finish

3. **Create Main Class**
   - Right-click `src/main/java`
   - New → Java Class
   - Name: `HelloWorld`

4. **Write Code**
   ```java
   public class HelloWorld {
       public static void main(String[] args) {
           System.out.println("Hello, Java Master Lab!");
       }
   }
   ```

5. **Run**
   - Right-click the file
   - Select "Run 'HelloWorld.main()'"

## 🎨 Verification Project: Hello World Application

### Project Overview

Create a simple "Hello World" application to verify your setup is working correctly.

### Project Structure

```
hello-world/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/learning/
│   │           └── HelloWorld.java
│   └── test/
│       └── java/
│           └── com/learning/
│               └── HelloWorldTest.java
├── pom.xml
└── README.md
```

### Implementation

**HelloWorld.java**:
```java
package com.learning;

/**
 * Simple Hello World application to verify Java setup.
 */
public class HelloWorld {
    
    /**
     * Main method - entry point of the application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        System.out.println("Hello, Java Master Lab!");
        System.out.println("Java version: " + System.getProperty("java.version"));
        System.out.println("Setup successful!");
    }
    
    /**
     * Returns a greeting message.
     *
     * @param name the name to greet
     * @return greeting message
     */
    public static String greet(String name) {
        return "Hello, " + name + "!";
    }
}
```

**HelloWorldTest.java**:
```java
package com.learning;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for HelloWorld class.
 */
public class HelloWorldTest {
    
    @Test
    void testGreet() {
        String result = HelloWorld.greet("Java");
        assertEquals("Hello, Java!", result);
    }
    
    @Test
    void testGreetWithDifferentName() {
        String result = HelloWorld.greet("World");
        assertEquals("Hello, World!", result);
    }
}
```

**pom.xml**:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.learning</groupId>
    <artifactId>hello-world</artifactId>
    <version>1.0-SNAPSHOT</version>

    <name>Hello World</name>
    <description>Simple Hello World application</description>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <!-- JUnit 5 for testing -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>5.9.2</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-engine</artifactId>
            <version>5.9.2</version>
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
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>3.1.0</version>
                <configuration>
                    <mainClass>com.learning.HelloWorld</mainClass>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### Running the Project

```bash
# Compile
mvn clean compile

# Run tests
mvn test

# Run the application
mvn exec:java

# Expected output:
# Hello, Java Master Lab!
# Java version: 17.0.x
# Setup successful!
```

## 📝 Exercises

### Exercise 1: Verify Your Installation

**Objective**: Confirm all tools are properly installed

**Task Description**:
Create a checklist and verify each component:

**Acceptance Criteria**:
- [ ] Java 17+ is installed and accessible
- [ ] Maven is installed and accessible
- [ ] IDE is installed and configured
- [ ] Git is installed and accessible
- [ ] All commands return version information

**Verification Commands**:
```bash
java -version
javac -version
mvn -version
git --version
```

**Reflection Prompt**:
- Did you encounter any issues during installation?
- How would you troubleshoot if a tool wasn't found?
- What is the purpose of each tool in the development workflow?

### Exercise 2: Create a Maven Project

**Objective**: Practice creating a project from scratch

**Task Description**:
Create a new Maven project and add a simple class that prints your name.

**Acceptance Criteria**:
- [ ] Project created using Maven archetype
- [ ] Project structure follows Maven conventions
- [ ] Main class created and prints your name
- [ ] Project compiles without errors
- [ ] Application runs successfully

**Starter Code**:
```bash
mvn archetype:generate \
  -DgroupId=com.learning \
  -DartifactId=my-first-project \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DinteractiveMode=false
```

**Reflection Prompt**:
- What is the purpose of groupId and artifactId?
- Why does Maven use a standard directory structure?
- How does Maven help manage dependencies?

### Exercise 3: Configure Your IDE

**Objective**: Set up your IDE for Java development

**Task Description**:
Configure your IDE with the correct JDK and create a simple project.

**Acceptance Criteria**:
- [ ] IDE is installed and launched
- [ ] JDK 17 is configured in IDE settings
- [ ] New project created successfully
- [ ] Code completion works
- [ ] Project runs from IDE

**Reflection Prompt**:
- What features of your IDE will help you write better code?
- How does IDE integration improve the development workflow?
- What keyboard shortcuts will you use most frequently?

## 🧪 Quiz

### Question 1: What is the primary purpose of the JDK?

A) To run Java applications only  
B) To compile and run Java applications  
C) To manage project dependencies  
D) To provide an IDE for coding  

**Answer**: B) To compile and run Java applications

**Explanation**: The JDK (Java Development Kit) includes both the compiler (javac) and the runtime (java), allowing you to both compile and execute Java code.

### Question 2: Which of the following is NOT a responsibility of Maven?

A) Compiling source code  
B) Managing dependencies  
C) Running the application  
D) Providing syntax highlighting  

**Answer**: D) Providing syntax highlighting

**Explanation**: Maven is a build tool that handles compilation, dependency management, and running applications. Syntax highlighting is provided by IDEs, not build tools.

### Question 3: What does the JAVA_HOME environment variable point to?

A) The location of the Java compiler  
B) The location of the JDK installation  
C) The location of your Java projects  
D) The location of the Java documentation  

**Answer**: B) The location of the JDK installation

**Explanation**: JAVA_HOME is an environment variable that points to the root directory of your JDK installation, allowing tools to find Java executables.

### Question 4: What is the purpose of Git in Java development?

A) To compile Java code  
B) To manage project dependencies  
C) To track code changes and enable collaboration  
D) To run unit tests  

**Answer**: C) To track code changes and enable collaboration

**Explanation**: Git is a version control system that tracks changes to your code and enables collaboration with other developers.

### Question 5: Which file is used by Maven to configure a project?

A) build.gradle  
B) pom.xml  
C) settings.xml  
D) project.properties  

**Answer**: B) pom.xml

**Explanation**: pom.xml (Project Object Model) is the configuration file used by Maven to define project structure, dependencies, and build settings.

## 🚀 Advanced Challenge

### Challenge: Set Up a Complete Development Environment

**Difficulty**: Intermediate

**Objective**: Create a fully configured development environment with multiple tools

**Description**:
Set up a complete Java development environment including:
- JDK 17 LTS
- Maven with custom settings
- IDE with plugins
- Git repository
- Project template

**Requirements**:
- [ ] JDK 17 installed and JAVA_HOME set
- [ ] Maven installed with custom repository
- [ ] IDE configured with code style settings
- [ ] Git initialized with .gitignore
- [ ] Maven archetype customized

**Hints**:
1. Create a custom Maven settings.xml for your environment
2. Configure IDE code style to match project standards
3. Create a .gitignore file for Java projects
4. Set up Maven profiles for different environments

**Stretch Goals**:
- [ ] Create a Maven plugin for custom build steps
- [ ] Set up IDE debugging configuration
- [ ] Configure Maven for multi-module projects
- [ ] Create a build script for automation

## 🏆 Best Practices

### Environment Setup

1. **Use LTS Versions**
   - Java 17 LTS is stable and supported until 2026
   - Avoid using experimental versions for learning

2. **Organize Your Workspace**
   - Create a dedicated folder for all projects
   - Use consistent naming conventions
   - Keep tools in standard locations

3. **Document Your Setup**
   - Keep notes on your configuration
   - Document any custom settings
   - Create a setup guide for your team

### IDE Configuration

1. **Code Style**
   - Configure consistent indentation (4 spaces)
   - Set up code formatting rules
   - Enable automatic formatting on save

2. **Debugging**
   - Learn to use the debugger
   - Set breakpoints effectively
   - Use watch expressions

3. **Productivity**
   - Learn keyboard shortcuts
   - Configure code templates
   - Use refactoring tools

### Version Control

1. **Git Workflow**
   - Initialize git in all projects
   - Create meaningful commit messages
   - Use branches for features

2. **.gitignore**
   - Exclude build artifacts
   - Exclude IDE configuration
   - Exclude sensitive files

## 🔗 Next Steps

### Ready for Lab 01?

Now that your environment is set up, you're ready to start learning Java!

**Next Lab**: [Lab 01: Java Basics](../01-java-basics/README.md)

### Additional Setup Resources

- [Java Installation Guide](https://docs.oracle.com/en/java/javase/17/install/)
- [Maven Getting Started](https://maven.apache.org/guides/getting-started/)
- [IntelliJ IDEA Setup](https://www.jetbrains.com/help/idea/getting-started.html)
- [Git Basics](https://git-scm.com/book/en/v2/Getting-Started-Git-Basics)

## ✅ Completion Checklist

- [ ] Java 17+ installed and verified
- [ ] Maven installed and verified
- [ ] IDE installed and configured
- [ ] Git installed and verified
- [ ] Hello World project created and runs
- [ ] All exercises completed
- [ ] Quiz passed (80%+)
- [ ] Environment documented

---

**Congratulations! Your Java development environment is ready! 🎉**

You've successfully set up a professional Java development environment. You're now ready to begin your Java mastery journey with [Lab 01: Java Basics](../01-java-basics/README.md).