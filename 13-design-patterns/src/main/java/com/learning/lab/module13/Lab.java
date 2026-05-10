package com.learning.lab.module13;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 13: Design Patterns ===");
        singletonDemo();
        factoryDemo();
        builderDemo();
        observerDemo();
        strategyDemo();
        decoratorDemo();
        adapterDemo();
        commandDemo();
    }

    static void singletonDemo() {
        System.out.println("\n--- Singleton Pattern ---");
        Database db1 = Database.getInstance();
        Database db2 = Database.getInstance();
        System.out.println("Same instance: " + (db1 == db2));
        db1.query("SELECT * FROM users");
    }

    static void factoryDemo() {
        System.out.println("\n--- Factory Pattern ---");
        AnimalFactory factory = new AnimalFactory();
        Animal dog = factory.createAnimal("dog");
        Animal cat = factory.createAnimal("cat");
        System.out.println("Dog says: " + dog.speak());
        System.out.println("Cat says: " + cat.speak());
    }

    static void builderDemo() {
        System.out.println("\n--- Builder Pattern ---");
        User user = new User.Builder()
            .name("John Doe")
            .email("john@example.com")
            .age(30)
            .phone("555-1234")
            .build();
        System.out.println("Built user: " + user);
    }

    static void observerDemo() {
        System.out.println("\n--- Observer Pattern ---");
        NewsAgency agency = new NewsAgency();
        NewsChannel cnn = new NewsChannel("CNN");
        NewsChannel bbc = new NewsChannel("BBC");

        agency.addObserver(cnn);
        agency.addObserver(bbc);
        agency.setNews("Breaking: Java 21 released!");
        agency.notifyObservers();
    }

    static void strategyDemo() {
        System.out.println("\n--- Strategy Pattern ---");
        ShoppingCart cart = new ShoppingCart();
        cart.addItem(new Item("Book", 50));
        cart.addItem(new Item("Movie", 20));

        cart.setPaymentStrategy(new CreditCardPayment("1234-5678-9012-3456"));
        cart.checkout(70);

        cart.setPaymentStrategy(new PayPalPayment("user@email.com"));
        cart.checkout(70);
    }

    static void decoratorDemo() {
        System.out.println("\n--- Decorator Pattern ---");
        Coffee coffee = new SimpleCoffee();
        System.out.println("Simple coffee: $" + coffee.getCost());

        Coffee milkCoffee = new MilkDecorator(coffee);
        System.out.println("With milk: $" + milkCoffee.getCost());

        Coffee fancyCoffee = new WhipDecorator(new MilkDecorator(new SimpleCoffee()));
        System.out.println("Fancy coffee: $" + fancyCoffee.getCost());
    }

    static void adapterDemo() {
        System.out.println("\n--- Adapter Pattern ---");
        MediaPlayer player = new MediaAdapter("mp3");
        player.play("song.mp3");

        player = new MediaAdapter("mp4");
        player.play("video.mp4");
    }

    static void commandDemo() {
        System.out.println("\n--- Command Pattern ---");
        RemoteControl remote = new RemoteControl();
        Light livingRoomLight = new Light("Living Room");

        remote.setCommand(new LightOnCommand(livingRoomLight));
        remote.pressButton();
        remote.setCommand(new LightOffCommand(livingRoomLight));
        remote.pressButton();
    }
}

class Database {
    private static Database instance;
    private Database() {}
    public static synchronized Database getInstance() {
        if (instance == null) instance = new Database();
        return instance;
    }
    public void query(String sql) { System.out.println("Executing: " + sql); }
}

interface Animal { String speak(); }
record Dog() implements Animal { public String speak() { return "Woof!"; } }
record Cat() implements Animal { public String speak() { return "Meow!"; } }
class AnimalFactory {
    public Animal createAnimal(String type) {
        return switch (type) {
            case "dog" -> new Dog();
            case "cat" -> new Cat();
            default -> throw new IllegalArgumentException("Unknown: " + type);
        };
    }
}

record User(String name, String email, int age, String phone) {
    static class Builder {
        private String name = "", email = "";
        private int age;
        private String phone = "";
        public Builder name(String v) { name = v; return this; }
        public Builder email(String v) { email = v; return this; }
        public Builder age(int v) { age = v; return this; }
        public Builder phone(String v) { phone = v; return this; }
        public User build() { return new User(name, email, age, phone); }
    }
}

interface Observer { void update(String news); }
class NewsAgency {
    private final List<Observer> observers = new ArrayList<>();
    private String news;
    public void addObserver(Observer o) { observers.add(o); }
    public void setNews(String n) { news = n; }
    public void notifyObservers() { observers.forEach(o -> o.update(news)); }
}
record NewsChannel(String name) implements Observer {
    public void update(String news) { System.out.println(name + " received: " + news); }
}

interface PaymentStrategy {
    void pay(double amount);
}
record CreditCardPayment(String cardNumber) implements PaymentStrategy {
    public void pay(double amount) { System.out.println("Paid $" + amount + " via card " + cardNumber); }
}
record PayPalPayment(String email) implements PaymentStrategy {
    public void pay(double amount) { System.out.println("Paid $" + amount + " via PayPal " + email); }
}
record Item(String name, double price) {}
class ShoppingCart {
    private final List<Item> items = new ArrayList<>();
    private PaymentStrategy strategy;
    public void addItem(Item i) { items.add(i); }
    public void setPaymentStrategy(PaymentStrategy s) { strategy = s; }
    public void checkout(double amount) { strategy.pay(amount); }
}

interface Coffee { double getCost(); String getDescription(); }
record SimpleCoffee() implements Coffee {
    public double getCost() { return 2.0; }
    public String getDescription() { return "Coffee"; }
}
record MilkDecorator(Coffee coffee) implements Coffee {
    public double getCost() { return coffee.getCost() + 0.5; }
    public String getDescription() { return coffee.getDescription() + ", Milk"; }
}
record WhipDecorator(Coffee coffee) implements Coffee {
    public double getCost() { return coffee.getCost() + 0.7; }
    public String getDescription() { return coffee.getDescription() + ", Whip"; }
}

interface MediaPlayer { void play(String filename); }
class MediaAdapter implements MediaPlayer {
    private final AdvancedMediaPlayer player;
    public MediaAdapter(String format) {
        player = switch (format) {
            case "mp3" -> new MP3Player();
            case "mp4" -> new MP4Player();
            default -> throw new IllegalArgumentException("Unsupported: " + format);
        };
    }
    public void play(String filename) { player.playAdvanced(filename); }
}
interface AdvancedMediaPlayer { void playAdvanced(String f); }
record MP3Player() implements AdvancedMediaPlayer { public void playAdvanced(String f) { System.out.println("Playing MP3: " + f); } }
record MP4Player() implements AdvancedMediaPlayer { public void playAdvanced(String f) { System.out.println("Playing MP4: " + f); } }

interface Command { void execute(); }
record LightOnCommand(Light light) implements Command { public void execute() { light.on(); } }
record LightOffCommand(Light light) implements Command { public void execute() { light.off(); } }
class Light {
    private final String location;
    public Light(String loc) { location = loc; }
    public void on() { System.out.println(location + " light ON"); }
    public void off() { System.out.println(location + " light OFF"); }
}
class RemoteControl {
    private Command command;
    public void setCommand(Command c) { command = c; }
    public void pressButton() { command.execute(); }
}

import java.util.ArrayList;
import java.util.List;
