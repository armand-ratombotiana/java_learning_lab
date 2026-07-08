package com.learning.backend17.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.patterns.PathPatternParser;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addPathMatchConfigurer(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/v1", handler ->
            handler instanceof V1ApiMarker);
        configurer.addPathPrefix("/v2", handler ->
            handler instanceof V2ApiMarker);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ApiVersionInterceptor())
            .addPathPatterns("/api/**");
    }
}
