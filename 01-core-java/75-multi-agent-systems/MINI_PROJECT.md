# Mini Project: Multi-Agent Research & Writing Team

## Objective
Build a hierarchical Multi-Agent System in Spring Boot. A "Manager Agent" will receive a topic from the user. It will delegate the research to a "Researcher Agent" (which has access to a mock search tool) and then pass those findings to a "Writer Agent" to format a final blog post.

## Prerequisites
*   OpenAI API Key
*   Java 21 & Spring Boot 3.2+

## Step 1: Dependencies
Add `spring-boot-starter-web` and `spring-ai-openai-spring-boot-starter` to your `pom.xml`.

## Step 2: Define the Research Tool
Create a mock tool that the Researcher Agent can use to look up facts.

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import java.util.function.Function;

@Configuration
public class ToolsConfig {

    public record SearchRequest(String query) {}
    public record SearchResponse(String results) {}

    @Bean
    @Description("Search the web for current facts and information")
    public Function<SearchRequest, SearchResponse> webSearch() {
        return request -> {
            // Mocking a web search
            if (request.query().toLowerCase().contains("java 21")) {
                return new SearchResponse("Java 21 introduced Virtual Threads, Sequenced Collections, and Record Patterns.");
            }
            return new SearchResponse("No specific information found. Assume general knowledge.");
        };
    }
}
```

## Step 3: Define the Worker Agents (As Tools)
Wrap the Researcher and Writer agents as `Function` beans so the Manager can call them.

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import java.util.function.Function;

@Configuration
public class AgentConfig {

    public record ResearchTask(String topic) {}
    public record ResearchResult(String facts) {}

    @Bean
    @Description("Delegates research tasks to the Researcher Agent. Use this to gather facts.")
    public Function<ResearchTask, ResearchResult> researcherAgent(ChatClient.Builder builder) {
        ChatClient researcher = builder
                .defaultSystem("You are a meticulous researcher. Use the webSearch tool to gather facts. Summarize the findings concisely.")
                .defaultFunctions("webSearch")
                .build();
                
        return request -> {
            String facts = researcher.prompt().user("Research this topic: " + request.topic()).call().content();
            return new ResearchResult(facts);
        };
    }

    public record WritingTask(String facts, String tone) {}
    public record WritingResult(String article) {}

    @Bean
    @Description("Delegates writing tasks to the Writer Agent. Use this to turn facts into a well-formatted article.")
    public Function<WritingTask, WritingResult> writerAgent(ChatClient.Builder builder) {
        ChatClient writer = builder
                .defaultSystem("You are an expert tech blogger. Write engaging, well-structured markdown articles based ONLY on the provided facts.")
                .build();
                
        return request -> {
            String prompt = String.format("Write a %s article using these facts: %s", request.tone(), request.facts());
            String article = writer.prompt().user(prompt).call().content();
            return new WritingResult(article);
        };
    }
}
```

## Step 4: Define the Manager Agent & Controller
Create the orchestrator that manages the workflow.

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mas")
public class ManagerController {

    private final ChatClient manager;

    public ManagerController(ChatClient.Builder builder) {
        this.manager = builder
            .defaultSystem("""
                You are the Managing Editor. Your job is to fulfill the user's request for an article.
                Step 1: Call the researcherAgent to gather facts about the topic.
                Step 2: Call the writerAgent, passing the facts you received and requesting a 'professional' tone.
                Step 3: Return the final article to the user. Do not add your own commentary.
                """)
            .defaultFunctions("researcherAgent", "writerAgent")
            .build();
    }

    @PostMapping("/generate-article")
    public String generateArticle(@RequestBody String topic) {
        return manager.prompt()
            .user("I need an article about: " + topic)
            .call()
            .content();
    }
}
```

## Step 5: Test the System
Send a POST request to `/api/mas/generate-article` with the body `Java 21 features`.
*Observe the logs (if you enabled `SimpleLoggerAdvisor`): You will see the Manager call the Researcher, the Researcher call the `webSearch` tool, the Researcher return facts to the Manager, the Manager pass those facts to the Writer, and finally, the Manager returns the completed markdown article.*