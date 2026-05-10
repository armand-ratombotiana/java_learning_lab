package com.learning.lab.module36;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Lab {
    public static void main(String[] args) throws IOException {
        System.out.println("=== Module 36: Elasticsearch Lab ===\n");

        RestClient restClient = RestClient.builder(
                new HttpHost("localhost", 9200, "http")).build();
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());
        ElasticsearchClient client = new ElasticsearchClient(transport);

        System.out.println("1. ES Client Configuration:");
        System.out.println("   - Connection: http://localhost:9200");
        System.out.println("   - JSON Mapper: Jackson");
        System.out.println("   - Protocol: REST API");

        System.out.println("\n2. Index Operations:");
        createIndex(client, "products");
        indexDocument(client, "products", "1", Map.of("name", "Laptop", "price", 999, "category", "electronics"));
        indexDocument(client, "products", "2", Map.of("name", "Phone", "price", 599, "category", "electronics"));
        indexDocument(client, "products", "3", Map.of("name", "Shirt", "price", 29, "category", "clothing"));

        System.out.println("\n3. Search Operations:");
        searchByKeyword(client, "products", "Laptop");
        searchByRange(client, "products", "price", 0, 500);

        System.out.println("\n4. Aggregations:");
        aggregateByField(client, "products", "category");

        transport.close();
        restClient.close();
        System.out.println("\n=== Elasticsearch Lab Complete ===");
    }

    static void createIndex(ElasticsearchClient client, String indexName) throws IOException {
        boolean exists = client.indices().exists(e -> e.index(indexName)).value();
        if (!exists) {
            client.indices().create(c -> c.index(indexName));
            System.out.println("   Created index: " + indexName);
        } else {
            System.out.println("   Index exists: " + indexName);
        }
    }

    static void indexDocument(ElasticsearchClient client, String index, String id, Map<String, Object> doc) throws IOException {
        client.index(i -> i.index(index).id(id).document(doc));
        System.out.println("   Indexed document: " + id);
    }

    static void searchByKeyword(ElasticsearchClient client, String index, String query) throws IOException {
        SearchResponse<Map> response = client.search(s -> s
                .index(index)
                .query(q -> q.match(m -> m.field("name").query(query))),
                Map.class);
        System.out.println("   Search for '" + query + "': " + response.hits().hits().size() + " hits");
        for (Hit<Map> hit : response.hits().hits()) {
            System.out.println("      - " + hit.source());
        }
    }

    static void searchByRange(ElasticsearchClient client, String index, String field, int from, int to) throws IOException {
        SearchResponse<Map> response = client.search(s -> s
                .index(index)
                .query(q -> q.range(r -> r.field(field).gte(from).lte(to))),
                Map.class);
        System.out.println("   Range search [" + from + "-" + to + "]: " + response.hits().hits().size() + " hits");
    }

    static void aggregateByField(ElasticsearchClient client, String index, String field) throws IOException {
        SearchResponse<Map> response = client.search(s -> s
                .index(index)
                .size(0)
                .aggregations(field, a -> a.terms(t -> t.field(field).size(10))),
                Map.class);
        System.out.println("   Aggregation by '" + field + "':");
        for (StringTermsBucket bucket : response.aggregations().get(field).sterms().buckets().array()) {
            System.out.println("      - " + bucket.key().string() + ": " + bucket.docCount());
        }
    }
}