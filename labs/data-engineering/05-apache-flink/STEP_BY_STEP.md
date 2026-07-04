# Step-by-Step

1. **Setup**: Add flink-java, flink-streaming dependencies
2. **Create Env**: StreamExecutionEnvironment.getExecutionEnvironment()
3. **Add Source**: env.addSource(new FlinkKafkaConsumer<>(...))
4. **Transform**: .keyBy().window().aggregate()
5. **Add Sink**: .addSink(new FlinkKafkaProducer<>(...))
6. **Execute**: env.execute("job-name")
