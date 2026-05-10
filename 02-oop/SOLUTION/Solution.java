package com.learning.lab.module02.solution;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public static void main(String[] args) {
        System.out.println("=== Module 02: OOP Design Patterns - Complete Solution ===\n");

        System.out.println("=".repeat(60));
        System.out.println("CREATIONAL PATTERNS");
        System.out.println("=".repeat(60) + "\n");

        demonstrateFactoryPattern();
        demonstrateAbstractFactoryPattern();
        demonstrateSingletonPattern();
        demonstrateBuilderPattern();
        demonstratePrototypePattern();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("STRUCTURAL PATTERNS");
        System.out.println("=".repeat(60) + "\n");

        demonstrateAdapterPattern();
        demonstrateDecoratorPattern();
        demonstrateCompositePattern();
        demonstrateFacadePattern();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("BEHAVIORAL PATTERNS");
        System.out.println("=".repeat(60) + "\n");

        demonstrateObserverPattern();
        demonstrateStrategyPattern();
        demonstrateCommandPattern();
        demonstrateTemplateMethodPattern();
    }

    private static void demonstrateFactoryPattern() {
        System.out.println("--- Factory Pattern ---");

        DocumentFactory factory = new DocumentFactory();

        Document pdf = factory.createDocument("PDF");
        pdf.open();

        Document word = factory.createDocument("WORD");
        word.open();

        Document excel = factory.createDocument("EXCEL");
        excel.open();

        System.out.println();
    }

    private static void demonstrateAbstractFactoryPattern() {
        System.out.println("--- Abstract Factory Pattern ---");

        UIFactory windowsFactory = new WindowsFactory();
        UIFactory macFactory = new MacFactory();

        Button windowsButton = windowsFactory.createButton();
        Checkbox windowsCheckbox = windowsFactory.createCheckbox();
        windowsButton.render();
        windowsCheckbox.render();

        Button macButton = macFactory.createButton();
        Checkbox macCheckbox = macFactory.createCheckbox();
        macButton.render();
        macCheckbox.render();

        System.out.println();
    }

    private static void demonstrateSingletonPattern() {
        System.out.println("--- Singleton Pattern ---");

        DatabaseConnection db1 = DatabaseConnection.getInstance();
        DatabaseConnection db2 = DatabaseConnection.getInstance();

        System.out.println("Instance 1 hash: " + db1.hashCode());
        System.out.println("Instance 2 hash: " + db2.hashCode());
        System.out.println("Same instance: " + (db1 == db2));

        db1.connect();
        db1.executeQuery("SELECT * FROM users");

        System.out.println();
    }

    private static void demonstrateBuilderPattern() {
        System.out.println("--- Builder Pattern ---");

        Computer gamingPC = new Computer.ComputerBuilder()
                .setCPU("Intel i9")
                .setRAM("32GB")
                .setStorage("2TB SSD")
                .setGPU("RTX 4090")
                .build();

        System.out.println("Gaming PC: " + gamingPC);

        Computer officePC = new Computer.ComputerBuilder()
                .setCPU("Intel i5")
                .setRAM("16GB")
                .setStorage("512GB SSD")
                .build();

        System.out.println("Office PC: " + officePC);

        System.out.println();
    }

    private static void demonstratePrototypePattern() {
        System.out.println("--- Prototype Pattern ---");

        DocumentPrototype report = new Report("Annual Report", "2024", "CEO");
        DocumentPrototype reportClone = report.clone();
        ((Report)reportClone).setYear("2025");

        System.out.println("Original: " + report);
        System.out.println("Clone: " + reportClone);

        DocumentPrototype memo = new Memo("Confidential", "Team Lead");
        DocumentPrototype memoClone = memo.clone();

        System.out.println("Original Memo: " + memo);
        System.out.println("Clone Memo: " + memoClone);

        System.out.println();
    }

    private static void demonstrateAdapterPattern() {
        System.out.println("--- Adapter Pattern ---");

        PaymentProcessor processor = new PaymentProcessor();

        CreditCardAdapter creditAdapter = new CreditCardAdapter(new CreditCard());
        processor.processPayment(creditAdapter, 100.00);

        PayPalAdapter paypalAdapter = new PayPalAdapter(new PayPal());
        processor.processPayment(paypalAdapter, 200.00);

        CryptoAdapter cryptoAdapter = new CryptoAdapter(new Cryptocurrency());
        processor.processPayment(cryptoAdapter, 0.5);

        System.out.println();
    }

    private static void demonstrateDecoratorPattern() {
        System.out.println("--- Decorator Pattern ---");

        Coffee espresso = new EspressoCoffee();
        System.out.println("Espresso: " + espresso.getDescription() + " - $" + espresso.getCost());

        Coffee espressoWithMilk = new MilkDecorator(espresso);
        System.out.println("With Milk: " + espressoWithMilk.getDescription() + " - $" + espressoWithMilk.getCost());

        Coffee espressoWithMilkAndSugar = new SugarDecorator(espressoWithMilk);
        System.out.println("With Milk + Sugar: " + espressoWithMilkAndSugar.getDescription() + " - $" + espressoWithMilkAndSugar.getCost());

        Coffee cappuccino = new MilkDecorator(new MilkDecorator(new EspressoCoffee()));
        System.out.println("Cappuccino: " + cappuccino.getDescription() + " - $" + cappuccino.getCost());

        System.out.println();
    }

    private static void demonstrateCompositePattern() {
        System.out.println("--- Composite Pattern ---");

        FileComponent file1 = new File("document.txt", 100);
        FileComponent file2 = new File("image.jpg", 500);
        FileComponent file3 = new File("video.mp4", 1000);

        Folder documents = new Folder("Documents");
        documents.add(file1);

        Folder pictures = new Folder("Pictures");
        pictures.add(file2);

        Folder videos = new Folder("Videos");
        videos.add(file3);

        Folder root = new Folder("Root");
        root.add(documents);
        root.add(pictures);
        root.add(videos);

        System.out.println("Total size: " + root.getSize() + " KB");
        root.display("");

        System.out.println();
    }

    private static void demonstrateFacadePattern() {
        System.out.println("--- Facade Pattern ---");

        ComputerFacade computer = new ComputerFacade();

        computer.start();
        System.out.println("---");
        computer.shutdown();

        System.out.println();
    }

    private static void demonstrateObserverPattern() {
        System.out.println("--- Observer Pattern ---");

        NewsAgency newsAgency = new NewsAgency();

        NewsChannel channel1 = new NewsChannel("Channel 1");
        NewsChannel channel2 = new NewsChannel("Channel 2");

        newsAgency.addObserver(channel1);
        newsAgency.addObserver(channel2);

        newsAgency.setNews("Breaking: Java 21 Released!");
        newsAgency.setNews("Update: New Features in OOP");

        System.out.println();
    }

    private static void demonstrateStrategyPattern() {
        System.out.println("--- Strategy Pattern ---");

        PaymentContext context = new PaymentContext();

        context.setStrategy(new CreditCardPayment("1234-5678-9012-3456", "123"));
        context.pay(100.00);

        context.setStrategy(new PayPalPayment("user@email.com", "password"));
        context.pay(50.00);

        context.setStrategy(new CryptoPayment("wallet123", 0.01));
        context.pay(75.00);

        System.out.println();
    }

    private static void demonstrateCommandPattern() {
        System.out.println("--- Command Pattern ---");

        RemoteControl remote = new RemoteControl();

        Light livingRoomLight = new Light("Living Room");

        Command lightOn = new LightOnCommand(livingRoomLight);
        Command lightOff = new LightOffCommand(livingRoomLight);

        remote.setCommand(lightOn);
        remote.pressButton();

        remote.setCommand(lightOff);
        remote.pressButton();

        remote.undo();

        System.out.println();
    }

    private static void demonstrateTemplateMethodPattern() {
        System.out.println("--- Template Method Pattern ---");

        DataMiner csvMiner = new CSVDataMiner();
        System.out.println("Processing CSV:");
        csvMiner.extractData();

        System.out.println();

        DataMiner jsonMiner = new JSONDataMiner();
        System.out.println("Processing JSON:");
        jsonMiner.extractData();

        System.out.println();
    }
}

