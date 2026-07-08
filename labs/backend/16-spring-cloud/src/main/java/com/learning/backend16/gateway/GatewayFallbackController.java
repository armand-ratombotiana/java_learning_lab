package com.learning.backend16.gateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;

@RestController
public class GatewayFallbackController {

    @RequestMapping("/fallback/products")
    public ProblemDetail productFallback() {
        var problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.SERVICE_UNAVAILABLE,
            "Product service is currently unavailable. Please try again later.");
        problem.setTitle("Service Unavailable");
        problem.setType(URI.create("https://api.example.com/errors/service-unavailable"));
        problem.setInstance(URI.create("/fallback/products"));
        return problem;
    }

    @RequestMapping("/fallback/orders")
    public ProblemDetail orderFallback() {
        var problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.SERVICE_UNAVAILABLE,
            "Order service is currently unavailable.");
        problem.setTitle("Service Unavailable");
        problem.setType(URI.create("https://api.example.com/errors/service-unavailable"));
        return problem;
    }
}
