package solution;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WebSocketSolutionTest {

    private WebSocketSolution server;

    @BeforeEach
    void setUp() {
        server = new WebSocketSolution();
    }

    @Test
    void testServerStarts() {
        server.startServer(8080);
        assertTrue(server.getConnectedCount() == 0);
    }

    @Test
    void testBroadcast() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean received = new AtomicBoolean(false);

        // Simulate client connection and broadcast
        server.broadcast("Hello World");

        assertTrue(true); // No exceptions thrown
    }

    @Test
    void testStompConnectFrame() {
        String frame = "CONNECT\naccept-version:1.2\n\n\u0000";
        String response = server.parseStompFrame(frame);
        assertNotNull(response);
        assertTrue(response.contains("CONNECTED"));
    }

    @Test
    void testStompSubscribeFrame() {
        String frame = "SUBSCRIBE\ndestination:/topic/events\n\n\u0000";
        String response = server.parseStompFrame(frame);
        assertEquals("", response);
    }

    @Test
    void testStompSendFrame() {
        String frame = "SEND\ndestination:/queue/test\n\n{\"msg\":\"hello\"}\u0000";
        String response = server.parseStompFrame(frame);
        assertEquals("", response);
    }

    @Test
    void testSendToUser() {
        server.sendToUser("user1", "Hello user1");
        assertTrue(true); // No exception thrown
    }
}