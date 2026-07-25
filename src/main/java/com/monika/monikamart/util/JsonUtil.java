package com.monika.monikamart.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

public class JsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    private JsonUtil() {}

    public static ObjectMapper getMapper() {
        return MAPPER;
    }

    public static String toJson(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting object to JSON", e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Error parsing JSON to object", e);
        }
    }

    public static <T> T fromJson(InputStream inputStream, Class<T> clazz) {
        try {
            return MAPPER.readValue(inputStream, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Error reading JSON from stream", e);
        }
    }

    public static void writeJson(Writer writer, Object object) {
        try {
            MAPPER.writeValue(writer, object);
        } catch (IOException e) {
            throw new RuntimeException("Error writing JSON to response stream", e);
        }
    }
}
