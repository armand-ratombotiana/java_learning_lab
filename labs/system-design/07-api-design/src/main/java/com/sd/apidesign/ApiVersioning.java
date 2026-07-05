package com.sd.apidesign;

import java.util.*;

public class ApiVersioning {

    public enum VersionLocation { URL, HEADER, QUERY_PARAM }

    public static class VersionedRouter {
        private final Map<String, Map<String, Handler>> routes = new HashMap<>();
        private final VersionLocation location;

        public interface Handler {
            Object handle(Map<String, String> params);
        }

        public VersionedRouter(VersionLocation location) {
            this.location = location;
        }

        public void register(String path, String version, Handler handler) {
            String key = path + ":" + version;
            routes.computeIfAbsent(key, k -> new HashMap<>());
            routes.get(key).put(path, handler);
            System.out.println("Registered: " + path + " v" + version);
        }

        public Object route(String path, String version, Map<String, String> params) {
            String key = path + ":" + version;
            Handler handler = routes.getOrDefault(key, routes.get(path + ":v1")).get(path);
            if (handler == null) {
                return Map.of("error", "No handler for " + path + " v" + version);
            }
            System.out.println("Routing " + path + " v" + version + " via " + location);
            return handler.handle(params);
        }
    }

    public static class UserAPI {
        public static Handler v1Handler = params -> Map.of("id", params.get("id"), "name", "User v1");
        public static Handler v2Handler = params -> Map.of("id", params.get("id"), "name", "User v2",
            "email", "user@example.com");
    }

    public static void main(String[] args) {
        VersionedRouter router = new VersionedRouter(VersionLocation.URL);

        router.register("/users", "v1", UserAPI.v1Handler);
        router.register("/users", "v2", UserAPI.v2Handler);

        System.out.println("\n=== API Versioning ===");
        Object v1Resp = router.route("/users", "v1", Map.of("id", "42"));
        System.out.println("v1: " + v1Resp);

        Object v2Resp = router.route("/users", "v2", Map.of("id", "42"));
        System.out.println("v2: " + v2Resp);
    }
}
