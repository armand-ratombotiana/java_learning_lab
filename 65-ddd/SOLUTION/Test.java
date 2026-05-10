package com.learning.ddd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DDDSolutionTest {

    private DDDSolution solution;

    @BeforeEach
    void setUp() {
        solution = new DDDSolution();
    }

    @Test
    void testEntityEquality() {
        DDDSolution.Entity<String> e1 = new DDDSolution.Entity<>("id1");
        DDDSolution.Entity<String> e2 = new DDDSolution.Entity<>("id1");
        DDDSolution.Entity<String> e3 = new DDDSolution.Entity<>("id2");
        assertEquals(e1, e2);
        assertNotEquals(e1, e3);
    }

    @Test
    void testValueObject() {
        DDDSolution.Address addr1 = new DDDSolution.Address("123 Main St");
        DDDSolution.Address addr2 = new DDDSolution.Address("123 Main St");
        assertEquals(addr1, addr2);
    }

    @Test
    void testMoneyOperations() {
        DDDSolution.Money m1 = new Money(100.0);
        DDDSolution.Money m2 = new Money(50.0);
        assertEquals(150.0, m1.add(m2).getValue());
        assertEquals(50.0, m1.subtract(m2).getValue());
    }

    @Test
    void testOrderCreation() {
        DDDSolution.Order order = solution.createOrder("order-1");
        assertEquals("order-1", order.getId());
        assertEquals(DDDSolution.OrderStatus.DRAFT, order.getStatus());
    }

    @Test
    void testOrderAddItem() {
        DDDSolution.Order order = solution.createOrder("order-1");
        order.addItem("product-1", 2, new DDDSolution.Money(50.0));
        assertEquals(1, order.getItems().size());
        assertEquals(2, order.getItems().get(0).getQuantity());
    }

    @Test
    void testOrderConfirm() {
        DDDSolution.Order order = solution.createOrder("order-1");
        order.addItem("product-1", 1, new DDDSolution.Money(100.0));
        order.confirm();
        assertEquals(DDDSolution.OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    void testOrderCancel() {
        DDDSolution.Order order = solution.createOrder("order-1");
        order.addItem("product-1", 1, new DDDSolution.Money(100.0));
        order.confirm();
        order.cancel();
        assertEquals(DDDSolution.OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void testOrderTotal() {
        DDDSolution.Order order = solution.createOrder("order-1");
        order.addItem("product-1", 2, new DDDSolution.Money(50.0));
        order.addItem("product-2", 1, new DDDSolution.Money(75.0));
        assertEquals(175.0, order.getTotal().getValue());
    }

    @Test
    void testDomainEvents() {
        DDDSolution.Order order = solution.createOrder("order-1");
        order.addItem("product-1", 1, new DDDSolution.Money(100.0));
        order.confirm();
        List<DDDSolution.DomainEvent> events = order.getEvents();
        assertTrue(events.size() >= 2);
    }

    @Test
    void testRepository() {
        DDDSolution.Repository<DDDSolution.Order> repo = solution.createOrderRepository();
        DDDSolution.Order order = solution.createOrder("order-1");
        repo.save(order);
        Optional<DDDSolution.Order> found = repo.findById("order-1");
        assertTrue(found.isPresent());
        assertEquals("order-1", found.get().getId());
    }
}