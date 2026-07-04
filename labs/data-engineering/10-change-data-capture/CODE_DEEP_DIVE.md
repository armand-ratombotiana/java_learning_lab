# Code Deep Dive: Debezium CDC

## Spring Boot Integration
```java
@Configuration
public class CdcConfig {
    @Bean
    public DebeziumEngine<ChangeEvent<String, String>> debeziumEngine() {
        Properties props = new Properties();
        props.setProperty("connector.class", "io.debezium.connector.mysql.MySqlConnector");
        props.setProperty("database.hostname", System.getenv("DB_HOST"));
        props.setProperty("database.port", "3306");
        props.setProperty("database.user", System.getenv("DB_USER"));
        props.setProperty("database.password", System.getenv("DB_PASSWORD"));
        props.setProperty("topic.prefix", "cdc");
        props.setProperty("schema.history.internal.kafka.topic", "schemahistory");

        return DebeziumEngine.create(Json.class)
            .using(props)
            .notifying(this::handleChangeEvent)
            .build();
    }

    private void handleChangeEvent(ChangeEvent<String, String> event) {
        JsonNode payload = new ObjectMapper().readTree(event.value()).get("payload");
        String op = payload.get("op").asText();
        switch (op) {
            case "c": handleCreate(payload.get("after")); break;
            case "u": handleUpdate(payload.get("before"), payload.get("after")); break;
            case "d": handleDelete(payload.get("before")); break;
        }
    }
}
```
