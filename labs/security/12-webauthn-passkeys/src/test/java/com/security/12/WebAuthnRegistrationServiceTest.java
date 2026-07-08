package com.security12;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class WebAuthnRegistrationServiceTest {
    private WebAuthnRegistrationService service;

    @BeforeEach
    void setUp() {
        service = new WebAuthnRegistrationService();
    }

    @Test
    void testStartRegistrationReturnsNonNullOptions() {
        var opts = service.startRegistration("user-1", "alice");
        assertNotNull(opts);
        assertNotNull(opts.challenge);
    }

    @Test
    void testCompleteRegistrationWithNullCredential() {
        var req = new WebAuthnRegistrationService.RegistrationRequest();
        assertFalse(service.completeRegistration(req));
    }

    @Test
    void testCompleteRegistrationSuccess() {
        var req = new WebAuthnRegistrationService.RegistrationRequest();
        req.credentialId = "dGVzdC1jcmVkZW50aWFsLWlk";
        req.publicKey = "dGVzdC1wdWJsaWMta2V5";
        req.userId = "user-1";
        assertTrue(service.completeRegistration(req));
    }
}
