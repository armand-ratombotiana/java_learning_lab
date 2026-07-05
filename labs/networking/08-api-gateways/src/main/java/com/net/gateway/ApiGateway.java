package com.net.gateway;

import java.util.*;
import java.util.concurrent.*;

public class ApiGateway {

    public static class Route {
        public final String path;
        public final String targetUrl;
        public final Set<String> methods;
        public final Map<String, String> headers;

        public Route(String path, String targetUrl, Set<String> methods) {
            this.path = path;
            this.targetUrl = targetUrl;
            this.methods = methods;
            this.headers = new HashMap<>();
        }

        public Route addHeader(String key, String value) {
            headers.put(key, value);
            return this;
        }
    }

    public static class GatewayContext {
        public final String method;
        public final String path;
        public final Map<String, String> headers;
        public final String body;
        public int statusCode;
        public String responseBody;

        public GatewayContext(String method, String path, Map<String, String> headers, String body) {
            this.method = method;
            this.path = path;
            this.headers = headers;
            this.body = body;
            this.statusCode = 200;
        }
    }

    public interface GatewayFilter {
        boolean filter(GatewayContext ctx);
    }

    public static class SimpleGateway {
        private final List<Route> routes = new ArrayList<>();
        private final List<GatewayFilter> preFilters = new ArrayList<>();
        private final List<GatewayFilter> postFilters = new ArrayList<>();
        private final Map<String, Integer> rateCounts = new ConcurrentHashMap<>();

        public void addRoute(Route route) {
            routes.add(route);
            System.out.println("Route: " + route.methods + " " + route.path + " -> " + route.targetUrl);
        }

        public void addPreFilter(GatewayFilter filter) {
            preFilters.add(filter);
        }

        public boolean route(GatewayContext ctx) {
            for (GatewayFilter filter : preFilters) {
                if (!filter.filter(ctx)) {
                    ctx.statusCode = 403;
                    ctx.responseBody = "{\"error\":\"Request blocked by filter\"}";
                    return false;
                }
            }

            Route matched = null;
            for (Route route : routes) {
                if (route.methods.contains(ctx.method) && ctx.path.startsWith(route.path)) {
                    matched = route;
                    break;
                }
            }

            if (matched == null) {
                ctx.statusCode = 404;
                ctx.responseBody = "{\"error\":\"No route found\"}";
                return false;
            }

            ctx.responseBody = "{\"from\":\"" + matched.targetUrl + "\",\"path\":\"" + ctx.path + "\"}";

            for (GatewayFilter filter : postFilters) {
                filter.filter(ctx);
            }

            System.out.println(ctx.method + " " + ctx.path + " -> " + ctx.statusCode);
            return true;
        }
    }

    public static void main(String[] args) {
        SimpleGateway gateway = new SimpleGateway();

        gateway.addPreFilter(ctx -> {
            System.out.println("  [PreFilter] " + ctx.method + " " + ctx.path);
            return true;
        });

        gateway.addPostFilter(ctx -> {
            ctx.responseBody = ctx.responseBody.replace("}", ",\"gateway\":\"api-gw-1\"}");
            return true;
        });

        gateway.addRoute(new Route("/users", "http://user-service:8081", Set.of("GET", "POST", "PUT")));
        gateway.addRoute(new Route("/orders", "http://order-service:8082", Set.of("GET", "POST")));
        gateway.addRoute(new Route("/products", "http://product-service:8083", Set.of("GET")));

        System.out.println("\n=== Requests ===");
        gateway.route(new GatewayContext("GET", "/users", new HashMap<>(), null));
        gateway.route(new GatewayContext("POST", "/orders", new HashMap<>(), "{\"item\":\"book\"}"));
        gateway.route(new GatewayContext("DELETE", "/admin", new HashMap<>(), null));
    }
}
