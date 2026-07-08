package com.security16;

import java.time.Instant;
import java.util.*;

public class AccessPolicyEngine {

    public enum Decision { PERMIT, DENY, STEP_UP }

    public AccessDecision evaluate(AccessRequest request) {
        TrustSignals signals = collectSignals(request);
        double trustScore = calculateTrustScore(signals);
        Decision decision = applyPolicy(request, trustScore);
        return new AccessDecision(decision, trustScore, signals);
    }

    private TrustSignals collectSignals(AccessRequest request) {
        TrustSignals s = new TrustSignals();
        s.identityStrength = request.hasMfa ? 90.0 : 50.0;
        s.devicePosture = request.deviceTrusted ? 85.0 : 30.0;
        s.locationRisk = "internal".equals(request.networkZone) ? 90.0 : 40.0;
        s.behaviorScore = 75.0;
        s.timeAppropriate = isBusinessHours() ? 90.0 : 50.0;
        return s;
    }

    private double calculateTrustScore(TrustSignals signals) {
        return signals.identityStrength * 0.35
             + signals.devicePosture * 0.25
             + signals.behaviorScore * 0.20
             + signals.locationRisk * 0.10
             + signals.timeAppropriate * 0.10;
    }

    private Decision applyPolicy(AccessRequest request, double trustScore) {
        if (trustScore < 50.0) return Decision.DENY;
        if (trustScore < 75.0 && request.sensitiveResource) return Decision.STEP_UP;
        return Decision.PERMIT;
    }

    private boolean isBusinessHours() {
        int hour = java.time.LocalTime.now().getHour();
        return hour >= 8 && hour <= 18;
    }

    public record AccessRequest(
        String userId, String resource, String action, String networkZone,
        boolean hasMfa, boolean deviceTrusted, boolean sensitiveResource
    ) {}

    public record AccessDecision(Decision decision, double trustScore, TrustSignals signals) {}
    public static class TrustSignals {
        public double identityStrength, devicePosture, behaviorScore, locationRisk, timeAppropriate;
    }

    public static void main(String[] args) {
        AccessPolicyEngine engine = new AccessPolicyEngine();
        var request = new AccessRequest("alice", "/api/payments", "POST", "vpn", true, true, true);
        var decision = engine.evaluate(request);
        System.out.println("Decision: " + decision.decision() + " (trust: " + String.format("%.1f", decision.trustScore()) + ")");
    }
}
