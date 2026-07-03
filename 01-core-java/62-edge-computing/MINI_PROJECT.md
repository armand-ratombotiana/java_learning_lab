# Module 62: Edge Computing & IoT - Mini Project

**Project Name**: Edge Data Aggregator (Store and Forward)  
**Difficulty Level**: Advanced  
**Estimated Time**: 3-4 hours

---

## 🎯 Objective
Build a Java-based Edge Gateway that subscribes to high-frequency local MQTT sensor data, aggregates it to save bandwidth, and resiliently forwards it to a mock "Cloud" server, gracefully handling network disconnections.

## 📝 Requirements

### Core Features

1. **The Infrastructure**:
   - Use Testcontainers (or Docker Compose) to spin up a local Eclipse Mosquitto (MQTT Broker) instance.

2. **The Sensor (Producer)**:
   - Create a Java thread that acts as a local temperature sensor.
   - Using the Eclipse Paho MQTT client, publish a random temperature value (between 20.0 and 30.0) to the topic `factory/sensor/temp` every 100 milliseconds.

3. **The Edge Gateway (Consumer & Aggregator)**:
   - Create an `EdgeGateway` class using Eclipse Paho.
   - Subscribe to `factory/sensor/temp`.
   - Maintain a local buffer (e.g., a `List<Double>`) of incoming temperatures.
   - Implement a scheduled task (e.g., using `ScheduledExecutorService`) that runs every 5 seconds. It should calculate the average of the temperatures in the buffer, clear the buffer, and pass the average to the `CloudForwarder`.

4. **The Store & Forward Mechanism (Resilience)**:
   - Create a `CloudForwarder` class.
   - It maintains a `Queue<Double>` representing data waiting to be synced to the cloud.
   - Write a `syncToCloud()` method. Introduce a mock network failure: use a random boolean to decide if the "cloud" is reachable.
   - If reachable, dequeue all items, print "Successfully synced average: [value] to AWS", and continue.
   - If unreachable, print "NETWORK DOWN. Buffering data at the Edge." Leave the items in the queue.
   - Ensure the next successful sync processes all backlogged data.

---

## 💡 Solution Blueprint

1. **MQTT Dependency**:
   ```xml
   <dependency>
       <groupId>org.eclipse.paho</groupId>
       <artifactId>org.eclipse.paho.client.mqttv3</artifactId>
       <version>1.2.5</version>
   </dependency>
   ```

2. **The Aggregator Logic**:
   ```java
   public class EdgeGateway {
       private final List<Double> buffer = Collections.synchronizedList(new ArrayList<>());
       private final CloudForwarder forwarder = new CloudForwarder();

       public void start() throws Exception {
           MqttClient client = new MqttClient("tcp://localhost:1883", "EdgeGateway");
           client.connect();
           client.subscribe("factory/sensor/temp", (topic, msg) -> {
               buffer.add(Double.parseDouble(new String(msg.getPayload())));
           });

           Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
               if (!buffer.isEmpty()) {
                   double avg = buffer.stream().mapToDouble(d -> d).average().orElse(0);
                   buffer.clear();
                   forwarder.queueForSync(avg);
               }
           }, 5, 5, TimeUnit.SECONDS);
       }
   }
   ```

3. **The Store & Forward Logic**:
   ```java
   public class CloudForwarder {
       private final Queue<Double> offlineBuffer = new ConcurrentLinkedQueue<>();
       private final Random random = new Random();

       public void queueForSync(double avgTemp) {
           offlineBuffer.offer(avgTemp);
           sync();
       }

       private void sync() {
           boolean isNetworkUp = random.nextBoolean(); // 50% chance of failure
           
           if (isNetworkUp) {
               while (!offlineBuffer.isEmpty()) {
                   double val = offlineBuffer.poll();
                   System.out.println("☁️ AWS SYNC SUCCESS: Average Temp " + val);
               }
           } else {
               System.out.println("❌ NETWORK DOWN. Buffering at the Edge. Queue size: " + offlineBuffer.size());
           }
       }
   }
   ```