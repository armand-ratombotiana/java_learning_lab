package com.cloud.awssec;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class IAMPolicyEngineTest {
    private IAMPolicyEngine.PolicyEvaluator evaluator;

    @BeforeEach void setUp() { evaluator = new IAMPolicyEngine.PolicyEvaluator(); }

    @Test void testAllowAction() {
        var policy = new IAMPolicyEngine.Policy("Test", List.of(
            new IAMPolicyEngine.PolicyStatement("ALLOW", List.of("s3:GetObject"), List.of("arn:aws:s3:::bucket/*"), Map.of())));
        assertTrue(evaluator.evaluate(policy, "s3:GetObject", "arn:aws:s3:::bucket/key.txt", Map.of()));
    }

    @Test void testDenyOverridesAllow() {
        var policy = new IAMPolicyEngine.Policy("Test", List.of(
            new IAMPolicyEngine.PolicyStatement("ALLOW", List.of("s3:*"), List.of("arn:aws:s3:::bucket/*"), Map.of()),
            new IAMPolicyEngine.PolicyStatement("DENY", List.of("s3:DeleteObject"), List.of("arn:aws:s3:::bucket/secret/*"), Map.of())));
        assertFalse(evaluator.evaluate(policy, "s3:DeleteObject", "arn:aws:s3:::bucket/secret/data.txt", Map.of()));
    }

    @Test void testWildcardAction() {
        var policy = new IAMPolicyEngine.Policy("Test", List.of(
            new IAMPolicyEngine.PolicyStatement("ALLOW", List.of("s3:*"), List.of("arn:aws:s3:::bucket/*"), Map.of())));
        assertTrue(evaluator.evaluate(policy, "s3:PutObject", "arn:aws:s3:::bucket/data.txt", Map.of()));
    }

    @Test void testKmsEncryptDecrypt() {
        var kms = new IAMPolicyEngine.KmsSimulator();
        var key = kms.createKey("SYMMETRIC_DEFAULT");
        byte[] original = "Hello KMS!".getBytes();
        byte[] encrypted = kms.encrypt(key.keyId(), original);
        byte[] decrypted = kms.decrypt(key.keyId(), encrypted);
        assertArrayEquals(original, decrypted);
    }

    @Test void testWafEngine() {
        var waf = new IAMPolicyEngine.WafEngine();
        waf.addRule(new IAMPolicyEngine.WafEngine.WafRule("BlockXSS", "XSS", IAMPolicyEngine.WafEngine.Action.BLOCK));
        assertEquals(IAMPolicyEngine.WafEngine.Action.BLOCK, waf.evaluate("1.2.3.4", "/", "<script>alert(1)</script>"));
        assertEquals(IAMPolicyEngine.WafEngine.Action.ALLOW, waf.evaluate("1.2.3.4", "/", "normal request"));
    }
}
