package com.arch.layered;

public class Service {
    private final Repository repository;

    public Service(Repository repository) {
        this.repository = repository;
    }

    public String processRequest(String data) {
        System.out.println("Service processing: " + data);
        repository.save(new Entity(data));
        return "Processed: " + data;
    }
}