interface Document {
    void open();
    void save();
    void close();
}

class PDFDocument implements Document {
    public void open() { System.out.println("Opening PDF document"); }
    public void save() { System.out.println("Saving PDF document"); }
    public void close() { System.out.println("Closing PDF document"); }
}

class WordDocument implements Document {
    public void open() { System.out.println("Opening Word document"); }
    public void save() { System.out.println("Saving Word document"); }
    public void close() { System.out.println("Closing Word document"); }
}

class ExcelDocument implements Document {
    public void open() { System.out.println("Opening Excel document"); }
    public void save() { System.out.println("Saving Excel document"); }
    public void close() { System.out.println("Closing Excel document"); }
}

class DocumentFactory {
    public Document createDocument(String type) {
        return switch (type.toUpperCase()) {
            case "PDF" -> new PDFDocument();
            case "WORD" -> new WordDocument();
            case "EXCEL" -> new ExcelDocument();
            default -> throw new IllegalArgumentException("Unknown document type: " + type);
        };
    }
}

interface Button {
    void render();
}

interface Checkbox {
    void render();
}

class WindowsButton implements Button {
    public void render() { System.out.println("Rendering Windows button"); }
}

class MacButton implements Button {
    public void render() { System.out.println("Rendering Mac button"); }
}

class WindowsCheckbox implements Checkbox {
    public void render() { System.out.println("Rendering Windows checkbox"); }
}

