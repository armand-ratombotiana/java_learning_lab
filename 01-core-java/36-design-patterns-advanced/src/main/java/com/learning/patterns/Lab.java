package com.learning.patterns;

import java.util.*;
import java.util.function.*;

public class Lab {

    interface Visitor { String visit(Circle c); String visit(Rectangle r); }
    interface Shape { String accept(Visitor v); }
    record Circle(double r) implements Shape { public String accept(Visitor v) { return v.visit(this); } }
    record Rectangle(double w, double h) implements Shape { public String accept(Visitor v) { return v.visit(this); } }

    interface ChatMediator { void send(String msg, User sender); }
    static class ChatRoom implements ChatMediator {
        final List<User> users = new ArrayList<>();
        void addUser(User u) { users.add(u); }
        public void send(String msg, User sender) {
            users.stream().filter(u -> u != sender).forEach(u -> u.receive(msg, sender.name));
        }
    }
    static class User {
        final String name; final ChatMediator m;
        User(String n, ChatMediator m) { this.name = n; this.m = m; }
        void send(String msg) { System.out.printf("    %s: %s%n", name, msg); m.send(msg, this); }
        void receive(String msg, String from) { System.out.printf("    %s sees from %s: %s%n", name, from, msg); }
    }

    interface OrderState { OrderState next(); OrderState cancel(); String status(); }
    record NewOrder() implements OrderState {
        public OrderState next() { return new PaidOrder(); }
        public OrderState cancel() { return new CancelledOrder(); }
        public String status() { return "NEW"; }
    }
    record PaidOrder() implements OrderState {
        public OrderState next() { return new ShippedOrder(); }
        public OrderState cancel() { return new CancelledOrder(); }
        public String status() { return "PAID"; }
    }
    record ShippedOrder() implements OrderState {
        public OrderState next() { return new DeliveredOrder(); }
        public OrderState cancel() { return this; }
        public String status() { return "SHIPPED"; }
    }
    record DeliveredOrder() implements OrderState {
        public OrderState next() { return this; } public OrderState cancel() { return this; }
        public String status() { return "DELIVERED"; }
    }
    record CancelledOrder() implements OrderState {
        public OrderState next() { return this; } public OrderState cancel() { return this; }
        public String status() { return "CANCELLED"; }
    }
    static class Order { OrderState s = new NewOrder();
        void next() { s = s.next(); System.out.println("  -> " + s.status()); }
        void cancel() { s = s.cancel(); System.out.println("  -> " + s.status()); }
    }

    @FunctionalInterface
    interface Logger { void log(String msg); }
    static class Service {
        private final Logger log;
        Service(Logger l) { log = l; }
        void work() { log.log("Working..."); log.log("Done."); }
    }

    interface Coffee { String desc(); double cost(); }
    record BasicCoffee() implements Coffee { public String desc() { return "Coffee"; } public double cost() { return 2.0; } }
    abstract static class CoffeeDecorator implements Coffee { protected final Coffee c; CoffeeDecorator(Coffee c) { this.c = c; } }
    static class Milk extends CoffeeDecorator { Milk(Coffee c) { super(c); } public String desc() { return c.desc() + " + Milk"; } public double cost() { return c.cost() + 0.5; } }
    static class Sugar extends CoffeeDecorator { Sugar(Coffee c) { super(c); } public String desc() { return c.desc() + " + Sugar"; } public double cost() { return c.cost() + 0.25; } }

    interface Payment { String pay(double amount); }
    record Card(String num) implements Payment {
        public String pay(double a) { return String.format("  Card ****%s: $%.2f", num.substring(num.length()-4), a); }
    }
    record PayPal(String email) implements Payment {
        public String pay(double a) { return String.format("  PayPal %s: $%.2f", email, a); }
    }
    record Crypto(String addr) implements Payment {
        public String pay(double a) { return String.format("  Crypto %s...: $%.2f", addr.substring(0,6), a); }
    }
    record Checkout(Payment p) {
        String execute(double a) { return p.pay(a); }
    }

    public static void main(String[] args) {
        System.out.println("=== Advanced Design Patterns Lab ===\n");

        System.out.println("--- Visitor Pattern ---");
        var area = new Visitor() {
            public String visit(Circle c) { return String.format("Circle area: %.2f", Math.PI * c.r() * c.r()); }
            public String visit(Rectangle r) { return String.format("Rect area: %.2f", r.w() * r.h()); }
        };
        var xml = new Visitor() {
            public String visit(Circle c) { return "<circle r='" + c.r() + "'/>"; }
            public String visit(Rectangle r) { return "<rect w='" + r.w() + "' h='" + r.h() + "'/>"; }
        };
        List<Shape> shapes = List.of(new Circle(5), new Rectangle(4, 6));
        System.out.println("  Area: " + shapes.stream().map(s -> s.accept(area)).toList());
        System.out.println("  XML:  " + shapes.stream().map(s -> s.accept(xml)).toList());

        System.out.println("\n--- Mediator Pattern ---");
        var chat = new ChatRoom();
        var alice = new User("Alice", chat); var bob = new User("Bob", chat);
        chat.addUser(alice); chat.addUser(bob);
        alice.send("Hi!"); bob.send("Hey!");

        System.out.println("\n--- State Pattern ---");
        var o = new Order();
        System.out.print("  NEW"); o.next(); o.next(); o.cancel();

        System.out.println("\n--- Null Object Pattern ---");
        var console = (Logger) msg -> System.out.println("  [LOG] " + msg);
        var noop = (Logger) msg -> {};
        System.out.println("  With logger:");
        new Service(console).work();
        System.out.println("  With NullLogger (silent):");
        new Service(noop).work();

        System.out.println("\n--- Decorator Pattern ---");
        Coffee coffee = new Sugar(new Milk(new BasicCoffee()));
        System.out.printf("  %s: $%.2f%n", coffee.desc(), coffee.cost());

        System.out.println("\n--- Strategy Pattern ---");
        System.out.println(new Checkout(new Card("1234567890123456")).execute(99.99));
        System.out.println(new Checkout(new PayPal("alice@x.com")).execute(49.99));
        System.out.println("\n  Common uses: Payment, Sorting, Compression, Validation, Auth");
    }
}
