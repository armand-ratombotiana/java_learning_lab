# Step by Step: 09 Helidon

## Step 1: Add Dependencies
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-09-helidon</artifactId>
</dependency>
```

## Step 2: Configure Properties
```properties
09helidon.enabled=true
09helidon.timeout=30
```

## Step 3: Create Service
```java
@Service
public class MyService {
    public void execute() {
        // Implementation
    }
}
```

## Step 4: Use in Controller
```java
@RestController
public class MyController {
    private final MyService myService;

    public MyController(MyService myService) {
        this.myService = myService;
    }
}
```

## Step 5: Test
```java
@SpringBootTest
class MyServiceTest {
    @Autowired
    private MyService myService;

    @Test
    void testExecute() {
        myService.execute();
    }
}
```

