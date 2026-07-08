# TCP/UDP Deep Dive — Common Mistakes

## 1. TCP Socket Programming Mistakes

### Mistake 1: Not setting SO_TIMEOUT
Default socket timeout is 0 (infinite), causing threads to block forever.

**❌ Wrong:**
```java
Socket socket = new Socket("host", 8080);
InputStream in = socket.getInputStream();
in.read(); // Blocks forever if no data arrives
```

**✅ Correct:**
```java
Socket socket = new Socket();
socket.connect(new InetSocketAddress("host", 8080), 5000);
socket.setSoTimeout(30000); // Read timeout
InputStream in = socket.getInputStream();
in.read(); // Throws SocketTimeoutException after 30s
```

### Mistake 2: Assuming write() sends immediately
TCP is a stream protocol — multiple writes may be coalesced.

**❌ Wrong:**
```java
out.write(message1.getBytes());
out.write(message2.getBytes());
// Receiver sees message1+message2 concatenated
```

**✅ Correct (message framing):**
```java
// Use message length prefix
byte[] data = message.getBytes();
out.writeInt(data.length); // Length prefix
out.write(data);           // Message body
out.flush();
```

### Mistake 3: Ignoring partial reads/writes
TCP streams may deliver data in chunks.

**❌ Wrong:**
```java
byte[] buffer = new byte[1024];
int bytesRead = in.read(buffer);
// Assumes buffer is full or message complete
```

**✅ Correct:**
```java
public static byte[] readFully(InputStream in, int length) throws IOException {
    byte[] buffer = new byte[length];
    int offset = 0;
    while (offset < length) {
        int bytesRead = in.read(buffer, offset, length - offset);
        if (bytesRead == -1) throw new EOFException("Unexpected EOF");
        offset += bytesRead;
    }
    return buffer;
}
```

### Mistake 4: Not handling InterruptedIOException
Network I/O may be interrupted, leaving the socket in an inconsistent state.

✅ **Correct:**
```java
try {
    socket.getInputStream().read(buffer);
} catch (InterruptedIOException e) {
    Thread.currentThread().interrupt();
    socket.close(); // Socket may be in unknown state
    return;
}
```

### Mistake 5: Forgetting to close resources
Failing to close sockets leads to file descriptor leaks.

**❌ Wrong:**
```java
Socket s = new Socket("host", 8080);
// Use socket...
// Socket never closed
```

**✅ Correct:**
```java
try (Socket s = new Socket("host", 8080);
     var out = s.getOutputStream();
     var in = s.getInputStream()) {
    // Use socket...
} // Auto-closed
```

## 2. UDP Socket Programming Mistakes

### Mistake 6: Assuming UDP datagrams arrive in order
UDP does not guarantee ordering — packets may arrive out of sequence.

**❌ Wrong:**
```java
// Assumes data[0] is first message, data[1] second, etc.
```

**✅ Correct:**
```java
// Include sequence numbers
public class SequencedPacket {
    final int seqNum;
    final byte[] data;

    public static SequencedPacket deserialize(byte[] raw) {
        ByteBuffer buf = ByteBuffer.wrap(raw);
        return new SequencedPacket(buf.getInt(), new byte[buf.remaining()]);
    }
}

// Reorder on receiving end
public class ReorderBuffer {
    private final NavigableMap<Integer, byte[]> buffer = new TreeMap<>();
    private int nextExpected = 0;

    public List<byte[]> addPacket(int seqNum, byte[] data) {
        buffer.put(seqNum, data);
        List<byte[]> ready = new ArrayList<>();
        while (buffer.containsKey(nextExpected)) {
            ready.add(buffer.remove(nextExpected));
            nextExpected++;
        }
        return ready;
    }
}
```

### Mistake 7: Sending packets larger than MTU
UDP packets > MTU (typically 1500 bytes) cause IP fragmentation, increasing loss probability.

**❌ Wrong:**
```java
byte[] largeData = new byte[65507]; // Max UDP payload
DatagramPacket p = new DatagramPacket(largeData, largeData.length, address, port);
```

