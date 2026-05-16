# Prompt Engineering - MINI PROJECT

## Project: Text Processing Pipeline

Build a multi-task text processing system using various prompt techniques.

### Implementation

```java
public class TextProcessingPipeline {
    
    public String process(String text, TaskType task) {
        String prompt;
        
        switch (task) {
            case SUMMARIZE:
                prompt = buildSummarizePrompt(text);
                break;
            case EXTRACT_ENTITIES:
                prompt = buildExtractPrompt(text);
                break;
            case CLASSIFY:
                prompt = buildClassifyPrompt(text);
                break;
            case TRANSLATE:
                prompt = buildTranslatePrompt(text);
                break;
            default:
                prompt = text;
        }
        
        return callLLM(prompt);
    }
    
    private String buildSummarizePrompt(String text) {
        return PromptTemplate.summarization()
            .withVariable("text_type", "article")
            .withVariable("num_sentences", "3")
            .withVariable("input_text", text)
            .render();
    }
    
    private String buildExtractPrompt(String text) {
        return PromptTemplate.extraction()
            .withVariable("entity_types", "person, organization, location, date")
            .withVariable("input_text", text)
            .render();
    }
    
    private String buildClassifyPrompt(String text) {
        return new FewShotPromptBuilder()
            .setSystemPrompt("Classify the text category")
            .addExample("Sale happening at store", "shopping")
            .addExample("Flight delayed 2 hours", "travel")
            .addExample("Server crashed", "technical")
            .build(text);
    }
    
    private String buildTranslatePrompt(String text) {
        return "Translate to Spanish: " + text;
    }
    
    private String callLLM(String prompt) {
        return "Processed result for: " + prompt.substring(0, 20);
    }
}

enum TaskType {
    SUMMARIZE, EXTRACT_ENTITIES, CLASSIFY, TRANSLATE
}
```

### Testing

```java
public class TestPipeline {
    public static void main(String[] args) {
        TextProcessingPipeline pipeline = new TextProcessingPipeline();
        
        String article = "NASA announced a new Mars mission. " +
                        "The rover will launch in 2026. " +
                        "Scientists hope to find evidence of ancient life.";
        
        System.out.println("Summary:");
        System.out.println(pipeline.process(article, TaskType.SUMMARIZE));
        
        System.out.println("\nEntities:");
        System.out.println(pipeline.process(article, TaskType.EXTRACT_ENTITIES));
        
        System.out.println("\nCategory:");
        System.out.println(pipeline.process(article, TaskType.CLASSIFY));
    }
}
```

## Deliverables

- [ ] Build few-shot classifier
- [ ] Create extraction prompt template
- [ ] Implement summarization prompt
- [ ] Add chain-of-thought for reasoning
- [ ] Test prompt variations
- [ ] Measure accuracy