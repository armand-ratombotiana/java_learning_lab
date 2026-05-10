package com.learning.lab.module13.solution;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

public class Test {

    // Singleton Tests
    @Test
    void testSingletonReturnsSameInstance() {
        Solution.Singleton s1 = Solution.Singleton.getInstance("data1");
        Solution.Singleton s2 = Solution.Singleton.getInstance("data2");
        assertSame(s1, s2, "Singleton should return the same instance");
    }

    @Test
    void testSingletonData() {
        Solution.Singleton instance = Solution.Singleton.getInstance("test");
        assertEquals("test", instance.getData());
    }

    // Factory Tests
    @Test
    void testFactoryCreatesEmailNotification() {
        Solution.Notification notification = Solution.NotificationFactory.createNotification("email");
        assertNotNull(notification);
        assertTrue(notification instanceof Solution.EmailNotification);
    }

    @Test
    void testFactoryCreatesSMSNotification() {
        Solution.Notification notification = Solution.NotificationFactory.createNotification("sms");
        assertNotNull(notification);
        assertTrue(notification instanceof Solution.SMSNotification);
    }

    @Test
    void testFactoryCreatesPushNotification() {
        Solution.Notification notification = Solution.NotificationFactory.createNotification("push");
        assertNotNull(notification);
        assertTrue(notification instanceof Solution.PushNotification);
    }

    @Test
    void testFactoryThrowsForInvalidType() {
        assertThrows(IllegalArgumentException.class, () -> {
            Solution.NotificationFactory.createNotification("invalid");
        });
    }

    // Builder Tests
    @Test
    void testBuilderCreatesUserWithAllFields() {
        Solution.User user = new Solution.User.UserBuilder()
            .name("John")
            .email("john@test.com")
            .age(30)
            .address("123 Main St")
            .build();

        assertEquals("John", user.getName());
        assertEquals("john@test.com", user.getEmail());
        assertEquals(30, user.getAge());
        assertEquals("123 Main St", user.getAddress());
    }

    @Test
    void testBuilderCreatesUserWithOptionalFields() {
        Solution.User user = new Solution.User.UserBuilder()
            .name("Jane")
            .build();

        assertEquals("Jane", user.getName());
    }

    // Observer Tests
    @Test
    void testObserverNotifiesAllObservers() {
        Solution.Subject subject = new Solution.Subject();
        List<String> received = new ArrayList<>();

        Solution.Observer o1 = e -> received.add("Observer1: " + e);
        Solution.Observer o2 = e -> received.add("Observer2: " + e);

        subject.attach(o1);
        subject.attach(o2);
        subject.notifyObservers("test event");

        assertEquals(2, received.size());
    }

    @Test
    void testObserverDetach() {
        Solution.Subject subject = new Solution.Subject();
        List<String> received = new ArrayList<>();

        Solution.Observer o1 = e -> received.add("Observer1: " + e);
        Solution.Observer o2 = e -> received.add("Observer2: " + e);

        subject.attach(o1);
        subject.attach(o2);
        subject.detach(o1);
        subject.notifyObservers("test");

        assertEquals(1, received.size());
    }

    // Strategy Tests
    @Test
    void testStrategyCreditCardPayment() {
        Solution.PaymentStrategy strategy = new Solution.CreditCardPayment("1234-5678");
        assertNotNull(strategy);
    }

    @Test
    void testStrategyPayPalPayment() {
        Solution.PaymentStrategy strategy = new Solution.PayPalPayment("test@test.com");
        assertNotNull(strategy);
    }

    @Test
    void testShoppingCartCheckout() {
        Solution.ShoppingCart cart = new Solution.ShoppingCart(new Solution.CreditCardPayment("1234"));
        assertNotNull(cart);
    }

    // Adapter Tests
    @Test
    void testAdapterPlaysMp4() {
        Solution.MediaPlayer player = new Solution.MediaAdapter();
        assertNotNull(player);
    }

    // Decorator Tests
    @Test
    void testDecoratorAddsCost() {
        Solution.Coffee coffee = new Solution.MilkDecorator(new Solution.SimpleCoffee());
        assertTrue(coffee.getCost() > 2.0);
    }