class MacCheckbox implements Checkbox {
    public void render() { System.out.println("Rendering Mac checkbox"); }
}

interface UIFactory {
    Button createButton();
    Checkbox createCheckbox();
}

class WindowsFactory implements UIFactory {
    public Button createButton() { return new WindowsButton(); }
    public Checkbox createCheckbox() { return new WindowsCheckbox(); }
}

class MacFactory implements UIFactory {
    public Button createButton() { return new MacButton(); }
    public Checkbox createCheckbox() { return new MacCheckbox(); }
}

class DatabaseConnection {
    private static DatabaseConnection instance;

    private DatabaseConnection() {}

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public void connect() { System.out.println("Connecting to database"); }
    public void disconnect() { System.out.println("Disconnecting from database"); }
    public void executeQuery(String query) { System.out.println("Executing: " + query); }
}

class Computer {
    private String cpu;
    private String ram;
    private String storage;
    private String gpu;

    private Computer(ComputerBuilder builder) {
        this.cpu = builder.cpu;
        this.ram = builder.ram;
        this.storage = builder.storage;
        this.gpu = builder.gpu;
    }

    public String getCPU() { return cpu; }
    public String getRAM() { return ram; }
    public String getStorage() { return storage; }
    public String getGPU() { return gpu; }

    @Override
    public String toString() {
        return "Computer{cpu='" + cpu + "', ram='" + ram + "', storage='" + storage + "', gpu='" + gpu + "'}";
    }

    static class ComputerBuilder {
        private String cpu;
        private String ram;
        private String storage;
        private String gpu;

        public ComputerBuilder setCPU(String cpu) { this.cpu = cpu; return this; }
        public ComputerBuilder setRAM(String ram) { this.ram = ram; return this; }
        public ComputerBuilder setStorage(String storage) { this.storage = storage; return this; }
        public ComputerBuilder setGPU(String gpu) { this.gpu = gpu; return this; }

        public Computer build() {
            return new Computer(this);
        }
    }
}

abstract class DocumentPrototype {
    public abstract DocumentPrototype clone();
}

class Report extends DocumentPrototype {
    private String title;
    private String year;
    private String author;

    public Report(String title, String year, String author) {
        this.title = title;
        this.year = year;
        this.author = author;
    }

    public void setYear(String year) { this.year = year; }

