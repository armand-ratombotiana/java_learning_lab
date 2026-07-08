package com.networking.tcp-udp-deep-dive;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class BdpCalculatorTest {

    @Test
    void bdpFor1Gbps20ms() {
        var calc = new BdpCalculator(1000, 20);
        double expectedBytes = (1_000_000_000.0 / 8) * 0.02;
        assertEquals(expectedBytes, calc.bdpBytes(), expectedBytes * 0.01);
    }

    @Test
    void bdpFor100Mbps10ms() {
        var calc = new BdpCalculator(100, 10);
        double expectedBytes = (100_000_000.0 / 8) * 0.01;
        assertEquals(expectedBytes, calc.bdpBytes(), expectedBytes * 0.01);
    }

    @Test
    void bdpFor10Mbps100ms() {
        var calc = new BdpCalculator(10, 100);
        double expectedBytes = (10_000_000.0 / 8) * 0.1;
        assertEquals(expectedBytes, calc.bdpBytes(), expectedBytes * 0.01);
    }

    @Test
    void optimalBufferIsTwiceBdp() {
        var calc = new BdpCalculator(100, 20);
        assertEquals((int) Math.ceil(calc.bdpBytes() * 2), calc.optimalBufferBytes());
    }

    @Test
    void maxThroughputMatchesFormula() {
        var calc = new BdpCalculator(100, 20);
        double expected = (calc.optimalBufferBytes() * 8.0) / 0.02 / 1_000_000;
        assertEquals(expected, calc.maxThroughputMbps(calc.optimalBufferBytes()), 0.1);
    }

    @Test
    void throughputWith64kWindow() {
        var calc = new BdpCalculator(100, 20);
        double expected = (65536 * 8.0) / 0.02 / 1_000_000;
        assertEquals(expected, calc.maxThroughputMbps(65536), 0.1);
    }

    @Test
    void zeroBandwidthReturnsZero() {
        var calc = new BdpCalculator(0, 100);
        assertEquals(0, calc.bdpBytes(), 0.001);
    }

    @Test
    void zeroRttReturnsZero() {
        var calc = new BdpCalculator(1000, 0);
        assertEquals(0, calc.bdpBytes(), 0.001);
    }

    @Test
    void reportContainsKeyInfo() {
        var calc = new BdpCalculator(100, 20);
        String report = calc.report();
        assertTrue(report.contains("BDP"));
        assertTrue(report.contains("Optimal socket buffer"));
    }

    @Test
    void bdpUnits() {
        var calc = new BdpCalculator(1000, 20);
        double bytes = calc.bdpBytes();
        assertEquals(bytes / 1024, calc.bdpKb(), 0.001);
        assertEquals(bytes / (1024 * 1024), calc.bdpMb(), 0.001);
    }
}
