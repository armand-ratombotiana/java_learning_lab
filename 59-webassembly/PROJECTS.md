# WebAssembly Projects - Module 59

This module covers WASM in Java, GraalVM native image for high-performance Java applications.

## Mini-Project: GraalVM Native Image REST API (2-4 hours)

### Overview
Build and compile a Spring Boot REST API into a native executable using GraalVM for instant startup and minimal memory footprint.

### Project Structure
```
graalvm-native-demo/
├── src/
│   └── main/
│       ├── java/com/learning/wasm/
│       │   ├── NativeDemoApplication.java
│       │   ├── controller/ProductController.java
│       │   └── model/Product.java
│       └── resources/
│           └── application.yml
├── pom.xml
├── native-image-config/
│   ├── reflect-config.json
│   ├── resource-config.json
│   └── initialization-config.json
└── Dockerfile
```

### Implementation

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>graalvm-native-demo</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>21</java.version>
        <graalvm.version>23.1.0</graalvm.version>
        <native.maven.plugin.version>0.9.24</native.maven.plugin.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-native</artifactId>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-starter</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.graalvm.buildtools</groupId>
                <artifactId>native-maven-plugin</artifactId>
                <version>${native.maven.plugin.version}</version>
                <configuration>
                    <buildArgs>
                        <buildArg>-H:IncludeResources=application.yml</buildArg>
                        <buildArg>-H:ReflectionConfigurationFiles=native-image-config/reflect-config.json</buildArg>
                    </buildArgs>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

#### Java Application
```java
// NativeDemoApplication.java
package com.learning.wasm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NativeDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(NativeDemoApplication.class, args);
    }
}

// ProductController.java
package com.learning.wasm.controller;

import com.learning.wasm.model.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final Map<Long, Product> products = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    public ProductController() {
        products.put(1L, new Product(1L, "Native Laptop", 1299.99, "High-performance"));
        products.put(2L, new Product(2L, "Wireless Mouse", 49.99, "Ergonomic design"));
    }
    
    @GetMapping
    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        Product product = products.get(id);
        return product != null 
            ? ResponseEntity.ok(product) 
            : ResponseEntity.notFound().build();
    }
    
    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        Long id = idGenerator.getAndIncrement();
        product.setId(id);
        products.put(id, product);
        return product;
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id, 
            @RequestBody Product product) {
        
        if (products.containsKey(id)) {
            product.setId(id);
            products.put(id, product);
            return ResponseEntity.ok(product);
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        return products.remove(id) != null 
            ? ResponseEntity.noContent().build() 
            : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
            "status", "UP",
            "products", products.size(),
            "timestamp", System.currentTimeMillis()
        );
    }
}

// Product.java
package com.learning.wasm.model;

public class Product {
    private Long id;
    private String name;
    private Double price;
    private String description;
    
    public Product() {}
    
    public Product(Long id, String name, Double price, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
```

#### Native Image Configuration
```json
// native-image-config/reflect-config.json
[
  {
    "name": "com.learning.wasm.model.Product",
    "fields": [
      { "name": "id", "allowWrite": true },
      { "name": "name", "allowWrite": true },
      { "name": "price", "allowWrite": true },
      { "name": "description", "allowWrite": true }
    ]
  },
  {
    "name": "com.learning.wasm.controller.ProductController",
    "methods": [
      { "name": "getAllProducts" },
      { "name": "getProduct" },
      { "name": "createProduct" },
      { "name": "updateProduct" },
      { "name": "deleteProduct" },
      { "name": "health" }
    ]
  },
  {
    "name": "java.lang.String"
  },
  {
    "name": "java.lang.Long"
  },
  {
    "name": "java.lang.Double"
  },
  {
    "name": "java.util.Map"
  },
  {
    "name": "java.util.HashMap"
  }
]

// native-image-config/resource-config.json
{
  "resources": {
    "includes": [
      { "pattern": "application.yml" },
      { "pattern": "application.properties" }
    ]
  }
}

// application.yml
server:
  port: 8080

spring:
  application:
    name: graalvm-native-demo

logging:
  level:
    root: INFO
    com.learning: DEBUG
```

