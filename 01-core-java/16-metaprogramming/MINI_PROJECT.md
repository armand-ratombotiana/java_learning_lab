# Mini Project: Building a Timing Agent with Javassist

## Objective
Build a Java Agent that intercepts method calls at load-time and injects bytecode to measure and print the execution time of any method annotated with a custom `@LogExecutionTime` annotation.

## Prerequisites
*   Java 21
*   Maven

## Step 1: Create the Annotation
Create the annotation that developers will use to mark methods for timing.

```java
package com.example.agent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExecutionTime {
}
```

## Step 2: Add Javassist Dependency
Add Javassist to your `pom.xml`.

```xml
<dependency>
    <groupId>org.javassist</groupId>
    <artifactId>javassist</artifactId>
    <version>3.29.2-GA</version>
</dependency>
```

## Step 3: Create the ClassFileTransformer
This class intercepts the byte array of classes as they are loaded. We use Javassist to parse the bytes, look for the annotation, and inject timing code.

```java
package com.example.agent;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class TimingTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        try {
            // Convert JVM class name format (com/example/MyClass) to Java format (com.example.MyClass)
            String javaClassName = className.replace("/", ".");
            
            ClassPool cp = ClassPool.getDefault();
            CtClass cc = cp.get(javaClassName);

            boolean modified = false;

            for (CtMethod method : cc.getDeclaredMethods()) {
                if (method.hasAnnotation(LogExecutionTime.class)) {
                    // Inject code at the beginning of the method
                    method.addLocalVariable("startTime", CtClass.longType);
                    method.insertBefore("startTime = System.currentTimeMillis();");

                    // Inject code at the end of the method (before return)
                    String endBlock = "{ long endTime = System.currentTimeMillis(); " +
                                      "System.out.println(\"Method " + method.getName() + " took \" + (endTime - startTime) + \" ms.\"); }";
                    method.insertAfter(endBlock);
                    
                    modified = true;
                }
            }

            if (modified) {
                return cc.toBytecode();
            }
        } catch (Exception e) {
            // If transformation fails, just return the original bytecode
            e.printStackTrace();
        }
        return classfileBuffer;
    }
}
```

## Step 4: Create the Agent Entry Point
The `premain` method is called by the JVM before the application's `main` method starts.

```java
package com.example.agent;

import java.lang.instrument.Instrumentation;

public class TimingAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("[Agent] Registering TimingTransformer...");
        inst.addTransformer(new TimingTransformer());
    }
}
```

## Step 5: Configure the Manifest
To run as a Java Agent, the output JAR must have a specific `MANIFEST.MF`. Configure the Maven Assembly or Jar plugin:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jar-plugin</artifactId>
    <version>3.3.0</version>
    <configuration>
        <archive>
            <manifestEntries>
                <Premain-Class>com.example.agent.TimingAgent</Premain-Class>
                <Can-Redefine-Classes>true</Can-Redefine-Classes>
                <Can-Retransform-Classes>true</Can-Retransform-Classes>
            </manifestEntries>
        </archive>
    </configuration>
</plugin>
```

## Step 6: Test the Agent
1. Build the agent JAR: `mvn clean package`.
2. Create a separate test application with a method annotated with `@LogExecutionTime`.
3. Run the test application with the agent attached:
   `java -javaagent:path/to/your-agent.jar -jar your-app.jar`

You should see the injected print statements executing automatically without modifying the source code of the test application!