package com.learning.lab.module13.solution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Solution {

    // Singleton Pattern - Thread-safe implementation
    public static class Singleton {
        private static volatile Singleton instance;
        private final String data;

        private Singleton(String data) {
            this.data = data;
        }

        public static Singleton getInstance(String data) {
            if (instance == null) {
                synchronized (Singleton.class) {
                    if (instance == null) {
                        instance = new Singleton(data);
                    }
                }
            }
            return instance;
        }

        public String getData() {
            return data;
        }
    }

    // Factory Pattern - Interface and implementations
    public interface Notification {
        void send(String message);
    }

    public static class EmailNotification implements Notification {
        @Override
        public void send(String message) {
            System.out.println("Email sent: " + message);
        }
    }

    public static class SMSNotification implements Notification {
        @Override
        public void send(String message) {
            System.out.println("SMS sent: " + message);
        }
    }

    public static class PushNotification implements Notification {
        @Override
        public void send(String message) {
            System.out.println("Push notification: " + message);
        }
    }

    public static class NotificationFactory {
        public static Notification createNotification(String type) {
            return switch (type.toLowerCase()) {
                case "email" -> new EmailNotification();
                case "sms" -> new SMSNotification();
                case "push" -> new PushNotification();
                default -> throw new IllegalArgumentException("Unknown notification type: " + type);
            };
        }
    }

    // Builder Pattern
    public static class User {
        private final String name;
        private final String email;
        private final int age;
        private final String address;

        private User(UserBuilder builder) {
            this.name = builder.name;
            this.email = builder.email;
            this.age = builder.age;
            this.address = builder.address;
        }

        public String getName() { return name; }
        public String getEmail() { return email; }
        public int getAge() { return age; }
        public String getAddress() { return address; }

        public static class UserBuilder {
            private String name;
            private String email;
            private int age;
            private String address;

            public UserBuilder name(String name) {
                this.name = name;
                return this;
            }

            public UserBuilder email(String email) {
                this.email = email;
                return this;
            }

            public UserBuilder age(int age) {
                this.age = age;
                return this;
            }

            public UserBuilder address(String address) {
                this.address = address;
                return this;
            }

            public User build() {
                return new User(this);
            }
        }
    }

    // Observer Pattern
    public interface Observer {
        void update(String event);
    }

    public static class Subject {
        private final List<Observer> observers = new ArrayList<>();

        public void attach(Observer observer) {
            observers.add(observer);
        }

        public void detach(Observer observer) {
            observers.remove(observer);
        }

        public void notifyObservers(String event) {
            for (Observer observer : observers) {
                observer.update(event);
            }
        }
    }

    public static class ConcreteObserver implements Observer {
        private final String name;

        public ConcreteObserver(String name) {
            this.name = name;
        }

        @Override
        public void update(String event) {
            System.out.println(name + " received: " + event);
        }
    }

    // Strategy Pattern
    public interface PaymentStrategy {
        void pay(double amount);
    }

    public static class CreditCardPayment implements PaymentStrategy {
        private final String cardNumber;

        public CreditCardPayment(String cardNumber) {
            this.cardNumber = cardNumber;
        }

        @Override
        public void pay(double amount) {
            System.out.println("Paid " + amount + " using Credit Card: " + cardNumber);
        }
    }

    public static class PayPalPayment implements PaymentStrategy {
        private final String email;

        public PayPalPayment(String email) {
            this.email = email;
        }

        @Override
        public void pay(double amount) {
            System.out.println("Paid " + amount + " using PayPal: " + email);
        }
    }

    public static class ShoppingCart {
        private final PaymentStrategy paymentStrategy;

        public ShoppingCart(PaymentStrategy paymentStrategy) {
            this.paymentStrategy = paymentStrategy;
        }

        public void checkout(double amount) {
            paymentStrategy.pay(amount);
        }
    }

    // Adapter Pattern
    public interface MediaPlayer {
        void play(String filename);
    }

    public static class AdvancedMediaPlayer {
        public void playMp4(String filename) {
            System.out.println("Playing MP4: " + filename);
        }

        public void playVlc(String filename) {
            System.out.println("Playing VLC: " + filename);
        }
    }

    public static class MediaAdapter implements MediaPlayer {
        private final AdvancedMediaPlayer advancedPlayer;

        public MediaAdapter() {
            this.advancedPlayer = new AdvancedMediaPlayer();
        }

        @Override
        public void play(String filename) {
            if (filename.endsWith(".mp4")) {
                advancedPlayer.playMp4(filename);
            } else if (filename.endsWith(".vlc")) {
                advancedPlayer.playVlc(filename);
            }
        }
    }

    // Decorator Pattern
    public interface Coffee {
        String getDescription();
        double getCost();
    }

    public static class SimpleCoffee implements Coffee {
        @Override
        public String getDescription() {
            return "Simple Coffee";
        }

        @Override
        public double getCost() {
            return 2.0;
        }
    }

    public static abstract class CoffeeDecorator implements Coffee {
        protected Coffee coffee;

        public CoffeeDecorator(Coffee coffee) {
            this.coffee = coffee;
        }
    }

    public static class MilkDecorator extends CoffeeDecorator {
        public MilkDecorator(Coffee coffee) {
            super(coffee);
        }

        @Override
        public String getDescription() {
            return coffee.getDescription() + ", Milk";
        }

        @Override
        public double getCost() {
            return coffee.getCost() + 0.5;
        }
    }

    public static class SugarDecorator extends CoffeeDecorator {
        public SugarDecorator(Coffee coffee) {
            super(coffee);
        }

        @Override
        public String getDescription() {
            return coffee.getDescription() + ", Sugar";
        }

        @Override
        public double getCost() {
            return coffee.getCost() + 0.2;
        }
    }

    // Facade Pattern
    public static class ComputerFacade {
        private final CPU cpu;
        private final Memory memory;
        private final Storage storage;

        public ComputerFacade() {
            this.cpu = new CPU();
            this.memory = new Memory();
            this.storage = new Storage();
        }

        public void start() {
            cpu.start();
            memory.load();
            storage.read();
            System.out.println("Computer started successfully");
        }

        public void shutdown() {
            storage.write();
            memory.clear();
            cpu.stop();
            System.out.println("Computer shut down");
        }
    }

    public static class CPU {
        public void start() { System.out.println("CPU starting..."); }
        public void stop() { System.out.println("CPU stopping..."); }
    }

    public static class Memory {
        public void load() { System.out.println("Memory loading..."); }
        public void clear() { System.out.println("Memory clearing..."); }
    }

    public static class Storage {
        public void read() { System.out.println("Storage reading..."); }
        public void write() { System.out.println("Storage writing..."); }
    }

    // Command Pattern
    public interface Command {
        void execute();
        void undo();
    }

    public static class Light {
        public void on() { System.out.println("Light is ON"); }
        public void off() { System.out.println("Light is OFF"); }
    }

    public static class LightOnCommand implements Command {
        private final Light light;

        public LightOnCommand(Light light) {
            this.light = light;
        }

        @Override
        public void execute() {
            light.on();
        }

        @Override
        public void undo() {
            light.off();
        }
    }

    public static class LightOffCommand implements Command {
        private final Light light;

        public LightOffCommand(Light light) {
            this.light = light;
        }

        @Override
        public void execute() {
            light.off();
        }

        @Override
        public void undo() {
            light.on();
        }
    }

    public static class RemoteControl {
        private Command lastCommand;

        public void executeCommand(Command command) {
            command.execute();
            lastCommand = command;
        }

        public void undoLast() {
            if (lastCommand != null) {
                lastCommand.undo();
            }
        }
    }

    // State Pattern
    public interface State {
        void handle(Context context);
    }

    public static class Context {
        private State state;

        public void setState(State state) {
            this.state = state;
        }

        public void request() {
            if (state != null) {
                state.handle(this);
            }
        }
    }

    public static class StateA implements State {
        @Override
        public void handle(Context context) {
            System.out.println("Handling State A, switching to State B");
            context.setState(new StateB());
        }
    }

    public static class StateB implements State {
        @Override
        public void handle(Context context) {
            System.out.println("Handling State B, switching to State A");
            context.setState(new StateA());
        }
    }

    // Template Method Pattern
    public abstract static class DataMiner {
        public void mine(String path) {
            String file = openFile(path);
            String data = extractData(file);
            String parsed = parseData(data);
            sendReport(parsed);
            closeFile(file);
        }

        protected String openFile(String path) {
            return path;
        }

        protected abstract String extractData(String file);
        protected abstract String parseData(String data);

        protected void sendReport(String data) {
            System.out.println("Report: " + data);
        }

        protected void closeFile(String file) {
            System.out.println("File closed");
        }
    }

    public static class PDFDataMiner extends DataMiner {
        @Override
        protected String extractData(String file) {
            return "PDF data from " + file;
        }

        @Override
        protected String parseData(String data) {
            return "Parsed: " + data;
        }
    }

    public static class CSVDataMiner extends DataMiner {
        @Override
        protected String extractData(String file) {
            return "CSV data from " + file;
        }

        @Override
        protected String parseData(String data) {
            return "Parsed: " + data;
        }
    }

    // Proxy Pattern
    public interface Image {
        void display();
    }

    public static class RealImage implements Image {
        private final String filename;

        public RealImage(String filename) {
            this.filename = filename;
            loadFromDisk();
        }

        private void loadFromDisk() {
            System.out.println("Loading: " + filename);
        }

        @Override
        public void display() {
            System.out.println("Displaying: " + filename);
        }
    }

    public static class ImageProxy implements Image {
        private RealImage realImage;
        private final String filename;

        public ImageProxy(String filename) {
            this.filename = filename;
        }

        @Override
        public void display() {
            if (realImage == null) {
                realImage = new RealImage(filename);
            }
            realImage.display();
        }
    }

    // Chain of Responsibility Pattern
    public abstract static class Handler {
        private Handler next;

        public Handler setNext(Handler handler) {
            this.next = handler;
            return handler;
        }

        public void handle(String request) {
            if (process(request)) {
                return;
            }
            if (next != null) {
                next.handle(request);
            }
        }

        protected abstract boolean process(String request);
    }

    public static class AuthHandler extends Handler {
        @Override
        protected boolean process(String request) {
            if (request.startsWith("auth:")) {
                System.out.println("AuthHandler: Processing " + request);
                return true;
            }
            return false;
        }
    }

    public static class LogHandler extends Handler {
        @Override
        protected boolean process(String request) {
            if (request.startsWith("log:")) {
                System.out.println("LogHandler: Processing " + request);
                return true;
            }
            return false;
        }
    }

    public static class ValidateHandler extends Handler {
        @Override
        protected boolean process(String request) {
            if (request.startsWith("validate:")) {
                System.out.println("ValidateHandler: Processing " + request);
                return true;
            }
            return false;
        }
    }

    // Composite Pattern
    public static abstract class FileSystemComponent {
        protected String name;

        public FileSystemComponent(String name) {
            this.name = name;
        }

        public abstract long getSize();
        public abstract void ls(String indent);
    }

    public static class File extends FileSystemComponent {
        private final long size;

        public File(String name, long size) {
            super(name);
            this.size = size;
        }

        @Override
        public long getSize() {
            return size;
        }

        @Override
        public void ls(String indent) {
            System.out.println(indent + "- " + name + " (" + size + " bytes)");
        }
    }

    public static class Directory extends FileSystemComponent {
        private final List<FileSystemComponent> children = new ArrayList<>();

        public Directory(String name) {
            super(name);
        }

        public void add(FileSystemComponent component) {
            children.add(component);
        }

        @Override
        public long getSize() {
            return children.stream().mapToLong(FileSystemComponent::getSize).sum();
        }

        @Override
        public void ls(String indent) {
            System.out.println(indent + "+ " + name + "/");
            for (FileSystemComponent child : children) {
                child.ls(indent + "  ");
            }
        }
    }

    // Iterator Pattern
    public interface Iterator<T> {
        boolean hasNext();
        T next();
    }

    public static class ListIterator<T> implements Iterator<T> {
        private final List<T> list;
        private int position = 0;

        public ListIterator(List<T> list) {
            this.list = list;
        }

        @Override
        public boolean hasNext() {
            return position < list.size();
        }

        @Override
        public T next() {
            return list.get(position++);
        }
    }

    // Memento Pattern
    public static class Memento {
        private final String state;

        public Memento(String state) {
            this.state = state;
        }

        public String getState() {
            return state;
        }
    }

    public static class Originator {
        private String state;

        public void setState(String state) {
            this.state = state;
        }

        public String getState() {
            return state;
        }

        public Memento save() {
            return new Memento(state);
        }

        public void restore(Memento memento) {
            this.state = memento.getState();
        }
    }

    public static class Caretaker {
        private final List<Memento> mementos = new ArrayList<>();

        public void addMemento(Memento memento) {
            mementos.add(memento);
        }

        public Memento getMemento(int index) {
            if (index >= 0 && index < mementos.size()) {
                return mementos.get(index);
            }
            return null;
        }
    }

    // Flyweight Pattern
    public static class TreeType {
        private final String name;
        private final String color;
        private final String texture;

        public TreeType(String name, String color, String texture) {
            this.name = name;
            this.color = color;
            this.texture = texture;
        }

        public void draw(int x, int y) {
            System.out.println("Drawing " + name + " tree at (" + x + ", " + y + ")");
        }
    }

    public static class TreeFactory {
        private static final Map<String, TreeType> treeTypes = new HashMap<>();

        public static TreeType getTreeType(String name, String color, String texture) {
            String key = name + "-" + color + "-" + texture;
            if (!treeTypes.containsKey(key)) {
                treeTypes.put(key, new TreeType(name, color, texture));
                System.out.println("Created new TreeType: " + key);
            }
            return treeTypes.get(key);
        }
    }

    public static class Tree {
        private int x, y;
        private TreeType type;

        public Tree(int x, int y, TreeType type) {
            this.x = x;
            this.y = y;
            this.type = type;
        }

        public void draw() {
            type.draw(x, y);
        }
    }

    public static void demonstrateAllPatterns() {
        System.out.println("=== Singleton ===");
        Singleton s1 = Singleton.getInstance("test");
        Singleton s2 = Singleton.getInstance("test2");
        System.out.println("s1 data: " + s1.getData());
        System.out.println("s1 == s2: " + (s1 == s2));

        System.out.println("\n=== Factory ===");
        Notification email = NotificationFactory.createNotification("email");
        email.send("Hello");

        System.out.println("\n=== Builder ===");
        User user = new User.UserBuilder()
            .name("John")
            .email("john@test.com")
            .age(30)
            .build();
        System.out.println("User: " + user.getName());

        System.out.println("\n=== Observer ===");
        Subject subject = new Subject();
        subject.attach(new ConcreteObserver("Observer1"));
        subject.attach(new ConcreteObserver("Observer2"));
        subject.notifyObservers("Event!");

        System.out.println("\n=== Strategy ===");
        ShoppingCart cart = new ShoppingCart(new CreditCardPayment("1234"));
        cart.checkout(100.0);

        System.out.println("\n=== Adapter ===");
        MediaPlayer player = new MediaAdapter();
        player.play("test.mp4");

        System.out.println("\n=== Decorator ===");
        Coffee coffee = new MilkDecorator(new SugarDecorator(new SimpleCoffee()));
        System.out.println(coffee.getDescription() + " = $" + coffee.getCost());

        System.out.println("\n=== Facade ===");
        ComputerFacade computer = new ComputerFacade();
        computer.start();
        computer.shutdown();

        System.out.println("\n=== Command ===");
        Light light = new Light();
        RemoteControl remote = new RemoteControl();
        remote.executeCommand(new LightOnCommand(light));
        remote.undoLast();

        System.out.println("\n=== State ===");
        Context context = new Context();
        context.setState(new StateA());
        context.request();
        context.request();

        System.out.println("\n=== Template Method ===");
        DataMiner pdfMiner = new PDFDataMiner();
        pdfMiner.mine("file.pdf");

        System.out.println("\n=== Proxy ===");
        Image image = new ImageProxy("photo.jpg");
        image.display();

        System.out.println("\n=== Chain of Responsibility ===");
        Handler chain = new AuthHandler().setNext(new LogHandler()).setNext(new ValidateHandler());
        chain.handle("auth:login");
        chain.handle("validate:data");

        System.out.println("\n=== Composite ===");
        Directory root = new Directory("root");
        root.add(new File("a.txt", 100));
        Directory sub = new Directory("sub");
        sub.add(new File("b.txt", 200));
        root.add(sub);
        root.ls("");

        System.out.println("\n=== Iterator ===");
        List<String> list = List.of("a", "b", "c");
        Iterator<String> iter = new ListIterator<>(list);
        while (iter.hasNext()) {
            System.out.println(iter.next());
        }

        System.out.println("\n=== Memento ===");
        Originator originator = new Originator();
        Caretaker caretaker = new Caretaker();
        originator.setState("State1");
        caretaker.addMemento(originator.save());
        originator.setState("State2");
        originator.restore(caretaker.getMemento(0));
        System.out.println("Restored: " + originator.getState());

        System.out.println("\n=== Flyweight ===");
        TreeType oak = TreeFactory.getTreeType("Oak", "Green", "Oak texture");
        Tree t1 = new Tree(1, 2, oak);
        Tree t2 = new Tree(3, 4, oak);
        t1.draw();
        t2.draw();
    }

    public static void main(String[] args) {
        demonstrateAllPatterns();
    }
}