    @Test
    void testDecoratorAddsDescription() {
        Solution.Coffee coffee = new Solution.MilkDecorator(new Solution.SimpleCoffee());
        assertTrue(coffee.getDescription().contains("Milk"));
    }

    @Test
    void testDecoratorChaining() {
        Solution.Coffee coffee = new Solution.MilkDecorator(new Solution.SugarDecorator(new Solution.SimpleCoffee()));
        assertTrue(coffee.getDescription().contains("Milk"));
        assertTrue(coffee.getDescription().contains("Sugar"));
    }

    // Facade Tests
    @Test
    void testFacadeCreatesComputer() {
        Solution.ComputerFacade facade = new Solution.ComputerFacade();
        assertNotNull(facade);
    }

    // Command Tests
    @Test
    void testCommandExecuteAndUndo() {
        Solution.Light light = new Solution.Light();
        Solution.Command onCmd = new Solution.LightOnCommand(light);
        Solution.RemoteControl remote = new Solution.RemoteControl();

        remote.executeCommand(onCmd);
        remote.undoLast();
    }

    // State Tests
    @Test
    void testStateTransitions() {
        Solution.Context context = new Solution.Context();
        context.setState(new Solution.StateA());
        context.request();
        context.request();
    }

    // Template Method Tests
    @Test
    void testTemplateMethodPDF() {
        Solution.DataMiner miner = new Solution.PDFDataMiner();
        assertNotNull(miner);
    }

    @Test
    void testTemplateMethodCSV() {
        Solution.DataMiner miner = new Solution.CSVDataMiner();
        assertNotNull(miner);
    }

    // Proxy Tests
    @Test
    void testProxyLazilyLoadsImage() {
        Solution.Image proxy = new Solution.ImageProxy("test.jpg");
        assertNotNull(proxy);
    }

    @Test
    void testProxyDisplaysImage() {
        Solution.Image proxy = new Solution.ImageProxy("test.jpg");
        proxy.display();
    }

    // Chain of Responsibility Tests
    @Test
    void testChainHandlesAuth() {
        Solution.Handler handler = new Solution.AuthHandler();
        assertNotNull(handler);
    }

    @Test
    void testChainOfResponsibility() {
        Solution.Handler chain = new Solution.AuthHandler()
            .setNext(new Solution.LogHandler())
            .setNext(new Solution.ValidateHandler());

        chain.handle("auth:login");
        chain.handle("log:info");
    }

    // Composite Tests
    @Test
    void testCompositeFileSize() {
        Solution.File file = new Solution.File("test.txt", 100);
        assertEquals(100, file.getSize());
    }

    @Test
    void testCompositeDirectorySize() {
        Solution.Directory dir = new Solution.Directory("root");
        dir.add(new Solution.File("a.txt", 100));
        dir.add(new Solution.File("b.txt", 200));
        assertEquals(300, dir.getSize());
    }

    @Test
    void testCompositeNestedDirectories() {
        Solution.Directory root = new Solution.Directory("root");
        Solution.Directory sub = new Solution.Directory("sub");
        sub.add(new Solution.File("c.txt", 50));
        root.add(new Solution.File("a.txt", 100));
        root.add(sub);
        assertEquals(150, root.getSize());
    }

    // Iterator Tests
    @Test
    void testIteratorHasNext() {
        List<String> list = List.of("a", "b", "c");
        Solution.Iterator<String> iter = new Solution.ListIterator<>(list);
        assertTrue(iter.hasNext());
    }

    @Test
    void testIteratorNext() {
        List<String> list = List.of("a", "b", "c");
        Solution.Iterator<String> iter = new Solution.ListIterator<>(list);
        assertEquals("a", iter.next());
        assertEquals("b", iter.next());
        assertEquals("c", iter.next());
    }

    @Test
    void testIteratorExhausted() {
        List<String> list = List.of("a");
        Solution.Iterator<String> iter = new Solution.ListIterator<>(list);
        iter.next();
        assertFalse(iter.hasNext());
    }

