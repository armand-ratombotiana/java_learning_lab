package com.security17;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SmartContractAuditorTest {
    @Test
    void testDetectsReentrancy() {
        SmartContractAuditor auditor = new SmartContractAuditor();
        String code = """
            function withdraw(uint amount) public {
                (bool success, ) = msg.sender.call{value: amount}("");
                require(success);
                balances[msg.sender] -= amount;
            }
        """;
        var report = auditor.analyzeContract(code);
        assertFalse(report.vulnerabilities().isEmpty());
    }

    @Test
    void testCleanContractHasNoVulnerabilities() {
        SmartContractAuditor auditor = new SmartContractAuditor();
        String code = "function add(uint a, uint b) public pure returns (uint) { return a + b; }";
        var report = auditor.analyzeContract(code);
        assertTrue(report.vulnerabilities().isEmpty());
    }
}
