# Prompt Engineering - REAL WORLD PROJECT

## Project: Multi-Tenant LLM Application Platform

Build a production platform that handles multiple tenants with custom prompts.

### Architecture

```
Request → Auth → Tenant Lookup → Prompt Manager → LLM → Response
              ↓
         Tenant Config
```

### Implementation

```java
@Service
public class PromptManagementService {
    private Map<String, TenantPromptConfig> tenantConfigs;
    private PromptVersionManager versionManager;
    
    @PostConstruct
    public void initialize() {
        tenantConfigs = new HashMap<>();
    }
    
    public String buildPrompt(String tenantId, PromptRequest request) {
        TenantPromptConfig config = tenantConfigs.get(tenantId);
        
        if (config == null) {
            throw new TenantNotFoundException(tenantId);
        }
        
        String basePrompt = config.getBasePrompt();
        
        if (request.getExamples() != null) {
            basePrompt = addExamples(basePrompt, request.getExamples());
        }
        
        if (request.getContext() != null) {
            basePrompt = addContext(basePrompt, request.getContext());
        }
        
        return basePrompt + "\n\n" + request.getTask();
    }
    
    private String addExamples(String prompt, List<Example> examples) {
        StringBuilder sb = new StringBuilder(prompt);
        
        sb.append("\n\nExamples:\n");
        
        for (Example ex : examples) {
            sb.append("Input: ").append(ex.input()).append("\n");
            sb.append("Output: ").append(ex.output()).append("\n");
        }
        
        return sb.toString();
    }
    
    private String addContext(String prompt, String context) {
        return prompt + "\n\nContext:\n" + context;
    }
    
    public void registerTenant(String tenantId, TenantPromptConfig config) {
        tenantConfigs.put(tenantId, config);
        versionManager.initializeVersion(tenantId);
    }
}
```

### API

```java
@RestController
@RequestMapping("/api/v1/prompts")
public class PromptController {
    
    @PostMapping("/execute")
    public ResponseEntity<PromptResponse> execute(
            @RequestBody PromptRequest request,
            @RequestHeader("X-Tenant-Id") String tenantId) {
        
        String prompt = promptService.buildPrompt(tenantId, request);
        
        String response = llmClient.generate(prompt);
        
        return ResponseEntity.ok(new PromptResponse(response));
    }
    
    @PostMapping("/validate")
    public ResponseEntity<ValidationResult> validate(
            @RequestBody PromptValidationRequest request) {
        
        ValidationResult result = promptEvaluator.evaluate(request.getPrompt());
        
        return ResponseEntity.ok(result);
    }
}
```

### Metrics

```java
public class PromptMetricsService {
    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        metrics.put("total_requests", counter.get());
        metrics.put("avg_latency", latencyCollector.getAverage());
        metrics.put("success_rate", successCollector.getRate());
        
        return metrics;
    }
}
```

## Deliverables

- [x] Multi-tenant prompt management
- [x] Version control for prompts
- [x] Prompt validation
- [x] Metrics collection
- [x] REST API
- [x] Caching layer
- [x] Rate limiting