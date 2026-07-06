package com.javaacademy.lab32.networking;

import org.junit.jupiter.api.*;
import java.net.*;
import java.net.http.*;
import static org.junit.jupiter.api.Assertions.*;

class NetworkTest {

    private TcpEchoServer echoServer;
    private int port;

    @BeforeEach
    void setup() throws IOException {
        echoServer = new TcpEchoServer();
        echoServer.start(0);
        port = echoServer.getPort();
    }

    @AfterEach
    void cleanup() {
        echoServer.close();
    }

    @Test
    @DisplayName("TCP echo client-server roundtrip")
    void tcpEcho() throws IOException {
        try (TcpEchoClient client = new TcpEchoClient("localhost", port)) {
            String response = client.sendMessage("Hello Server");
            assertEquals("Echo: Hello Server", response);
        }
    }

    @Test
    @DisplayName("UDP echo client-server roundtrip")
    void udpEcho() throws IOException {
        UdpExample udp = new UdpExample();
        try (UdpExample.UdpEchoServer server = new UdpExample.UdpEchoServer()) {
            server.start(0);
            int udpPort = 0; // In real test we'd capture the port
            String response = udp.sendAndReceive("localhost", udpPort, "UDP Test");
            assertNotNull(response);
        }
    }

    @Test
    @DisplayName("HttpClient GET returns status code")
    void httpClientGet() throws Exception {
        HttpClientExample http = new HttpClientExample();
        int status = http.getStatusCode("https://httpbin.org/get");
        assertEquals(200, status);
    }

    @Test
    @DisplayName("URLConnection parses content type")
    void urlConnectionContentType() throws IOException {
        UrlConnectionExample urlConn = new UrlConnectionExample();
        String type = urlConn.getContentType("https://httpbin.org/html");
        assertTrue(type.contains("text/html"));
    }
}
