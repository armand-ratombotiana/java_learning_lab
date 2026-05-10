package com.learning.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ElasticsearchSolution {

    private final ElasticsearchClient client;

    public ElasticsearchSolution(ElasticsearchClient client) {
        this.client = client;
    }

    public void createIndex(String indexName) throws IOException {
        client.indices().create(c -> c.index(indexName));
    }

    public void indexDocument(String index, String id, Map<String, Object> document) throws IOException {
        client.index(i -> i
            .index(index)
            .id(id)
            .document(document)
        );
    }

    public List<Hit<Map>> searchDocuments(String index, String query) throws IOException {
        SearchResponse<Map> response = client.search(s -> s
            .index(index)
            .query(q -> q
                .match(m -> m.field("content").query(query))
            ),
            Map.class
        );
        return response.hits().hits();
    }

    public Map<String, JsonData> aggregateByField(String index, String field) throws IOException {
        SearchResponse<Void> response = client.search(s -> s
            .index(index)
            .size(0)
            .aggregations("terms_agg", Aggregation.of(a -> a
                .terms(t -> t.field(field))
            ))
        , Void.class);

        return response.aggregations();
    }

    public void bulkIndex(String index, List<Map<String, Object>> documents) throws IOException {
        BulkRequest.Builder bre = new BulkRequest.Builder();
        for (int i = 0; i < documents.size(); i++) {
            bre.operations(op -> op
                .index(idx -> idx
                    .index(index)
                    .id(String.valueOf(i))
                    .document(documents.get(i))
                )
            );
        }
        client.bulk(bre.build());
    }
}