package com.learning.backend10;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * REST resource for Fruit entities using Panache.
 *
 * Demonstrates Panache's active record pattern:
 * - Fruit.listAll() — fetches all fruits
 * - Fruit.findById(id) — finds by primary key
 * - fruit.persist() — saves a new entity
 * - fruit.delete() — removes the entity
 */
@Path("/api/fruits")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FruitResource {

    private static final Logger log = LoggerFactory.getLogger(FruitResource.class);

    @GET
    public List<Fruit> list() {
        log.info("GET /api/fruits — listing all fruits");
        return Fruit.listAll();
    }

    @GET
    @Path("/{id}")
    public Fruit get(Long id) {
        log.info("GET /api/fruits/{}", id);
        return Fruit.findById(id);
    }

    @POST
    @Transactional
    public Response create(Fruit fruit) {
        log.info("POST /api/fruits with body: {}", fruit);
        fruit.persist();
        return Response.status(Response.Status.CREATED).entity(fruit).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void delete(Long id) {
        log.info("DELETE /api/fruits/{}", id);
        Fruit.deleteById(id);
    }
}
