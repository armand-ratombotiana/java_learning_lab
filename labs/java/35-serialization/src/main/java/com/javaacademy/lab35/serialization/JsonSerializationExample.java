package com.javaacademy.lab35.serialization;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.StringWriter;

public class JsonSerializationExample {

    private final ObjectMapper mapper;

    public JsonSerializationExample() {
        mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .enable(SerializationFeature.INDENT_OUTPUT);
    }

    public String toJson(Object obj) throws Exception {
        return mapper.writeValueAsString(obj);
    }

    public <T> T fromJson(String json, Class<T> clazz) throws Exception {
        return mapper.readValue(json, clazz);
    }

    public <T> T fromJson(String json, TypeReference<T> typeRef) throws Exception {
        return mapper.readValue(json, typeRef);
    }

    public String prettyPrint(Object obj) throws Exception {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }

    public JsonNode parseToTree(String json) throws Exception {
        return mapper.readTree(json);
    }

    public ObjectMapper getMapper() { return mapper; }
}
