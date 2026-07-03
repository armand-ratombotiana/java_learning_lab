# Module 52: Serverless Java & AWS Lambda - Mini Project

**Project Name**: Cloud-Native Thumbnail Generator  
**Difficulty Level**: Advanced  
**Estimated Time**: 3-4 hours

---

## 🎯 Objective
Build an AWS Lambda function from scratch using pure Java. Implement proper resource initialization, deploy it, and optionally configure it for GraalVM Native Image compilation to measure the massive reduction in Cold Start times.

## 📝 Requirements

### Core Features

1. **Lambda Function Setup**:
   - Create a standard Maven project.
   - Add the `aws-lambda-java-core` and `aws-lambda-java-events` dependencies.
   - Create a class `ImageResizerHandler` that implements `RequestHandler<S3Event, String>`.

2. **Resource Initialization (The Right Way)**:
   - Instantiate a mock `S3Client` or `ImageProcessingService` inside the class constructor as a `private final` variable.
   - Log a message to `System.out` inside the constructor: "--- COLD START: Initializing Resources ---".

3. **Event Processing**:
   - In the `handleRequest` method, extract the S3 Bucket Name and Object Key from the incoming `S3Event` object.
   - Log the processing of the file: "Processing image: " + bucket + "/" + key.
   - Return a success string.

4. **Testing the Cold Start Lifecycle**:
   - Write a `main` method or a JUnit test to simulate AWS Lambda's execution environment.
   - Instantiate the `ImageResizerHandler` *once*.
   - Call `handleRequest` 3 times in a loop.
   - *Observation*: You should see the "COLD START" log exactly once, and the "Processing" log three times, proving that static state survives between invocations.

5. **GraalVM Native Build (Bonus)**:
   - Add the Spring Cloud Function dependencies.
   - Refactor the Handler into a `Function<S3Event, String>`.
   - Add the `native-maven-plugin`.
   - Run `mvn -Pnative native:compile` (requires GraalVM installed locally or via Docker).
   - Observe the final generated binary executable.

---

## 💡 Solution Blueprint

```java
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;

public class ImageResizerHandler implements RequestHandler<S3Event, String> {

    // Heavy resource initialized ONCE during the cold start
    private final ImageProcessingService imageService;

    public ImageResizerHandler() {
        System.out.println("--- COLD START: Booting JVM and Initializing Heavy Resources ---");
        this.imageService = new ImageProcessingService();
    }

    @Override
    public String handleRequest(S3Event s3Event, Context context) {
        // Extract S3 Event details
        String bucket = s3Event.getRecords().get(0).getS3().getBucket().getName();
        String key = s3Event.getRecords().get(0).getS3().getObject().getKey();

        System.out.println("Warm Invocation: Processing image: " + bucket + "/" + key);
        
        imageService.resize(bucket, key);
        return "Successfully resized " + key;
    }
}

class ImageProcessingService {
    public void resize(String bucket, String key) {
        // Mock processing
    }
}
```