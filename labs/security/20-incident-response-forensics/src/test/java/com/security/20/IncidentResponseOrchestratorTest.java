package com.security20;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IncidentResponseOrchestratorTest {
    @Test
    void testCreateIncident() {
        IncidentResponseOrchestrator ir = new IncidentResponseOrchestrator();
        var incident = ir.createIncident("RANSOMWARE", "server-01", "Files encrypted");
        assertNotNull(incident);
        assertEquals("CRITICAL", incident.severity);
        assertEquals(IncidentResponseOrchestrator.IncidentStatus.DETECTED, incident.status);
    }

    @Test
    void testSeverityClassification() {
        IncidentResponseOrchestrator ir = new IncidentResponseOrchestrator();
        assertEquals("CRITICAL", ir.classifySeverity("data_breach"));
        assertEquals("HIGH", ir.classifySeverity("malware"));
        assertEquals("MEDIUM", ir.classifySeverity("policy_violation"));
        assertEquals("LOW", ir.classifySeverity("low_priority_event"));
    }

    @Test
    void testCollectEvidence() throws Exception {
        IncidentResponseOrchestrator ir = new IncidentResponseOrchestrator();
        var evidence = ir.collectEvidence("server-01", IncidentResponseOrchestrator.EvidenceType.MEMORY_DUMP);
        assertNotNull(evidence.id);
        assertEquals(IncidentResponseOrchestrator.ChainOfCustodyStatus.COLLECTED, evidence.status);
    }
}
