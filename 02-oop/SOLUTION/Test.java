package com.learning.lab.module02.solution;

import java.util.ArrayList;
import java.util.List;

public class Test {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== Module 02: OOP Design Patterns - Comprehensive Tests ===\n");

        testFactoryPattern();
        testAbstractFactoryPattern();
        testSingletonPattern();
        testBuilderPattern();
        testPrototypePattern();
        testAdapterPattern();
        testDecoratorPattern();
        testCompositePattern();
        testFacadePattern();
        testObserverPattern();
        testStrategyPattern();
        testCommandPattern();
        testTemplateMethodPattern();

        printSummary();
    }

    private static void testFactoryPattern() {
        System.out.println("--- Testing Factory Pattern ---");

        test("Factory creates PDF", () -> {
            DocumentFactory factory = new DocumentFactory();
            Document doc = factory.createDocument("PDF");
            assert doc instanceof PDFDocument : "Should create PDFDocument";
        });

        test("Factory creates Word", () -> {
            DocumentFactory factory = new DocumentFactory();
            Document doc = factory.createDocument("WORD");
            assert doc instanceof WordDocument : "Should create WordDocument";
        });

        test("Factory creates Excel", () -> {
            DocumentFactory factory = new DocumentFactory();
            Document doc = factory.createDocument("EXCEL");
            assert doc instanceof ExcelDocument : "Should create ExcelDocument";
        });

        test("Factory throws for unknown type", () -> {
            DocumentFactory factory = new DocumentFactory();
            boolean thrown = false;
            try {
                factory.createDocument("UNKNOWN");
            } catch (IllegalArgumentException e) {
                thrown = true;
            }
            assert thrown : "Should throw for unknown type";
        });

        System.out.println();
    }

    private static void testAbstractFactoryPattern() {
        System.out.println("--- Testing Abstract Factory Pattern ---");

        test("Windows factory creates Windows components", () -> {
            UIFactory factory = new WindowsFactory();
            assert factory.createButton() instanceof WindowsButton : "Should create Windows button";
            assert factory.createCheckbox() instanceof WindowsCheckbox : "Should create Windows checkbox";
        });

        test("Mac factory creates Mac components", () -> {
            UIFactory factory = new MacFactory();
            assert factory.createButton() instanceof MacButton : "Should create Mac button";
            assert factory.createCheckbox() instanceof MacCheckbox : "Should create Mac checkbox";
        });

        System.out.println();
    }

    private static void testSingletonPattern() {
        System.out.println("--- Testing Singleton Pattern ---");

        test("Singleton returns same instance", () -> {
            DatabaseConnection instance1 = DatabaseConnection.getInstance();
            DatabaseConnection instance2 = DatabaseConnection.getInstance();
            assert instance1 == instance2 : "Should return same instance";
        });

        test("Singleton is thread-safe", () -> {
            List<DatabaseConnection> instances = new ArrayList<>();
            Thread t1 = new Thread(() -> instances.add(DatabaseConnection.getInstance()));
            Thread t2 = new Thread(() -> instances.add(DatabaseConnection.getInstance()));
            t1.start();
            t2.start();
            t1.join();
            t2.join();
            assert instances.get(0) == instances.get(1) : "Should be thread-safe";
        });

        System.out.println();
    }

    private static void testBuilderPattern() {
        System.out.println("--- Testing Builder Pattern ---");

        test("Builder creates computer with all fields", () -> {
            Computer pc = new Computer.ComputerBuilder()
                    .setCPU("Intel i9")
                    .setRAM("32GB")
                    .setStorage("2TB SSD")
                    .setGPU("RTX 4090")
                    .build();

            assert pc.getCPU().equals("Intel i9") : "CPU should match";
            assert pc.getRAM().equals("32GB") : "RAM should match";
            assert pc.getStorage().equals("2TB SSD") : "Storage should match";
            assert pc.getGPU().equals("RTX 4090") : "GPU should match";
        });

        test("Builder creates computer with optional fields", () -> {
            Computer pc = new Computer.ComputerBuilder()
                    .setCPU("Intel i5")
                    .setRAM("16GB")
                    .build();

            assert pc.getCPU().equals("Intel i5") : "CPU should match";
            assert pc.getRAM().equals("16GB") : "RAM should match";
            assert pc.getGPU() == null : "GPU should be null";
        });

        System.out.println();
    }

    private static void testPrototypePattern() {
        System.out.println("--- Testing Prototype Pattern ---");

        test("Prototype creates independent clone", () -> {
            Report original = new Report("Report", "2024", "Author");
            Report clone = (Report) original.clone();
            clone.setYear("2025");

            assert original.toString().contains("2024") : "Original year should be unchanged";
            assert clone.toString().contains("2025") : "Clone year should be different";
        });

        test("Prototype clones different types", () -> {
            Memo memo = new Memo("Confidential", "Manager");
            DocumentPrototype clone = memo.clone();
            assert clone instanceof Memo : "Should clone Memo type";
        });

        System.out.println();
    }

    private static void testAdapterPattern() {
        System.out.println("--- Testing Adapter Pattern ---");

        test("Credit card adapter works", () -> {
            CreditCardAdapter adapter = new CreditCardAdapter(new CreditCard());
            PaymentProcessor processor = new PaymentProcessor();
            processor.processPayment(adapter, 100.0);
        });

        test("PayPal adapter works", () -> {
            PayPalAdapter adapter = new PayPalAdapter(new PayPal());
            PaymentProcessor processor = new PaymentProcessor();
            processor.processPayment(adapter, 50.0);
        });

        test("Crypto adapter works", () -> {
            CryptoAdapter adapter = new CryptoAdapter(new Cryptocurrency());
            PaymentProcessor processor = new PaymentProcessor();
            processor.processPayment(adapter, 0.5);
        });

        System.out.println();
    }

    private static void testDecoratorPattern() {
        System.out.println("--- Testing Decorator Pattern ---");

        test("Base coffee has correct cost", () -> {
            Coffee coffee = new EspressoCoffee();
            assert coffee.getCost() == 3.00 : "Espresso should cost $3.00";
        });

        test("Milk decorator adds cost", () -> {
            Coffee coffee = new EspressoCoffee();
            Coffee withMilk = new MilkDecorator(coffee);
            assert withMilk.getCost() == 3.50 : "Should add $0.50";
        });

        test("Decorator adds description", () -> {
            Coffee coffee = new EspressoCoffee();
            Coffee withMilk = new MilkDecorator(coffee);
            assert withMilk.getDescription().contains("Milk") : "Should include Milk";
        });

        test("Multiple decorators stack", () -> {
            Coffee coffee = new SugarDecorator(new MilkDecorator(new EspressoCoffee()));
            assert coffee.getCost() == 3.75 : "Total should be $3.75";
            assert coffee.getDescription().contains("Milk") : "Should include Milk";
            assert coffee.getDescription().contains("Sugar") : "Should include Sugar";
        });

        System.out.println();
    }

    private static void testCompositePattern() {
        System.out.println("--- Testing Composite Pattern ---");

        test("File has correct size", () -> {
            File file = new File("test.txt", 100);
            assert file.getSize() == 100 : "File size should be 100";
        });

        test("Folder calculates total size", () -> {
            Folder folder = new Folder("Test");
            folder.add(new File("f1.txt", 100));
            folder.add(new File("f2.txt", 200));
            assert folder.getSize() == 300 : "Total should be 300";
        });

        test("Folder contains nested folders", () -> {
            Folder root = new Folder("root");
            Folder sub = new Folder("sub");
            sub.add(new File("data.txt", 50));
            root.add(sub);
            assert root.getSize() == 50 : "Should include nested size";
        });

        System.out.println();
    }

    private static void testFacadePattern() {
        System.out.println("--- Testing Facade Pattern ---");

        test("Facade simplifies complex system", () -> {
            ComputerFacade facade = new ComputerFacade();
            facade.start();
        });

        System.out.println();
    }

    private static void testObserverPattern() {
        System.out.println("--- Testing Observer Pattern ---");

        test("Observer receives updates", () -> {
            NewsAgency agency = new NewsAgency();
            NewsChannel channel = new NewsChannel("TestChannel");
            agency.addObserver(channel);
            agency.setNews("Test News");
        });

        test("Multiple observers receive updates", () -> {
            NewsAgency agency = new NewsAgency();
            NewsChannel ch1 = new NewsChannel("Channel1");
            NewsChannel ch2 = new NewsChannel("Channel2");
            agency.addObserver(ch1);
            agency.addObserver(ch2);
            agency.setNews("Breaking News");
        });

        test("Observer can be removed", () -> {
            NewsAgency agency = new NewsAgency();
            NewsChannel channel = new NewsChannel("Channel");
            agency.addObserver(channel);
            agency.removeObserver(channel);
            agency.setNews("No observer should get this");
        });

        System.out.println();
    }

    private static void testStrategyPattern() {
        System.out.println("--- Testing Strategy Pattern ---");

        test("Credit card strategy", () -> {
            PaymentContext context = new PaymentContext();
            context.setStrategy(new CreditCardPayment("1234567890123456", "123"));
            context.pay(100.0);
        });

        test("PayPal strategy", () -> {
            PaymentContext context = new PaymentContext();
            context.setStrategy(new PayPalPayment("test@email.com", "pass"));
            context.pay(50.0);
        });

        test("Strategy can be switched at runtime", () -> {
            PaymentContext context = new PaymentContext();
            context.setStrategy(new CreditCardPayment("1234", "123"));
            context.pay(100.0);
            context.setStrategy(new PayPalPayment("test", "pass"));
            context.pay(100.0);
        });

        System.out.println();
    }

    private static void testCommandPattern() {
        System.out.println("--- Testing Command Pattern ---");

        test("Command executes action", () -> {
            Light light = new Light("Test");
            Command onCommand = new LightOnCommand(light);
            onCommand.execute();
            onCommand.undo();
        });

        test("Command can be undone", () -> {
            Light light = new Light("Test");
            Command onCommand = new LightOnCommand(light);
            Command offCommand = new LightOffCommand(light);

            onCommand.execute();
            onCommand.undo();
        });

        test("Remote control can change command", () -> {
            RemoteControl remote = new RemoteControl();
            Light light = new Light("Test");

            remote.setCommand(new LightOnCommand(light));
            remote.pressButton();

            remote.setCommand(new LightOffCommand(light));
            remote.pressButton();
        });

        System.out.println();
    }

    private static void testTemplateMethodPattern() {
        System.out.println("--- Testing Template Method Pattern ---");

        test("Template method executes all steps", () -> {
            DataMiner csvMiner = new CSVDataMiner();
            csvMiner.extractData();
        });

        test("Concrete implementations override steps", () -> {
            DataMiner csvMiner = new CSVDataMiner();
            DataMiner jsonMiner = new JSONDataMiner();
            csvMiner.extractData();
            jsonMiner.extractData();
        });

        System.out.println();
    }

    private static void test(String name, Runnable test) {
        try {
            test.run();
            System.out.println("  PASS: " + name);
            passed++;
        } catch (AssertionError | Exception e) {
            System.out.println("  FAIL: " + name + " - " + e.getMessage());
            failed++;
        }
    }

    private static void printSummary() {
        System.out.println("=== Test Summary ===");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Total: " + (passed + failed));
        System.out.println("===================");
    }
}