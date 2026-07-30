package com.security.deep.lab08;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class ZeroTrust {

    public static class AccessPolicy {
        private final List<PolicyRule> rules = new ArrayList<>();

        public record PolicyRule(String name, String resource, String action,
                                  Predicate<AccessContext> condition, String decision) {}

        public record AccessContext(String userId, String resource, String action,
                                     String deviceId, String location, double riskScore,
                                     boolean mfaVerified, boolean deviceCompliant) {}

        public void addRule(PolicyRule rule) { rules.add(rule); }

        public String evaluate(AccessContext context) {
            for (PolicyRule rule : rules) {
                if (context.resource().equals(rule.resource()) && context.action().equals(rule.action())) {
                    if (rule.condition().test(context)) return rule.decision();
                }
            }
            return "deny";
        }
    }

    public static class RiskEngine {
        private double calculateBaseRisk(ZeroTrust.AccessPolicy.AccessContext context) {
            double risk = 0.0;
            if (context.location() != null && !context.location().equals("office")) risk += 0.2;
            if (!context.deviceCompliant()) risk += 0.3;
            if (!context.mfaVerified()) risk += 0.4;
            if (context.riskScore() > 0) risk += context.riskScore();
            return Math.min(1.0, risk);
        }

        public String determineAction(double risk) {
            if (risk < 0.3) return "allow";
            if (risk < 0.7) return "step_up_mfa";
            return "block";
        }
    }

    public static class JustInTimeAccess {
        private final Map<String, JitGrant> activeGrants = new ConcurrentHashMap<>();

        public record JitGrant(String userId, String resource, long expiry, String justification) {}

        public String requestElevation(String userId, String resource, String justification,
                                        int durationMinutes) {
            if (!canRequestElevation(userId, resource)) return null;
            String token = UUID.randomUUID().toString();
            long expiry = Instant.now().getEpochSecond() + durationMinutes * 60L;
            activeGrants.put(token, new JitGrant(userId, resource, expiry, justification));
            return token;
        }

        public boolean validateElevation(String token, String resource) {
            JitGrant grant = activeGrants.get(token);
            if (grant == null) return false;
            if (!grant.resource().equals(resource)) return false;
            if (Instant.now().getEpochSecond() > grant.expiry()) {
                activeGrants.remove(token);
                return false;
            }
            return true;
        }

        private boolean canRequestElevation(String userId, String resource) {
            return userId != null && resource != null;
        }
    }

    public static class SessionManager {
        private final Map<String, Session> sessions = new ConcurrentHashMap<>();

        public record Session(String sessionId, String userId, String deviceId,
                               Instant createdAt, Instant lastVerified, boolean active) {}

        public Session createSession(String userId, String deviceId) {
            String sessionId = UUID.randomUUID().toString();
            Session session = new Session(sessionId, userId, deviceId,
                Instant.now(), Instant.now(), true);
            sessions.put(sessionId, session);
            return session;
        }

        public boolean verifySession(String sessionId) {
            Session session = sessions.get(sessionId);
            if (session == null) return false;
            if (!session.active()) return false;
            long elapsed = Instant.now().getEpochSecond() - session.lastVerified().getEpochSecond();
            if (elapsed > 300) return false; // re-verify every 5 minutes
            return true;
        }

        public void terminateSession(String sessionId) {
            sessions.computeIfPresent(sessionId, (k, v) ->
                new Session(k, v.userId(), v.deviceId(), v.createdAt(), v.lastVerified(), false));
        }
    }

    public static Map<String, Object> buildZeroTrustReport(int totalRequests, int allowed, int denied, int stepUpMfa) {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("totalRequests", totalRequests);
        report.put("allowed", allowed);
        report.put("denied", denied);
        report.put("stepUpMfa", stepUpMfa);
        report.put("allowRate", totalRequests > 0 ? (double) allowed / totalRequests : 0);
        report.put("denyRate", totalRequests > 0 ? (double) denied / totalRequests : 0);
        return report;
    }
}
