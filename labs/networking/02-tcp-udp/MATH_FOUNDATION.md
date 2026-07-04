# TCP/UDP - Mathematical Foundation

## TCP Throughput
- **Simple**: Throughput = WindowSize / RTT
- **Mathis Equation**: Throughput = MSS * sqrt(3/2) / (RTT * sqrt(loss_rate))
- **Padhye Model**: Complex formula including timeout effects

## UDP Bandwidth
Bandwidth = PacketSize * PacketsPerSecond * 8 (bps)

## Expected Retransmissions
E[N] = 1/p where p = packet loss rate

```java
public class BandwidthCalculator {
    public static double tcpThroughput(int mss, double rttMs, double lossRate) {
        double rtt = rttMs / 1000.0;
        return (mss * 8 * Math.sqrt(1.5)) / (rtt * Math.sqrt(lossRate));
    }

    public static double udpBandwidth(int packetSize, int pps) {
        return packetSize * pps * 8.0;
    }

    public static void main(String[] args) {
        System.out.printf("TCP throughput: %.2f Mbps%n",
            tcpThroughput(1460, 50, 0.001) / 1_000_000);
        System.out.printf("UDP bandwidth: %.2f Mbps%n",
            udpBandwidth(1472, 10000) / 1_000_000);
    }
}
```
