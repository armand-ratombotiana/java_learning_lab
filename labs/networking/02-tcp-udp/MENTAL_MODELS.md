# TCP/UDP - Mental Models

## 1. Postal Service Model (TCP)
TCP is like registered mail with return receipt: open a mailbox (socket), write to a specific address (IP+port), get delivery confirmation (ACK), letters arrive in order (sequencing), lost letters are resent (retransmission).

## 2. Walkie-Talkie Model (UDP)
UDP is like a walkie-talkie: just press and talk (send datagram), no confirmation if heard (no ACK), can broadcast to everyone (multicast).

## 3. TCP State Machine
```
CLOSED -> SYN_SENT -> ESTABLISHED -> FIN_WAIT_1 -> FIN_WAIT_2 -> TIME_WAIT -> CLOSED
                                              -> CLOSE_WAIT -> LAST_ACK -> CLOSED
```

## 4. Water Hose Model (Flow Control)
Receiver is a bucket with a hole. Sender adjusts flow rate. Advertised window = bucket capacity. Sender can't pour faster than bucket drains.

```java
public class SlidingWindow {
    private int windowSize;
    private int lastAck;
    private int nextSeq;

    public boolean canSend() {
        return (nextSeq - lastAck) < windowSize;
    }

    public void receiveAck(int seq) {
        lastAck = seq;
    }
}
```