#### Dockerfile for Native Build
```dockerfile
# Dockerfile
FROM ghcr.io/graalvm/graalvm-community:23.1.0 AS builder

# Install native-image tool
RUN gu install native-image

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN ./mvnw package -Pnative -DskipTests

FROM debian:bookworm-slim

COPY --from=builder /app/target/*.so /app/
COPY --from=builder /app/target/native-demo /app/native-demo

EXPOSE 8080

ENTRYPOINT ["/app/native-demo"]
```

### Build and Test Instructions
```bash
# Using Maven (requires GraalVM)
export GRAALVM_HOME=/path/to/graalvm
mvn clean package -Pnative -DskipTests

# Or using native-maven-plugin directly
mvn -Pnative native:build

# Run the native executable
./target/native-demo

# Verify startup time
time ./target/native-demo

# Check memory usage
ps aux | grep native-demo

# Test the API
curl http://localhost:8080/api/products
curl http://localhost:8080/api/products/1
curl http://localhost:8080/api/products/health

# Build with Docker
docker build -t graalvm-native-demo .
docker run -p 8080:8080 graalvm-native-demo
```

---

## Real-World Project: WASM-Enabled Image Processing Service (8+ hours)

### Overview
Build a high-performance image processing service using GraalVM native image with WebAssembly modules for portable, sandboxed image transformations.

### Architecture
- **GraalVM Native Image**: High-performance Java runtime
- **WASM Modules**: Portable image filters and transformations
- **Native Image Pre-compilation**: Instant startup
- **Memory Optimization**: Sub-100MB footprint
- **Docker Integration**: Minimal container images

### Project Structure
```
wasm-image-processor/
├── core/
│   ├── src/main/java/com/learning/wasm/
│   │   ├── ImageProcessorApplication.java
│   │   ├── controller/ImageController.java
│   │   ├── service/ImageService.java
│   │   └── model/ImageRequest.java
│   └── pom.xml
├── wasm-modules/
│   ├── filters/
│   │   ├── grayscale.wat
│   │   ├── blur.wat
│   │   ├── sharpen.wat
│   │   └── edge-detect.wat
│   └── src/
├── native-config/
│   ├── reflect-config.json
│   ├── resource-config.json
│   └── proxy-config.json
├── tests/
├── Dockerfile.native
└── pom.xml
```

### Implementation

