# TCP/UDP - Internals

## TCP Retransmission Timer (RTO)
```java
public class TcpRtoCalculator {
    private double srtt = 0;
    private double rttvar = 0;
    private static final double ALPHA = 0.125;
    private static final double BETA = 0.25;

    public long calculateRto(double measuredRtt) {
        if (srtt == 0) {
            srtt = measuredRtt;
            rttvar = measuredRtt / 2;
        } else {
            rttvar = (1 - BETA) * rttvar + BETA * Math.abs(srtt - measuredRtt);
            srtt = (1 - ALPHA) * srtt + ALPHA * measuredRtt;
        }
        return (long) (srtt + 4 * rttvar);
    }
}
```

## Congestion Window (CUBIC - simplified)
```java
public class TcpCubic {
    private double cwnd = 10;
    private double ssthresh = Integer.MAX_VALUE;

    public void onAck() {
        if (cwnd < ssthresh) cwnd += 1; // slow start
        else cwnd += 1 / cwnd; // congestion avoidance
    }

    public void onLoss() {
        ssthresh = cwnd / 2;
        cwnd = 1; // reset to initial window
    }
}
```
