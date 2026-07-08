package com.learning.backend25.service;

import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class NativeSupportService {

    public String loadResource(String path) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) return "Resource not found: " + path;
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "Error reading resource: " + e.getMessage();
        }
    }

    public String getBuildInfo() {
        return loadResource("META-INF/build-info.properties");
    }
}
