package com.javaacademy.lab37.profiling;

import jdk.jfr.*;
import java.util.ArrayList;
import java.util.List;

public class JfrEventExample {

    @Label("Database Query")
    @Description("Duration of a database query operation")
    @Category({"Performance", "Database"})
    @StackTrace(true)
    public static class DatabaseQueryEvent extends Event {
        @Label("Query")
        public String query;

        @Label("Connection Pool")
        public String pool;

        @Label("Rows Returned")
        public int rows;
    }

    @Label("HTTP Request")
    @Description("Outgoing HTTP request timing")
    @Category({"Performance", "Network"})
    public static class HttpRequestEvent extends Event {
        @Label("URL")
        public String url;

        @Label("Status Code")
        public int statusCode;

        @Label("Response Size")
        public long responseSize;
    }

    @Label("Cache Operation")
    @Description("Cache get/put operation")
    @Category({"Performance", "Cache"})
    public static class CacheEvent extends Event {
        @Label("Cache Name")
        public String cacheName;

        @Label("Operation")
        public String operation;

        @Label("Hit")
        public boolean hit;
    }

    public void simulateDbQuery(String query) {
        DatabaseQueryEvent event = new DatabaseQueryEvent();
        event.query = query;
        event.pool = "primary-pool";
        event.begin();
        try {
            Thread.sleep(50);
            event.rows = 42;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            event.commit();
        }
    }

    public void simulateHttpCall(String url, int statusCode) {
        HttpRequestEvent event = new HttpRequestEvent();
        event.url = url;
        event.statusCode = statusCode;
        event.begin();
        try {
            Thread.sleep(30);
            event.responseSize = 2048;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            event.commit();
        }
    }

    public void simulateCacheAccess(String cacheName, boolean hit) {
        CacheEvent event = new CacheEvent();
        event.cacheName = cacheName;
        event.operation = hit ? "GET" : "MISS";
        event.hit = hit;
        event.begin();
        try {
            Thread.sleep(hit ? 1 : 10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            event.commit();
        }
    }

    public void runWorkload() {
        for (int i = 0; i < 10; i++) {
            simulateDbQuery("SELECT * FROM users WHERE id = " + i);
            simulateHttpCall("https://api.example.com/users/" + i, 200);
            simulateCacheAccess("user-cache", i % 3 != 0);
        }
    }
}
