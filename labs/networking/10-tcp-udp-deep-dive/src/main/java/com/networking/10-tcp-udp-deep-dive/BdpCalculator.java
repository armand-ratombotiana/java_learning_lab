package com.networking.tcp-udp-deep-dive;

import java.util.Locale;

public record BdpCalculator(double bandwidthMbps, double rttMs) {

    public double bdpBytes() {
        double bandwidthBps = bandwidthMbps * 1_000_000;
        double rttSec = rttMs / 1000.0;
        return (bandwidthBps / 8) * rttSec;
    }

    public double bdpKb() { return bdpBytes() / 1024; }

    public double bdpMb() { return bdpBytes() / (1024 * 1024); }

    public int optimalBufferBytes() {
        return (int) Math.ceil(bdpBytes() * 2);
    }

    public double maxThroughputMbps(int windowBytes) {
        double rttSec = rttMs / 1000.0;
        return (windowBytes * 8.0) / rttSec / 1_000_000;
    }

    public int recommendedMss() {
        return 1460;
    }

    public String report() {
        return String.format(Locale.US,
            """
            BDP Analysis:
              Bandwidth: %.0f Mbps
              RTT: %.0f ms
              BDP: %.0f bytes (%.1f KB / %.3f MB)
              Optimal socket buffer: %d bytes (%.1f KB)
              Max throughput with optimal buffer: %.1f Mbps
              Max throughput with 64KB window: %.1f Mbps
            """,
            bandwidthMbps, rttMs, bdpBytes(), bdpKb(), bdpMb(),
            optimalBufferBytes(), optimalBufferBytes() / 1024.0,
            maxThroughputMbps(optimalBufferBytes()),
            maxThroughputMbps(65536));
    }

    public static void main(String[] args) {
        System.out.println("=== BDP Calculator ===");
        var calc = new BdpCalculator(1000, 20);
        System.out.println(calc.report());
    }
}
