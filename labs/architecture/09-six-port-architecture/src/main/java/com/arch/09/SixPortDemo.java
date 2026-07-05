package com.arch.sixport;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SixPortDemo {
    public static void main(String[] args) {
        DatabaseAdapter db = new DatabaseAdapter();
        Service service = new Service(db);

        service.execute();
    }

    interface DrivingPort {
        void execute();
    }

    interface DrivenPort {
        String read(String key);
        void write(String key, String value);
    }

    static class Service implements DrivingPort {
        private final DrivenPort driven;

        Service(DrivenPort driven) {
            this.driven = driven;
        }

        public void execute() {
            driven.write("name", "Six-Port Architecture");
            System.out.println("Stored: " + driven.read("name"));
        }
    }

    static class DatabaseAdapter implements DrivenPort {
        private final Map<String, String> store = new ConcurrentHashMap<>();

        public String read(String key) {
            return store.get(key);
        }

        public void write(String key, String value) {
            store.put(key, value);
        }
    }
}
