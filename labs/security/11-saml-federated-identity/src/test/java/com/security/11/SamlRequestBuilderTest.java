package com.security11;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SamlRequestBuilderTest {
    @Test
    void testBuildAuthnRequest() {
        SamlRequestBuilder builder = new SamlRequestBuilder();
        String request = builder.buildAuthnRequest(
            "https://sp.example.com/metadata",
            "https://sp.example.com/acs",
            "https://idp.example.com/sso");
        assertNotNull(request);
        assertTrue(request.contains("AuthnRequest"));
        assertTrue(request.contains("https://sp.example.com/acs"));
    }
}
