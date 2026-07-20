package com.javalab.03;

import org.junit.jupiter.api.*;
import java.net.http.*;
import static org.junit.jupiter.api.Assertions.*;

class MainImplementationTest {

    private MainImplementation client;

    @BeforeEach
    void setUp() {
        client = new MainImplementation();
    }

    @Test
    @DisplayName("HttpClient should have correct configuration")
    void testClientConfig() {
        assertNotNull(client.getClient());
        assertTrue(client.getClient().connectTimeout().isPresent());
    }

    @Test
    @DisplayName("Should send async request")
    void testAsyncRequest() throws Exception {
        CompletableFuture<HttpResponse<String>> future = client.sendGetAsync("https://httpbin.org/get");
        HttpResponse<String> response = future.get();
        assertEquals(200, response.statusCode());
    }
}
