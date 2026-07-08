package com.learning.backend17.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.Optional;

public class ApiVersionInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ApiVersionInterceptor.class);
    private static final ThreadLocal<String> currentVersion = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {
        String version = Optional.ofNullable(request.getHeader("Accept-Version"))
            .orElseGet(() -> request.getParameter("version"));
        if (version != null) {
            currentVersion.set(version);
            log.debug("API version requested: {}", version);
        }
        response.setHeader("X-API-Version",
            Optional.ofNullable(version).orElse("default"));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        currentVersion.remove();
    }

    public static String getCurrentVersion() {
        return currentVersion.get();
    }
}
