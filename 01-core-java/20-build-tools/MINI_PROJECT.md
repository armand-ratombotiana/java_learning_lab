# Module 20: Build Tools (Maven & Gradle) - Mini Project

**Project Name**: Custom Maven Plugin & Multi-Module Setup  
**Difficulty Level**: Intermediate  
**Estimated Time**: 2-3 hours

---

## 🎯 Objective
Understand how build tools manage dependencies, phases, and modules by structuring a multi-module Maven project and creating a simple custom Maven plugin.

## 📝 Requirements

### Core Features
1. **Multi-Module Project**:
   - Create a parent directory `e-commerce-app` with a `pom.xml`. Set the packaging to `<packaging>pom</packaging>`.
   - Create two child modules: `core-logic` and `web-api`.
   - In the parent `pom.xml`, define the modules using the `<modules>` tag.

2. **Dependency Management**:
   - In the parent `pom.xml`, use `<dependencyManagement>` to declare versions for shared libraries (e.g., JUnit 5, Jackson).
   - In the `core-logic` module, add JUnit 5 as a test dependency.
   - In the `web-api` module, add `core-logic` as a dependency, so `web-api` can access classes defined in `core-logic`.

3. **Custom Maven Plugin (Bonus)**:
   - Create a third module called `hello-maven-plugin`.
   - Change its packaging to `<packaging>maven-plugin</packaging>`.
   - Create a Java class (Mojo) extending `AbstractMojo`. Annotate it with `@Mojo(name = "sayhi", defaultPhase = LifecyclePhase.COMPILE)`.
   - Override the `execute()` method to print `"Hello from custom plugin!"` using `getLog().info()`.

4. **Execution**:
   - Run `mvn clean install` from the parent directory and observe the reactor build order.

---

## 💡 Solution Blueprint

1. **Parent POM**:
   ```xml
   <project>
       <groupId>com.shop</groupId>
       <artifactId>e-commerce-parent</artifactId>
       <version>1.0</version>
       <packaging>pom</packaging>
       
       <modules>
           <module>core-logic</module>
           <module>web-api</module>
       </modules>
       
       <dependencyManagement>
           <dependencies>
               <dependency>
                   <groupId>org.junit.jupiter</groupId>
                   <artifactId>junit-jupiter-api</artifactId>
                   <version>5.10.0</version>
               </dependency>
           </dependencies>
       </dependencyManagement>
   </project>
   ```

2. **Child Module (`web-api`) POM**:
   ```xml
   <project>
       <parent>
           <groupId>com.shop</groupId>
           <artifactId>e-commerce-parent</artifactId>
           <version>1.0</version>
       </parent>
       <artifactId>web-api</artifactId>
       
       <dependencies>
           <dependency>
               <groupId>com.shop</groupId>
               <artifactId>core-logic</artifactId>
               <version>${project.version}</version>
           </dependency>
       </dependencies>
   </project>
   ```