**✅ Correct:**
```java
int mtu = 1500;
int udpHeader = 8;
int ipHeader = 20;
int maxPayload = mtu - udpHeader - ipHeader; // 1472 bytes

byte[] data = ... ; // Application data
List<byte[]> chunks = new ArrayList<>();
for (int i = 0; i < data.length; i += maxPayload) {
    int len = Math.min(maxPayload, data.length - i);
    chunks.add(Arrays.copyOfRange(data, i, i + len));
}
```

### Mistake 8: Not verifying UDP checksum
UDP checksum is optional in IPv4 but should always be verified.

✅ **Correct:**
```java
// Java does verify UDP checksum on receive
// But application should also add integrity checks
public class UdpPacket {
    final int seqNum;
    final byte[] data;
    final int checksum; // Application-level CRC

    public int computeChecksum() {
        return Arrays.hashCode(data) ^ seqNum;
    }

    public boolean isValid() {
        return checksum == computeChecksum();
    }
}
```

## 3. Nagle's Algorithm Mistakes

### Mistake 9: Enabling Nagle for interactive applications
Nagle intelligently batches data, but for real-time apps, this causes delays.

**❌ Wrong:**
```java
// Default: Nagle enabled
Socket s = new Socket("host", 8080);
// Interactive app suffers 500ms delays
```

**✅ Correct:**
```java
Socket s = new Socket("host", 8080);
s.setTcpNoDelay(true); // Disable Nagle for interactive apps
```

### Mistake 10: Disabling Nagle for bulk transfers
For large data transfers, Nagle reduces packet count and improves throughput.

**❌ Wrong:**
```java
// Bulk download with Nagle disabled
s.setTcpNoDelay(true);
// Sends many small TCP segments = more overhead
```

**✅ Correct:**
```java
// Bulk transfer: let Nagle coalesce
s.setTcpNoDelay(false);
```

## 4. Congestion Control Mistakes

### Mistake 11: Ignoring BDP in high-latency links
Default 64KB buffer limits throughput on high-BDP links.

**Example:** 100ms RTT, 1Gbps link
- BDP = 1Gbps × 0.1s = 100Mb = 12.5MB
- Default buffer: 64KB (~0.5% of BDP)
- Utilization: < 1%

**Fix:** Set SO_RCVBUF/SO_SNDBUF to at least 2× BDP.

### Mistake 12: Assuming loss rate determines throughput only
Throughput also depends on RTT and MSS.

```java
// Correct formula
double throughput = (mss * Math.sqrt(1.5)) / (rtt * Math.sqrt(lossRate));
// Both RTT and loss rate matter equally
```

## 5. Threading Mistakes

### Mistake 13: Blocking I/O in UI/event threads
Network I/O should never block the main thread.

**❌ Wrong:**
```java
public class MyApp extends Application {
    public void start() {
        // Blocking on UI thread!
        Socket s = new Socket("host", 8080);
    }
}
```

**✅ Correct:**
```java
CompletableFuture.runAsync(() -> {
    try (Socket s = new Socket("host", 8080)) {
        // Non-blocking on UI thread
    } catch (IOException e) {
        Platform.runLater(() -> showError(e));
    }
});
```

## 6. Socket Option Mistakes

### Mistake 14: Setting SO_REUSEADDR on clients
SO_REUSEADDR is primarily useful for servers that need to restart quickly.

**✅ Correct:**
```java
// Server socket reuse
ServerSocket ss = new ServerSocket();
ss.setReuseAddress(true);
ss.bind(new InetSocketAddress(port));

// Client: no need for reuse
Socket s = new Socket("host", 8080);
```

### Mistake 15: Confusing SO_LINGER with SO_TIMEOUT
- SO_LINGER: controls behavior on close with pending data
- SO_TIMEOUT: controls max wait for read() to return

## Summary
These are the most common mistakes in TCP/UDP programming. Following the patterns above will prevent the majority of transport-layer bugs in production systems.
