package com.learning.micronaut;

import io.micronaut.http.annotation.*;

@Controller("/api")
public class MicronautLab {

    @Get("/hello")
    public String hello() {
        return "Hello from Micronaut!";
    }

    @Get("/greet/{name}")
    public String greet(@PathVariable String name) {
        return "Greetings, " + name + "!";
    }

    @Post("/echo")
    public String echo(@Body String message) {
        return "Echo: " + message;
    }
}