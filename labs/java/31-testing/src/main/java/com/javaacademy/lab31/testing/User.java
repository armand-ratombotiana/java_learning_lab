package com.javaacademy.lab31.testing;

import java.util.Objects;

public class User {
    private final long id;
    private final String email;
    private final String name;
    private boolean active;

    public User(long id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.active = true;
    }

    public long getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
