package com.learning.backend26;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;

@Configuration
public class DispatcherChainAnalyzer implements WebMvcConfigurer {

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @Autowired
    private RequestMappingHandlerAdapter handlerAdapter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TraceInterceptor());
    }

    public void traceResolvers() {
        List<HandlerMethodArgumentResolver> resolvers = handlerAdapter.getArgumentResolvers();
        for (HandlerMethodArgumentResolver resolver : resolvers) {
            System.out.println("Resolver: " + resolver.getClass().getSimpleName()
                    + " [supports=" + resolver.supportsParameter(null) + "]");
        }
    }

    public void traceReturnValueHandlers() {
        List<HandlerMethodReturnValueHandler> handlers = handlerAdapter.getReturnValueHandlers();
        for (HandlerMethodReturnValueHandler handler : handlers) {
            System.out.println("ReturnValueHandler: " + handler.getClass().getSimpleName());
        }
    }

    public void traceMessageConverters() {
        List<HttpMessageConverter<?>> converters = handlerAdapter.getMessageConverters();
        for (HttpMessageConverter<?> converter : converters) {
            System.out.println("MessageConverter: " + converter.getClass().getSimpleName()
                    + " [types=" + converter.getSupportedMediaTypes() + "]");
        }
    }

    public HandlerExecutionChain analyze(String path) throws Exception {
        HttpServletRequest mockRequest = new org.springframework.mock.web.MockHttpServletRequest("GET", path);
        return handlerMapping.getHandler(mockRequest);
    }

    static class TraceInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            if (handler instanceof HandlerMethod hm) {
                System.out.println("Intercepted: " + hm.getBeanType().getSimpleName()
                        + "." + hm.getMethod().getName());
            }
            return true;
        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                    Object handler, Exception ex) {
            System.out.println("Completed: " + request.getMethod() + " " + request.getRequestURI()
                    + " → " + response.getStatus());
        }
    }
}