    // Memento Tests
    @Test
    void testMementoSavesState() {
        Solution.Originator originator = new Solution.Originator();
        originator.setState("State1");
        Solution.Memento memento = originator.save();
        assertEquals("State1", memento.getState());
    }

    @Test
    void testMementoRestoresState() {
        Solution.Originator originator = new Solution.Originator();
        Solution.Caretaker caretaker = new Solution.Caretaker();

        originator.setState("State1");
        caretaker.addMemento(originator.save());
        originator.setState("State2");
        originator.restore(caretaker.getMemento(0));

        assertEquals("State1", originator.getState());
    }

    // Flyweight Tests
    @Test
    void testFlyweightReturnsSameType() {
        Solution.TreeType type1 = Solution.TreeFactory.getTreeType("Oak", "Green", "texture");
        Solution.TreeType type2 = Solution.TreeFactory.getTreeType("Oak", "Green", "texture");
        assertSame(type1, type2);
    }

    @Test
    void testFlyweightCreatesNewType() {
        Solution.TreeType type1 = Solution.TreeFactory.getTreeType("Oak", "Green", "texture");
        Solution.TreeType type2 = Solution.TreeFactory.getTreeType("Pine", "Green", "texture");
        assertNotSame(type1, type2);
    }

    @Test
    void testFlyweightTreeCreation() {
        Solution.TreeType type = Solution.TreeFactory.getTreeType("Oak", "Green", "texture");
        Solution.Tree tree = new Solution.Tree(1, 2, type);
        assertNotNull(tree);
    }

    @Test
    void testObserverPatternIntegration() {
        Solution.Subject subject = new Solution.Subject();
        StringBuilder sb = new StringBuilder();

        subject.attach(event -> sb.append("1:").append(event).append(";"));
        subject.attach(event -> sb.append("2:").append(event).append(";"));

        subject.notifyObservers("test");

        assertTrue(sb.toString().contains("1:test"));
        assertTrue(sb.toString().contains("2:test"));
    }

    @Test
    void testStrategyPatternIntegration() {
        Solution.ShoppingCart cart = new Solution.ShoppingCart(new Solution.CreditCardPayment("1234"));
        assertNotNull(cart);
    }

    @Test
    void testDecoratorMultipleLayers() {
        Solution.Coffee coffee = new Solution.MilkDecorator(
            new Solution.SugarDecorator(
                new Solution.MilkDecorator(
                    new Solution.SimpleCoffee())));
        assertTrue(coffee.getDescription().contains("Milk"));
        assertTrue(coffee.getDescription().contains("Sugar"));
    }

    @Test
    void testCompositeEmptyDirectory() {
        Solution.Directory dir = new Solution.Directory("empty");
        assertEquals(0, dir.getSize());
    }

    @Test
    void testCommandPatternUndo() {
        Solution.Light light = new Solution.Light();
        Solution.Command offCmd = new Solution.LightOffCommand(light);
        Solution.RemoteControl remote = new Solution.RemoteControl();
        remote.executeCommand(offCmd);
        remote.undoLast();
    }

    @Test
    void testIteratorWithEmptyList() {
        List<String> emptyList = List.of();
        Solution.Iterator<String> iter = new Solution.ListIterator<>(emptyList);
        assertFalse(iter.hasNext());
    }

    @Test
    void testMementoMultipleSaves() {
        Solution.Originator originator = new Solution.Originator();
        Solution.Caretaker caretaker = new Solution.Caretaker();

        originator.setState("State1");
        caretaker.addMemento(originator.save());
        originator.setState("State2");
        caretaker.addMemento(originator.save());
        originator.setState("State3");

        originator.restore(caretaker.getMemento(1));
        assertEquals("State2", originator.getState());
    }

    @Test
    void testStatePatternInitialState() {
        Solution.Context context = new Solution.Context();
        assertDoesNotThrow(() -> context.request());
    }

    @Test
    void testTemplateMethodWithDifferentFormats() {
        Solution.DataMiner pdfMiner = new Solution.PDFDataMiner();
        Solution.DataMiner csvMiner = new Solution.CSVDataMiner();
        assertNotNull(pdfMiner);
        assertNotNull(csvMiner);
    }
}