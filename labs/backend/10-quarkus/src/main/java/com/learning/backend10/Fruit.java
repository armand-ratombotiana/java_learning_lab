package com.learning.backend10;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Quarkus Panache entity.
 *
 * Panache is Quarkus's active-record-style ORM layer.
 * By extending PanacheEntity, we get automatic ID generation and
 * built-in CRUD methods (persist(), findById(), listAll(), delete(), etc.)
 * without needing a separate repository.
 *
 * PanacheEntity provides the 'id' field automatically.
 */
@Entity
@Table(name = "fruits")
public class Fruit extends PanacheEntity {

    public String name;
    public String color;

    public Fruit() {}

    public Fruit(String name, String color) {
        this.name = name;
        this.color = color;
    }

    @Override
    public String toString() {
        return "Fruit{id=" + id + ", name='" + name + "', color='" + color + "'}";
    }
}
