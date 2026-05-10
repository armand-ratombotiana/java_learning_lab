package com.learning.lab.module21.solution;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;

public class Test {

    @Test void testProducerCreation() { Map<String,Object> config = Map.of("bootstrap.servers", "localhost:9092"); Solution.KafkaProducer<String,String> prod = new Solution.DefaultKafkaProducer<>(config); assertNotNull(prod); }
    @Test void testProducerSend() { Solution.KafkaProducer<String,String> prod = new Solution.DefaultKafkaProducer<>(Map.of()); prod.send("topic", "key", "value"); }
    @Test void testProducerSendAsync() { Solution.KafkaProducer<String,String> prod = new Solution.DefaultKafkaProducer<>(Map.of()); prod.sendAsync("topic", "key", "value", (m,e) -> {}); }
    @Test void testProducerFlush() { Solution.KafkaProducer<String,String> prod = new Solution.DefaultKafkaProducer<>(Map.of()); prod.flush(); }
    @Test void testProducerClose() { Solution.KafkaProducer<String,String> prod = new Solution.DefaultKafkaProducer<>(Map.of()); prod.close(); }
    @Test void testRecordMetadata() { Solution.RecordMetadata meta = new Solution.RecordMetadata("topic", 0, 100L); assertEquals("topic", meta.getTopic()); assertEquals(0, meta.getPartition()); assertEquals(100L, meta.getOffset()); }
    @Test void testConsumerCreation() { Map<String,Object> config = Map.of("bootstrap.servers", "localhost:9092"); Solution.KafkaConsumer<String,String> cons = new Solution.DefaultKafkaConsumer<>(config); assertNotNull(cons); }
    @Test void testConsumerSubscribe() { Solution.KafkaConsumer<String,String> cons = new Solution.DefaultKafkaConsumer<>(Map.of()); cons.subscribe("test-topic"); }
    @Test void testConsumerSubscribeMultiple() { Solution.KafkaConsumer<String,String> cons = new Solution.DefaultKafkaConsumer<>(Map.of()); cons.subscribe(List.of("topic1", "topic2")); }
    @Test void testConsumerPoll() { Solution.KafkaConsumer<String,String> cons = new Solution.DefaultKafkaConsumer<>(Map.of()); cons.subscribe("topic"); Solution.ConsumerRecords<String,String> recs = cons.poll(1000); assertTrue(recs.count() > 0); }
    @Test void testConsumerClose() { Solution.KafkaConsumer<String,String> cons = new Solution.DefaultKafkaConsumer<>(Map.of()); cons.close(); }
    @Test void testConsumerRecord() { Solution.ConsumerRecord<String,String> rec = new Solution.ConsumerRecord<>("topic", 0, 1L, "key", "value", System.currentTimeMillis()); assertEquals("topic", rec.getTopic()); assertEquals("value", rec.getValue()); }
    @Test void testConsumerRecordsCount() { Solution.ConsumerRecords<String,String> recs = new Solution.ConsumerRecords<>(); Solution.TopicPartition tp = new Solution.TopicPartition("topic", 0); recs.add(tp, List.of(new Solution.ConsumerRecord<>("topic", 0, 1L, null, "msg1", 0))); assertEquals(1, recs.count()); }
    @Test void testTopicPartition() { Solution.TopicPartition tp = new Solution.TopicPartition("topic", 2); assertEquals("topic", tp.getTopic()); assertEquals(2, tp.getPartition()); }
    @Test void testStreamsBuilder() { Solution.StreamsBuilder builder = new Solution.StreamsBuilder(); builder.stream("in").to("out"); assertNotNull(builder.build()); }
    @Test void testKafkaStreamsStart() { Solution.KafkaStreams streams = new Solution.DefaultKafkaStreams("in", "out"); streams.start(); }
    @Test void testKafkaStreamsStop() { Solution.KafkaStreams streams = new Solution.DefaultKafkaStreams("in", "out"); streams.start(); streams.stop(); }
    @Test void testKafkaStreamsClose() { Solution.KafkaStreams streams = new Solution.DefaultKafkaStreams("in", "out"); streams.close(); }
    @Test void testAdminCreateTopic() { Solution.KafkaAdminClient admin = new Solution.DefaultKafkaAdminClient(); admin.createTopic("test-topic", 3, (short)1); assertTrue(admin.listTopics().contains("test-topic")); }
    @Test void testAdminDeleteTopic() { Solution.KafkaAdminClient admin = new Solution.DefaultKafkaAdminClient(); admin.createTopic("test", 1, (short)1); admin.deleteTopic("test"); assertFalse(admin.listTopics().contains("test")); }
    @Test void testAdminListTopics() { Solution.KafkaAdminClient admin = new Solution.DefaultKafkaAdminClient(); admin.createTopic("topic1", 1, (short)1); admin.createTopic("topic2", 1, (short)1); assertEquals(2, admin.listTopics().size()); }
    @Test void testTopicDescription() { Solution.TopicDescription td = new Solution.TopicDescription("test", 3, (short)1); assertEquals("test", td.getName()); assertEquals(3, td.getPartitions()); }
    @Test void testStringSerializer() { Solution.Serializer<String> ser = new Solution.StringSerializer(); byte[] bytes = ser.serialize("topic", "test"); assertNotNull(bytes); }
    @Test void testStringDeserializer() { Solution.Deserializer<String> des = new Solution.StringDeserializer(); String result = des.deserialize("topic", "test".getBytes()); assertEquals("test", result); }
    @Test void testMessageCreation() { Solution.Message<String,String> msg = new Solution.Message<>("key", "value", null); assertEquals("key", msg.getKey()); assertEquals("value", msg.getValue()); }
    @Test void testHeaders() { Solution.Headers headers = new Solution.Headers(); headers.add("traceId", "123".getBytes()); assertNotNull(headers.get("traceId")); }
    @Test void testKafkaConfigConstants() { assertNotNull(Solution.KafkaConfig.BOOTSTRAP_SERVERS); assertNotNull(Solution.KafkaConfig.GROUP_ID); }
    @Test void testAsyncSendWithCallback() { Solution.KafkaProducer<String,String> prod = new Solution.DefaultKafkaProducer<>(Map.of()); prod.sendAsync("topic", "key", "value", (meta, ex) -> assertNotNull(meta)); }
}