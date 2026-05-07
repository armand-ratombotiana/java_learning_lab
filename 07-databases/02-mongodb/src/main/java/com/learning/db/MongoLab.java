package com.learning.db;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.annotation.Id;

import java.util.List;

@SpringBootApplication
public class MongoLab {
    public static void main(String[] args) {
        SpringApplication.run(MongoLab.class, args);
    }
}

class Document {
    @Id
    private String id;
    private String title;
    private String content;
    private List<String> tags;

    public Document() {}
    public Document(String title, String content, List<String> tags) {
        this.title = title; this.content = content; this.tags = tags;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}

interface DocumentRepository extends MongoRepository<Document, String> {
    List<Document> findByTitleContaining(String title);
    List<Document> findByTagsContaining(String tag);
}

@RestController
class DocumentController {
    private final DocumentRepository repository;

    DocumentController(DocumentRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/documents")
    List<Document> all() { return repository.findAll(); }

    @PostMapping("/documents")
    Document create(@RequestBody Document doc) {
        return repository.save(doc);
    }

    @GetMapping("/documents/search")
    List<Document> search(@RequestParam String title) {
        return repository.findByTitleContaining(title);
    }
}