# TCP/UDP - How It Works

## TCP Three-Way Handshake
```
Client                    Server
   |                         |
   |--- SYN (seq=x) -------->|
   |<-- SYN-ACK (seq=y,ack=x+1) --|
   |--- ACK (seq=x+1,ack=y+1)->|
   |                         |
   |<==== Connection Established ==>|
```

## Connection Termination
```
Client                    Server
   |                         |
   |--- FIN (seq=u) -------->|
   |<-- ACK (ack=u+1) -------|
   |<-- FIN (seq=v) ---------|
   |--- ACK (ack=v+1) ------>|
```

## Java Latency Comparison
```java
public class LatencyComparison {
    public static double tcpLatency(String host, int port) throws Exception {
        long start = System.nanoTime();
        try (Socket s = new Socket(host, port)) { }
        return (System.nanoTime() - start) / 1_000_000.0;
    }

    public static double udpLatency(String host, int port) throws Exception {
        long start = System.nanoTime();
        DatagramSocket s = new DatagramSocket();
        byte[] data = "ping".getBytes();
        DatagramPacket p = new DatagramPacket(data, data.length,
            InetAddress.getByName(host), port);
        s.send(p);
        return (System.nanoTime() - start) / 1_000_000.0;
    }
}
```