#### Core Application
```java
// pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>wasm-image-processor</artifactId>
    <version>1.0.0</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>21</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>
        <dependency>
            <groupId>org.wasmer</groupId>
            <artifactId>wasmer-java</artifactId>
            <version>1.1.0</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>org.graalvm.buildtools</groupId>
                <artifactId>native-maven-plugin</artifactId>
                <version>0.9.24</version>
            </plugin>
        </plugins>
    </build>
</project>

// ImageProcessorApplication.java
package com.learning.wasm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ImageProcessorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImageProcessorApplication.class, args);
    }
}

// ImageController.java
package com.learning.wasm.controller;

import com.learning.wasm.model.ImageRequest;
import com.learning.wasm.service.ImageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/images")
public class ImageController {
    
    private final ImageService imageService;
    
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }
    
    @PostMapping(value = "/process", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<byte[]>> processImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "filter", defaultValue = "grayscale") String filter,
            @RequestParam(value = "intensity", defaultValue = "1.0") double intensity) {
        
        return imageService.processImage(file.getBytes(), filter, intensity)
            .map(result -> ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(result));
    }
    
    @PostMapping("/batch")
    public Mono<Map<String, byte[]>> batchProcess(
            @RequestBody ImageRequest request) {
        
        return imageService.batchProcess(
            request.getImages(), 
            request.getFilter(), 
            request.getIntensity()
        );
    }
    
    @GetMapping("/filters")
    public Mono<Map<String, String>> getAvailableFilters() {
        return Mono.just(Map.of(
            "grayscale", "Convert image to grayscale",
            "blur", "Apply Gaussian blur filter",
            "sharpen", "Enhance image edges",
            "edge-detect", "Detect image edges"
        ));
    }
    
    @GetMapping("/health")
    public Mono<Map<String, Object>> health() {
        return Mono.just(Map.of(
            "status", "UP",
            "wasm-runtime", "initialized",
            "memory-usage", Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        ));
    }
}

// ImageService.java
package com.learning.wasm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.wasmer.Instance;
import org.wasmer.Memory;
import org.wasmer.Module;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ImageService {
    
    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);
    private final Map<String, Module> wasmModules = new ConcurrentHashMap<>();
    private boolean initialized = false;
    
    public ImageService() {
        initializeWasmModules();
    }
    
    private void initializeWasmModules() {
        try {
            String[] filters = {"grayscale", "blur", "sharpen", "edge-detect"};
            for (String filter : filters) {
                byte[] wasmBytes = loadWasmModule(filter);
                Module module = Module.compile(wasmBytes);
                wasmModules.put(filter, module);
            }
            initialized = true;
            logger.info("WASM modules initialized: {}", wasmModules.keySet());
        } catch (Exception e) {
            logger.error("Failed to initialize WASM modules", e);
            initialized = false;
        }
    }
    
    private byte[] loadWasmModule(String filter) throws Exception {
        // For demo, return a simple WASM placeholder
        // In production, load actual compiled .wasm files
        return generatePlaceholderWasm();
    }
    
    private byte[] generatePlaceholderWasm() {
        // Simple WASM module that processes images
        // This is a minimal placeholder for demonstration
        return new byte[] {
            0x00, 0x61, 0x73, 0x6d, // magic number
            0x01, 0x00, 0x00, 0x00  // version
        };
    }
    
    public Mono<byte[]> processImage(byte[] imageData, String filter, double intensity) {
        return Mono.fromCallable(() -> {
            if (!initialized) {
                throw new RuntimeException("WASM runtime not initialized");
            }
            
            Module module = wasmModules.get(filter);
            if (module == null) {
                throw new IllegalArgumentException("Unknown filter: " + filter);
            }
            
            return processWithWasm(imageData, module, filter, intensity);
        });
    }
    
    private byte[] processWithWasm(byte[] imageData, Module module, 
                                   String filter, double intensity) {
        try {
            Instance instance = module.instantiate();
            
            Memory memory = instance.getMemory();
            
            // Allocate memory for image data
            int dataOffset = 0;
            int dataLength = imageData.length;
            
            memory.write(dataOffset, imageData);
            
            // Call WASM function
            Instance.Function processFunction = instance.getExportedFunction("process");
            processFunction.apply(dataOffset, dataLength, (int)(intensity * 100));
            
            // Read processed data
            byte[] result = memory.read(dataOffset, dataLength);
            
            instance.close();
            return result;
            
        } catch (Exception e) {
            logger.error("WASM processing failed for filter: {}", filter, e);
            // Fallback to Java-based processing
            return processWithJava(imageData, filter, intensity);
        }
    }
    
    private byte[] processWithJava(byte[] imageData, String filter, double intensity) {
        // Java fallback implementation
        // Apply filter using standard Java image processing
        logger.info("Using Java fallback for filter: {}", filter);
        
        // Simple placeholder - in production use actual image processing
        return imageData;
    }
    
    public Mono<Map<String, byte[]>> batchProcess(
            List<byte[]> images, 
            String filter, 
            double intensity) {
        
        return Mono.fromCallable(() -> {
            Map<String, byte[]> results = new HashMap<>();
            
            for (int i = 0; i < images.size(); i++) {
                byte[] processed = processImage(images.get(i), filter, intensity).block();
                results.put("image_" + i, processed);
            }
            
            return results;
        });
    }
}

// ImageRequest.java
package com.learning.wasm.model;

import java.util.List;

public class ImageRequest {
    private List<byte[]> images;
    private String filter;
    private double intensity;
    
    public List<byte[]> getImages() { return images; }
    public void setImages(List<byte[]> images) { this.images = images; }
    public String getFilter() { return filter; }
    public void setFilter(String filter) { this.filter = filter; }
    public double getIntensity() { return intensity; }
    public void setIntensity(double intensity) { this.intensity = intensity; }
}
```

