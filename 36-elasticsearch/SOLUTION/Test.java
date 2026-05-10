package com.learning.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.json.JsonData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ElasticsearchSolutionTest {

    private ElasticsearchClient mockClient;
    private ElasticsearchSolution solution;

    @BeforeEach
    void setUp() {
        mockClient = mock(ElasticsearchClient.class);
        solution = new ElasticsearchSolution(mockClient);
    }

    @Test
    void testCreateIndex() throws IOException {
        solution.createIndex("test-index");
        verify(mockClient.indices()).create(any());
    }

    @Test
    void testSearchDocuments() throws IOException {
        var mockSearch = mock(co.elastic.clients.elasticsearch.core.SearchRequest.Builder.class);
        when(mockClient.search(any(), eq(Map.class))).thenReturn(
            mock(co.elastic.clients.elasticsearch.core.SearchResponse.class)
        );
        List<Map> results = solution.searchDocuments("test-index", "test query");
        assertNotNull(results);
    }

    @Test
    void testAggregateByField() throws IOException {
        Map<String, JsonData> aggregations = solution.aggregateByField("test-index", "category");
        assertNotNull(aggregations);
    }

    @Test
    void testBulkIndex() throws IOException {
        List<Map<String, Object>> docs = List.of(
            Map.of("content", "test1"),
            Map.of("content", "test2")
        );
        solution.bulkIndex("test-index", docs);
        verify(mockClient).bulk(any());
    }
}