    @Override
    public DocumentPrototype clone() {
        return new Report(this.title, this.year, this.author);
    }

    @Override
    public String toString() {
        return "Report{title='" + title + "', year='" + year + "', author='" + author + "'}";
    }
}

class Memo extends DocumentPrototype {
    private String classification;
    private String recipient;

    public Memo(String classification, String recipient) {
        this.classification = classification;
        this.recipient = recipient;
    }

    @Override
    public DocumentPrototype clone() {
        return new Memo(this.classification, this.recipient);
    }

    @Override
    public String toString() {
        return "Memo{classification='" + classification + "', recipient='" + recipient + "'}";
    }
}

interface Payment {
    void pay(double amount);
}

class CreditCard {
    public void payWithCard(String cardNumber, String cvv, double amount) {
        System.out.println("Paid $" + amount + " using Credit Card " + cardNumber);
    }
}

class PayPal {
    public void payWithPayPal(String email, String password, double amount) {
        System.out.println("Paid $" + amount + " using PayPal account " + email);
    }
}

class Cryptocurrency {
    public void payWithCrypto(String wallet, double amount, double cryptoAmount) {
        System.out.println("Paid " + cryptoAmount + " crypto from wallet " + wallet);
    }
}

class CreditCardAdapter implements Payment {
    private CreditCard creditCard;

    public CreditCardAdapter(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    public void pay(double amount) {
        creditCard.payWithCard("1234-5678-9012-3456", "123", amount);
    }
}

class PayPalAdapter implements Payment {
    private PayPal payPal;

    public PayPalAdapter(PayPal payPal) {
        this.payPal = payPal;
    }

    public void pay(double amount) {
        payPal.payWithPayPal("user@email.com", "password", amount);
    }
}

class CryptoAdapter implements Payment {
    private Cryptocurrency crypto;

    public CryptoAdapter(Cryptocurrency crypto) {
        this.crypto = crypto;
    }

    public void pay(double amount) {
        crypto.payWithCrypto("wallet123", amount, amount / 1500);
    }
}

class PaymentProcessor {
    public void processPayment(Payment payment, double amount) {
        payment.pay(amount);
    }
}

interface Coffee {
    String getDescription();
    double getCost();
}

class EspressoCoffee implements Coffee {
    public String getDescription() { return "Espresso"; }
    public double getCost() { return 3.00; }
}

class CoffeeDecorator implements Coffee {
    protected Coffee coffee;

    public CoffeeDecorator(Coffee coffee) { this.coffee = coffee; }
    public String getDescription() { return coffee.getDescription(); }
    public double getCost() { return coffee.getCost(); }
}

class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) { super(coffee); }
    public String getDescription() { return super.getDescription() + ", Milk"; }
    public double getCost() { return super.getCost() + 0.50; }
}

class SugarDecorator extends CoffeeDecorator {
    public SugarDecorator(Coffee coffee) { super(coffee); }
    public String getDescription() { return super.getDescription() + ", Sugar"; }
    public double getCost() { return super.getCost() + 0.25; }
}

abstract class FileComponent {
    abstract String getName();
    abstract int getSize();
    abstract void display(String indent);
}

class File extends FileComponent {
    private String name;
    private int size;

    public File(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public String getName() { return name; }
    public int getSize() { return size; }
    public void display(String indent) { System.out.println(indent + "- File: " + name + " (" + size + " KB)"); }
}

class Folder extends FileComponent {
    private String name;
    private List<FileComponent> components = new ArrayList<>();

    public Folder(String name) { this.name = name; }

    public String getName() { return name; }
    public void add(FileComponent component) { components.add(component); }

    public int getSize() {
        int total = 0;
        for (FileComponent comp : components) {
            total += comp.getSize();
        }
        return total;
    }