#### WASM Module Definition
```wat
;; grayscale.wat - WebAssembly Text Format
(module
  (func $grayscale (export "process")
    (param $data i32)
    (param $length i32)
    (param $intensity i32)
    
    (local $i i32)
    (local $r i32)
    (local $g i32)
    (local $b i32)
    (local $gray i32)
    
    (local.set $i (local.get $data))
    
    (block
      (loop
        ;; Read RGB values (assuming 3 bytes per pixel)
        (local.set $r (i32.load8_u (local.get $i)))
        (local.set $g (i32.load8_u (i32.add (local.get $i) (i32.const 1))))
        (local.set $b (i32.load8_u (i32.add (local.get $i) (i32.const 2))))
        
        ;; Calculate grayscale: 0.299*R + 0.587*G + 0.114*B
        (local.set $gray 
          (i32.add 
            (i32.add 
              (i32.mul (local.get $r) (i32.const 77))
              (i32.mul (local.get $g) (i32.const 150)))
            (i32.mul (local.get $b) (i32.const 29))))
        
        ;; Store grayscale value
        (i32.store8 (local.get $i) (local.get $gray))
        (i32.store8 (i32.add (local.get $i) (i32.const 1)) (local.get $gray))
        (i32.store8 (i32.add (local.get $i) (i32.const 2)) (local.get $gray))
        
        ;; Move to next pixel
        (local.set $i (i32.add (local.get $i) (i32.const 3)))
        
        ;; Check if more pixels
        (br_if 0 (i32.lt_u (local.get $i) (local.get $length)))
      )
    )
  )
  
  (memory (export "memory") 1)
)
```

### Native Image Configuration
```json
// native-config/reflect-config.json
[
  {
    "name": "com.learning.wasm.controller.ImageController",
    "methods": [
      { "name": "processImage" },
      { "name": "batchProcess" },
      { "name": "getAvailableFilters" },
      { "name": "health" }
    ]
  },
  {
    "name": "com.learning.wasm.service.ImageService",
    "methods": [
      { "name": "processImage" },
      { "name": "batchProcess" }
    ]
  },
  {
    "name": "org.wasmer.Instance",
    "methods": [
      { "name": "getMemory" },
      { "name": "close" }
    ]
  },
  {
    "name": "org.wasmer.Memory",
    "methods": [
      { "name": "read" },
      { "name": "write" }
    ]
  }
]
```

### Build and Deploy
```bash
# Install GraalVM
sdk install java 23.1.0-graal
export GRAALVM_HOME=$JAVA_HOME

# Install native-image
gu install native-image

# Build native image
mvn clean package -Pnative -DskipTests

# Measure startup time
time ./target/wasm-image-processor

# Measure memory usage
/usr/bin/time -v ./target/wasm-image-processor

# Run with Docker
docker build -f Dockerfile.native -t wasm-processor .
docker run -p 8080:8080 wasm-processor

# Test the API
curl -X POST http://localhost:8080/api/images/process \
  -F "file=@test-image.jpg" \
  -F "filter=grayscale" \
  -F "intensity=1.0" \
  -o processed.png

curl http://localhost:8080/api/images/filters

# Benchmark
ab -n 1000 -c 10 -T "multipart/form-data" \
  -p request.txt http://localhost:8080/api/images/process
```

### Learning Outcomes
- Build native executables with GraalVM
- Configure native image compilation
- Integrate WebAssembly with Java
- Optimize memory usage
- Deploy minimal Docker containers
- Benchmark native vs JVM performance