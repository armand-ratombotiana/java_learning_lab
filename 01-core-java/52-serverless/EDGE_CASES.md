# Module 52: Serverless Java & AWS Lambda - Edge Cases & Pitfalls

---

## Pitfall 1: Fat Jars and Massive Dependencies

### ❌ Wrong
Deploying a Serverless function packaged as a 150MB "Fat Jar" containing dependencies for 3 different databases, an embedded Tomcat server, and extensive utility libraries, even though the function only resizes images. 

### ✅ Correct
Cold starts are directly impacted by deployment package size. AWS Lambda has to download the ZIP file from S3 into the container environment. The larger the jar, the longer the cold start. Strip out all unnecessary dependencies. Consider using Maven Shade Plugin to minimize the final deployment artifact.

---

## Pitfall 2: Initializing Heavy Resources Inside the Handler

### ❌ Wrong
Opening a database connection or initializing an AWS S3 client *inside* the `handleRequest` method.
```java
public String handleRequest(String input, Context context) {
    // ❌ Called on EVERY execution, destroying performance
    AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient(); 
    return s3Client.getObjectAsString("my-bucket", input);
}
```

### ✅ Correct
Initialize heavy resources in the class constructor or as static fields. The cloud provider freezes the execution environment between warm invocations, meaning static state is preserved for subsequent calls.
```java
public class MyLambda implements RequestHandler<String, String> {
    // ✅ Initialized once during the Cold Start
    private static final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

    public String handleRequest(String input, Context context) {
        return s3Client.getObjectAsString("my-bucket", input);
    }
}
```

---

## Pitfall 3: Reflection Failures in GraalVM Native Images

### ❌ Wrong
Deploying an application to GraalVM that heavily relies on dynamic Java Reflection (like loading classes by name from a configuration file at runtime). GraalVM AOT compilation will strip those classes out, and the application will crash instantly at runtime with a `ClassNotFoundException`.

### ✅ Correct
GraalVM operates on the "Closed World Assumption." It must know exactly which classes will be executed at compile time. You must manually provide GraalVM with "Reflection Configuration" JSON files, or use a framework like Spring Boot 3 which automates the generation of these AOT configurations during the build phase.