    public void display(String indent) {
        System.out.println(indent + "+ Folder: " + name);
        for (FileComponent comp : components) {
            comp.display(indent + "  ");
        }
    }
}

class CPU { public void freeze() { System.out.println("CPU freezing"); } }
class Memory { public void load(byte[] data) { System.out.println("Memory loading data"); } }
class HardDrive { public void read(byte[] sector) { System.out.println("Hard drive reading"); } }
class Display { public void render() { System.out.println("Display rendering"); } }

class ComputerFacade {
    private CPU cpu = new CPU();
    private Memory memory = new Memory();
    private HardDrive hardDrive = new HardDrive();
    private Display display = new Display();

    public void start() {
        System.out.println("Computer starting...");
        cpu.freeze();
        memory.load(new byte[1024]);
        hardDrive.read(new byte[512]);
        display.render();
        System.out.println("Computer started!");
    }

    public void shutdown() {
        System.out.println("Computer shutting down...");
    }
}

interface Observer {
    void update(String news);
}

class NewsChannel implements Observer {
    private String name;

    public NewsChannel(String name) { this.name = name; }

    public void update(String news) {
        System.out.println(name + " received: " + news);
    }
}

class NewsAgency {
    private List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) { observers.add(observer); }
    public void removeObserver(Observer observer) { observers.remove(observer); }

    public void setNews(String news) {
        notifyObservers(news);
    }

    private void notifyObservers(String news) {
        for (Observer observer : observers) {
            observer.update(news);
        }
    }
}

interface PaymentStrategy {
    void pay(double amount);
}

class CreditCardPayment implements PaymentStrategy {
    private String cardNumber;
    private String cvv;

    public CreditCardPayment(String cardNumber, String cvv) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
    }

    public void pay(double amount) {
        System.out.println("Paid $" + amount + " with Credit Card ending in " + cardNumber.substring(12));
    }
}

class PayPalPayment implements PaymentStrategy {
    private String email;
    private String password;

    public PayPalPayment(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public void pay(double amount) {
        System.out.println("Paid $" + amount + " with PayPal account " + email);
    }
}

class CryptoPayment implements PaymentStrategy {
    private String wallet;
    private double cryptoAmount;

    public CryptoPayment(String wallet, double cryptoAmount) {
        this.wallet = wallet;
        this.cryptoAmount = cryptoAmount;
    }

    public void pay(double amount) {
        System.out.println("Paid " + (amount / 1500) + " ETH from wallet " + wallet);
    }
}

class PaymentContext {
    private PaymentStrategy strategy;

    public void setStrategy(PaymentStrategy strategy) { this.strategy = strategy; }
    public void pay(double amount) { strategy.pay(amount); }
}

interface Command {
    void execute();
    void undo();
}

class Light {
    private String location;

    public Light(String location) { this.location = location; }

    public void on() { System.out.println(location + " light is ON"); }
    public void off() { System.out.println(location + " light is OFF"); }
}

class LightOnCommand implements Command {
    private Light light;

    public LightOnCommand(Light light) { this.light = light; }

    public void execute() { light.on(); }
    public void undo() { light.off(); }
}

class LightOffCommand implements Command {
    private Light light;

    public LightOffCommand(Light light) { this.light = light; }

    public void execute() { light.off(); }
    public void undo() { light.on(); }
}

class RemoteControl {
    private Command command;

    public void setCommand(Command command) { this.command = command; }
    public void pressButton() { command.execute(); }
    public void undo() { command.undo(); }
}

abstract class DataMiner {
    public void extractData() {
        openFile();
        parseData();
        analyzeData();
        sendReport();
    }

    abstract void openFile();
    abstract void parseData();
    abstract void analyzeData();

    private void sendReport() { System.out.println("Sending report..."); }
}

class CSVDataMiner extends DataMiner {
    void openFile() { System.out.println("Opening CSV file"); }
    void parseData() { System.out.println("Parsing CSV data"); }
    void analyzeData() { System.out.println("Analyzing CSV data"); }
}

class JSONDataMiner extends DataMiner {
    void openFile() { System.out.println("Opening JSON file"); }
    void parseData() { System.out.println("Parsing JSON data"); }
    void analyzeData() { System.out.println("Analyzing JSON data"); }
}