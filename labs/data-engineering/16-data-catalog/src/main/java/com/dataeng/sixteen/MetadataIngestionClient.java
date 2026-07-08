package com.dataeng.sixteen;

import java.net.http.*;
import java.net.URI;
import java.util.*;

public class MetadataIngestionClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String catalogUrl;
    private final String apiKey;

    public MetadataIngestionClient(String catalogUrl, String apiKey) {
        this.catalogUrl = catalogUrl;
        this.apiKey = apiKey;
    }

    public record ColumnMetadata(String name, String type, String description) {}
    public record DatasetMetadata(String database, String schema, String table,
                                   List<ColumnMetadata> columns, String description, String source) {}

    public boolean ingestDataset(DatasetMetadata metadata) {
        try {
            String json = toJson(metadata);
            var request = HttpRequest.newBuilder()
                .uri(URI.create(catalogUrl + "/api/v1/datasets"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 || response.statusCode() == 201;
        } catch (Exception e) {
            System.err.println("Ingestion failed: " + e.getMessage());
            return false;
        }
    }

    public boolean submitLineage(String json) {
        try {
            var request = HttpRequest.newBuilder()
                .uri(URI.create(catalogUrl + "/api/v1/lineage"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            System.err.println("Lineage submission failed: " + e.getMessage());
            return false;
        }
    }

    private String toJson(DatasetMetadata m) {
        return String.format(
            "{"database":"%s","schema":"%s","table":"%s","columns":%s,"description":"%s","source":"%s"}",
            m.database(), m.schema(), m.table(), columnsToJson(m.columns()), m.description(), m.source());
    }

    private String columnsToJson(List<ColumnMetadata> cols) {
        var sb = new StringBuilder("[");
        for (int i = 0; i < cols.size(); i++) {
            var c = cols.get(i);
            sb.append(String.format("{"name":"%s","type":"%s","description":"%s"}", c.name(), c.type(), c.description()));
            if (i < cols.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
