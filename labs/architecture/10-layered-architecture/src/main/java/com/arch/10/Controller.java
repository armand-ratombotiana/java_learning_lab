package com.arch.layered;

public class Controller {
    private final Service service;

    public Controller(Service service) {
        this.service = service;
    }

    public String handleRequest(String request) {
        System.out.println("Controller received: " + request);
        return service.processRequest(request);
    }
}
