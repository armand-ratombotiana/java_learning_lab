package com.learning.backend01;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A REST controller demonstrating @RestController and property injection.
 *
 * @RestController combines @Controller and @ResponseBody so every method
 * returns a domain object (not a view) that is serialized directly to HTTP response.
 */
@RestController
@RequestMapping("/api")
public class HelloController {

    private static final Logger log = LoggerFactory.getLogger(HelloController.class);

    /**
     * Injects the 'app.greeting' property from application.properties (or
     * application.yml). The default value "Hello, Spring Boot!" is used when
     * the property is absent.
     */
    @Value("${app.greeting:Hello, Spring Boot!}")
    private String greeting;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    /**
     * GET /api/hello — returns a simple greeting string.
     */
    @GetMapping("/hello")
    public String sayHello() {
        log.info("sayHello() called");
        return greeting;
    }

    /**
     * GET /api/info — returns application metadata as JSON.
     * Spring Boot automatically serializes the returned Map to JSON via Jackson.
     */
    @GetMapping("/info")
    public java.util.Map<String, Object> getInfo() {
        return java.util.Map.of(
            "appName", "Spring Boot Basics Lab",
            "version", appVersion,
            "javaVersion", Runtime.version().toString()
        );
    }
}
