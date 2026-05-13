# Debugging RabbitMQ Message Delivery Issues

## Common Failure Scenarios

### Message Undelivered / Returned to Sender

Messages return as undelivered when no queue accepts them. The message bounces back to the producer with a "no route" error. This happens when the routing key doesn't match any binding, or the bound queue is deleted.

The producer receives a `AmqpException` or sees the message in the management UI as "released" or "returned." The message appears in the dead letter exchange if configured, or disappears if no DLX exists.

Verify routing key matches bindings exactly. RabbitMQ routing is exact string matching, not pattern matching by default. Check that the exchange type matches your routing requirements—direct for exact match, topic for pattern match, fanout for broadcast.

### Consumer Acknowledgment Failures

Messages requeue continuously when consumers fail to acknowledge. The same message appears in the queue repeatedly, causing processing loops. This happens when the consumer throws an exception, the connection drops before acknowledgment, or acknowledgment timeout occurs.

Check the consumer code for exception handling. Uncaught exceptions cause negative acknowledgment automatically. Implement proper error handling and decide whether to requeue or send to DLQ for failed messages.

Network issues cause unacked messages to be requeued. If the connection drops mid-processing, RabbitMQ considers the message unacked and requeues it. Ensure consumers implement idempotency to handle redelivery safely.

### Queue Backpressure and Memory Issues

Queues grow unbounded when consumers cannot keep up. Memory usage increases, disk space fills with persistence, and message delivery latency spikes. The queue depth metric shows messages accumulating.

The cause is consumer throughput not matching producer rate. This can be sudden (load spike) or gradual (consumer scaling mismatch). Scale consumer instances or optimize consumer processing time.

## Stack Trace Examples

**Queue not found:**
```
AmqpResourceNotFoundException: Queue not found: queue-name
    at com.rabbitmq.client.impl.ChannelManager.getQueue(ChannelManager.java:456)
```

**Connection refused:**
```
java.net.ConnectException: Connection refused: /localhost:5672
    at java.net.PlainSocketImpl.socketConnect(Native Method)
```

**Prefetch limit exceeded:**
```
java.io.IOException: Prefetch limit exceeded
    at com.rabbitmq.client.impl.ChannelImpl.basicQos(ChannelImpl.java:234)
```

## Debugging Techniques

### Monitoring Queue Metrics

Use the RabbitMQ Management UI to monitor queue depth, message rate, and consumer count. Watch for increasing queue depth as the primary indicator of backlog. Check the "Messages" tab for unacked vs ready message counts.

Enable RabbitMQ metrics in Prometheus for alerting. Key metrics: `rabbitmq_queue_messages_ready`, `rabbitmq_queue_messages_unacked`, `rabbitmq_channel_messages_published_total`.

Use `rabbitmqadmin` CLI to inspect queue state and purge test messages. Use `rabbitmqctl list_queues` for quick status checks.

### Tracing Message Flow

Enable firehose to trace all messages through the broker. This adds overhead but helps debug routing issues. Use the management UI message tracer or `rabbitmq_tracing` plugin.

Add tracing headers to messages at the producer. Include producer timestamp, message ID, and retry count. This helps track message age and identify delays.

Check consumer logs for processing errors. Look for "negative acknowledge" events in the logs. Use dead letter exchanges to capture failed messages for analysis.

## Best Practices

Configure dead letter exchanges for failed messages. Set TTL on queues to expire stale messages. Implement DLQ processing to analyze and address recurring failures.

Set appropriate prefetch count to control unacked message volume. Higher prefetch improves throughput but increases memory usage and delivery latency on failures.

Use message TTL and expiration for time-sensitive messages. Implement idempotency keys in consumers to handle duplicate deliveries safely.