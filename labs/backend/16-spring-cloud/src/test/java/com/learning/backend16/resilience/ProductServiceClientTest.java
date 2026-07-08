package com.learning.backend16.resilience;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ProductServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ProductServiceClient productServiceClient;

    @Test
    void fallbackReturnsDefaultInventory() {
        var result = productServiceClient.getDefaultInventory("P001", new RuntimeException("Service down"));
        assertNotNull(result);
        assertTrue(result.containsKey("status"));
        assertTrue(result.containsKey("source"));
    }
}
