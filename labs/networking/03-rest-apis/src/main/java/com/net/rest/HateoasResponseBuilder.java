package com.net.rest;

import java.util.*;

public class HateoasResponseBuilder {

    public static class Link {
        public final String rel;
        public final String href;
        public final String method;

        public Link(String rel, String href, String method) {
            this.rel = rel;
            this.href = href;
            this.method = method;
        }
    }

    public static class HateoasResponse {
        public final Map<String, Object> data;
        public final List<Link> links;

        public HateoasResponse(Map<String, Object> data, List<Link> links) {
            this.data = data;
            this.links = links;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("{\n  \"data\": ").append(data);
            sb.append(",\n  \"_links\": [\n");
            for (int i = 0; i < links.size(); i++) {
                Link l = links.get(i);
                sb.append("    {\"rel\":\"").append(l.rel)
                  .append("\",\"href\":\"").append(l.href)
                  .append("\",\"method\":\"").append(l.method).append("\"}");
                if (i < links.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("  ]\n}");
            return sb.toString();
        }
    }

    public static class HateoasBuilder {
        private final Map<String, Object> data = new LinkedHashMap<>();
        private final List<Link> links = new ArrayList<>();

        public HateoasBuilder withData(String key, Object value) {
            data.put(key, value);
            return this;
        }

        public HateoasBuilder withSelf(String href) {
            links.add(new Link("self", href, "GET"));
            return this;
        }

        public HateoasBuilder withLink(String rel, String href, String method) {
            links.add(new Link(rel, href, method));
            return this;
        }

        public HateoasResponse build() {
            return new HateoasResponse(data, links);
        }
    }

    public static void main(String[] args) {
        HateoasResponse userResponse = new HateoasBuilder()
            .withData("id", 1)
            .withData("name", "Alice")
            .withData("email", "alice@example.com")
            .withSelf("/api/users/1")
            .withLink("orders", "/api/users/1/orders", "GET")
            .withLink("update", "/api/users/1", "PUT")
            .withLink("delete", "/api/users/1", "DELETE")
            .build();

        System.out.println("HATEOAS Response:");
        System.out.println(userResponse);

        System.out.println("\nNavigating links:");
        for (Link link : userResponse.links) {
            System.out.println("  [" + link.rel + "] " + link.method + " " + link.href);
        }
    }
}
