package com.learning.backend25.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class GreetingController {

    @GetMapping("/greeting")
    public Map<String, Object> greeting() {
        return Map.of(
            "message", "Hello from GraalVM Native Image!",
            "timestamp", Instant.now().toString(),
            "javaVersion", Runtime.version().toString()
        );
    }

    @GetMapping("/info")
    public Map<String, Object> info() {
        return Map.of(
            "appName", "GraalVM Native Lab",
            "nativeImage", isNativeImage(),
            "availableProcessors", Runtime.getRuntime().availableProcessors()
        );
    }

    private boolean isNativeImage() {
        return System.getProperty("org.graalvm.nativeimage.imagecode") != null;
    }
}
