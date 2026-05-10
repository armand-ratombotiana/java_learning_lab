package com.learning.lab.module22.solution;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;

public class Test {

    @Test void testConnectionFactoryDefaults() { Solution.ConnectionFactory factory = new Solution.ConnectionFactory(); assertEquals("localhost", factory.newConnection().getClass().getDeclaredField("host").getType()); }
    @Test void testConnectionFactorySetters() { Solution.ConnectionFactory factory = new Solution.ConnectionFactory(); factory.setHost("rabbitmq.local"); factory.setPort(5673); factory.setUsername("admin"); factory.setPassword("admin"); }
    @Test void testConnectionNewConnection() { Solution.ConnectionFactory factory = new Solution.ConnectionFactory(); Solution.Connection conn = factory.newConnection(); assertTrue(conn instanceof Solution.RabbitConnection); }
    @Test void testRabbitConnectionIsOpen() { Solution.RabbitConnection conn = new Solution.RabbitConnection("localhost", 5672, "user", "pass", "/"); assertTrue(conn.isOpen()); }
    @Test void testRabbitConnectionClose() { Solution.RabbitConnection conn = new Solution.RabbitConnection("localhost", 5672, "user", "pass", "/"); conn.close(); assertFalse(conn.isOpen()); }
    @Test void testChannelQueueDeclare() { Solution.Channel channel = new Solution.RabbitChannel(); channel.queueDeclare("test-queue", true, false, false, null); }
    @Test void testChannelExchangeDeclare() { Solution.Channel channel = new Solution.RabbitChannel(); channel.exchangeDeclare("test-exchange", "direct", true); }
    @Test void testChannelQueueBind() { Solution.Channel channel = new Solution.RabbitChannel(); channel.queueDeclare("queue", true, false, false, null); channel.exchangeDeclare("exchange", "direct", false); channel.queueBind("queue", "exchange", "key"); }
    @Test void testChannelBasicPublish() { Solution.Channel channel = new Solution.RabbitChannel(); channel.basicPublish("", "test", "Hello".getBytes()); }
    @Test void testChannelClose() { Solution.Channel channel = new Solution.RabbitChannel(); channel.close(); }
    @Test void testQueueCreation() { Solution.Queue q = new Solution.Queue("test", true, false, true, null); assertEquals("test", q.getName()); assertTrue(q.isDurable()); }
    @Test void testQueueBind() { Solution.Queue q = new Solution.Queue("test", false, false, false, null); q.bind("exchange", "routing-key"); assertEquals(1, q.getBindings().size()); }
    @Test void testExchangeCreation() { Solution.Exchange ex = new Solution.Exchange("orders", "direct", true); assertEquals("orders", ex.getName()); assertEquals("direct", ex.getType()); }
    @Test void testBindingCreation() { Solution.Binding b = new Solution.Binding("exchange", "key"); assertEquals("exchange", b.getExchange()); assertEquals("key", b.getRoutingKey()); }
    @Test void testMessageCreation() { Solution.Message msg = Solution.Message.withBody("test".getBytes()); assertNotNull(msg.getBody()); }
    @Test void testMessageWithProperties() { Map<String,Object> props = Map.of("priority", 1); Solution.Message msg = Solution.Message.withProperties("test".getBytes(), props); assertNotNull(msg.getProperties()); }
    @Test void testDeadLetterQueueCreation() { Solution.DeadLetterQueue dlq = new Solution.DeadLetterQueue("dlq", "dlx", "key"); assertEquals("dlq", dlq.getQueueName()); }
    @Test void testDeadLetterQueueCreate() { Solution.DeadLetterQueue dlq = Solution.DeadLetterQueue.create("order-queue"); assertTrue(dlq.getQueueName().contains(".dlq")); }
    @Test void testExchangeTypes() { assertEquals("direct", Solution.ExchangeType.DIRECT); assertEquals("fanout", Solution.ExchangeType.FANOUT); assertEquals("topic", Solution.ExchangeType.TOPIC); }
    @Test void testMessageProperties() { assertNotNull(Solution.MessageProperties.CONTENT_TYPE); assertNotNull(Solution.MessageProperties.CORRELATION_ID); }
    @Test void testConsumerCreation() { Solution.Consumer c = new Solution.Consumer("test-queue", false); assertNotNull(c); }
    @Test void testConsumerSetCallback() { Solution.Consumer c = new Solution.Consumer("queue", false); c.setDeliverCallback((tag, msg) -> {}); }
    @Test void testProducerCreation() { Solution.Channel ch = new Solution.RabbitChannel(); Solution.Producer p = new Solution.Producer(ch, ""); assertNotNull(p); }
    @Test void testProducerSend() { Solution.Channel ch = new Solution.RabbitChannel(); Solution.Producer p = new Solution.Producer(ch, ""); p.send("queue", "message"); }
    @Test void testProducerSendWithExchange() { Solution.Channel ch = new Solution.RabbitChannel(); Solution.Producer p = new Solution.Producer(ch, ""); p.send("exchange", "key", "msg"); }
    @Test void testRetryPolicyDefaults() { Solution.RetryPolicy rp = new Solution.RetryPolicy(); assertEquals(3, rp.getMaxAttempts()); assertEquals(1000, rp.getInitialInterval()); }
    @Test void testRetryPolicySetters() { Solution.RetryPolicy rp = new Solution.RetryPolicy(); rp.setMaxAttempts(5); rp.setInitialInterval(2000); assertEquals(5, rp.getMaxAttempts()); }
    @Test void testRetryPolicyNextInterval() { Solution.RetryPolicy rp = new Solution.RetryPolicy(); rp.setInitialInterval(1000); rp.setMultiplier(2.0); long interval = rp.getNextInterval(2); assertEquals(2000, interval); }
    @Test void testRetryPolicyMaxInterval() { Solution.RetryPolicy rp = new Solution.RetryPolicy(); rp.setInitialInterval(1000); rp.setMultiplier(2.0); rp.setMaxInterval(1500); long interval = rp.getNextInterval(10); assertTrue(interval <= 1500); }
}