package com.arch.layered;

public class DTO {
    private final String id;
    private final String content;

    public DTO(String id, String content) {
        this.id = id;
        this.content = content;
    }

    public String getId() { return id; }
    public String getContent() { return content; }
}
