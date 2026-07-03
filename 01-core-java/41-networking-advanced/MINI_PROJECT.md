# Mini Project: Multicast Ticker Feed

## Objective
Build a lightweight, high-performance UDP Multicast system. You will create a "Stock Exchange" publisher that broadcasts stock price updates, and a "Broker" subscriber that listens to the multicast group to receive real-time updates.

## Prerequisites
*   Java 17+
*   A network environment that supports Multicast (Note: Some cloud VMs or strict VPNs block multicast traffic. This runs best on a local machine).

## Step 1: The Multicast Publisher (Stock Exchange)
This application generates random stock prices and blasts them out to a Multicast Group IP address via UDP.

```java
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;

public class StockPublisher {
    public static final String MULTICAST_GROUP_IP = "230.0.0.1";
    public static final int MULTICAST_PORT = 4446;

    public static void main(String[] args) {
        System.out.println("Starting Stock Exchange Publisher...");
        Random random = new Random();

        try (MulticastSocket socket = new MulticastSocket()) {
            InetAddress group = InetAddress.getByName(MULTICAST_GROUP_IP);
            
            // Set Time-To-Live. 0 = localhost only, 1 = local subnet (default)
            socket.setTimeToLive(1); 

            while (true) {
                // Generate a fake stock update
                String ticker = "AAPL";
                double price = 150.0 + (random.nextDouble() * 10 - 5);
                String message = String.format("%s:%.2f", ticker, price);

                // Convert to bytes
                byte[] buffer = message.getBytes();

                // Create the UDP Datagram Packet targeting the Multicast Group
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, MULTICAST_PORT);

                // Broadcast it!
                socket.send(packet);
                System.out.println("Broadcasted: " + message);

                // Wait 1 second before the next update
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

## Step 2: The Multicast Subscriber (Broker)
This application joins the Multicast Group and listens for incoming packets. You can run multiple instances of this subscriber simultaneously, and they will all receive the exact same packets from the single publisher.

```java
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;

public class BrokerSubscriber {
    
    public static void main(String[] args) {
        System.out.println("Starting Broker Subscriber...");

        // We use NIO DatagramChannel for modern Multicast support
        try (DatagramChannel channel = DatagramChannel.open()) {
            
            // Allow multiple subscribers on the same machine to bind to the same port
            channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            channel.bind(new InetSocketAddress(StockPublisher.MULTICAST_PORT));

            InetAddress group = InetAddress.getByName(StockPublisher.MULTICAST_GROUP_IP);
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());

            // Join the Multicast Group
            System.out.println("Joining Multicast Group: " + StockPublisher.MULTICAST_GROUP_IP);
            MembershipKey key = channel.join(group, networkInterface);

            ByteBuffer buffer = ByteBuffer.allocate(1024);

            while (true) {
                buffer.clear();
                
                // Blocks until a packet arrives
                channel.receive(buffer); 
                
                buffer.flip();
                byte[] bytes = new byte[buffer.limit()];
                buffer.get(bytes);
                
                String message = new String(bytes);
                System.out.println("Broker received update: " + message);
            }

        } catch (IOException e) {
            System.err.println("Multicast failed. Check if your network/OS supports it.");
            e.printStackTrace();
        }
    }
}
```

## Step 3: Run the Simulation
1.  Compile both classes.
2.  Open **Terminal 1** and run the `BrokerSubscriber`.
3.  Open **Terminal 2** and run a *second* instance of `BrokerSubscriber`.
4.  Open **Terminal 3** and run the `StockPublisher`.

## Expected Output
You will see the Publisher broadcasting messages in Terminal 3. 
Simultaneously, you will see BOTH Terminal 1 and Terminal 2 receiving the exact same messages at the exact same time, proving that the network is duplicating the UDP packets.

**(Terminal 3 - Publisher)**
```text
Starting Stock Exchange Publisher...
Broadcasted: AAPL:152.34
Broadcasted: AAPL:148.91
```

**(Terminal 1 - Broker 1)**
```text
Starting Broker Subscriber...
Joining Multicast Group: 230.0.0.1
Broker received update: AAPL:152.34
Broker received update: AAPL:148.91
```

**(Terminal 2 - Broker 2)**
```text
Starting Broker Subscriber...
Joining Multicast Group: 230.0.0.1
Broker received update: AAPL:152.34
Broker received update: AAPL:148.91
```