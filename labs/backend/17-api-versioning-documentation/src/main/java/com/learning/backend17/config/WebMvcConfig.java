package com.learning.backend17.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/v1", handler ->
            V1ApiMarker.class.isAssignableFrom(handler));
        configurer.addPathPrefix("/v2", handler ->
            V2ApiMarker.class.isAssignableFrom(handler));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ApiVersionInterceptor())
            .addPathPatterns("/api/**");
    }
}
