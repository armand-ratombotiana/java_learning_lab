package com.learning.lab.module79;

import java.util.*;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Module 79: LLM Integration - Patterns & Agents");
        System.out.println("=".repeat(60));

        llmIntegrationOverview();
        promptEngineering();
        functionCalling();
        chainOfThought();
        reActAgents();
        toolsAndActions();
        memoryManagement();
        retrievalAugmented();
        multiModalLlm();
        evaluationMetrics();
        productionPatterns();
        securityConsiderations();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("Module 79 Lab Complete!");
        System.out.println("=".repeat(60));
    }

    static void llmIntegrationOverview() {
        System.out.println("\n--- LLM Integration Overview ---");
        System.out.println("Integrating Large Language Models into applications");

        System.out.println("\nKey Integration Patterns:");
        System.out.println("   1. Direct API calls: Simple, straightforward");
        System.out.println("   2. Chain-based: Sequential processing");
        System.out.println("   3. Agent-based: Autonomous decision-making");
        System.out.println("   4. RAG: Retrieval-augmented generation");

        System.out.println("\nIntegration Components:");
        String[] components = {"LLM Client", "Prompt Management", "Output Parsing",
                             "Error Handling", "Caching", "Rate Limiting"};
        for (String comp : components) {
            System.out.println("   - " + comp);
        }

        System.out.println("\nPopular LLM Providers:");
        Map<String, String> providers = new LinkedHashMap<>();
        providers.put("OpenAI", "GPT-4, GPT-3.5");
        providers.put("Anthropic", "Claude 3.5, Claude 3");
        providers.put("Google", "Gemini Pro, Flash");
        providers.put("Meta", "Llama 3, Llama 2");
        providers.put("Mistral", "Mistral Large, Mixtral");
        System.out.println("   " + providers);

        System.out.println("\nJava Integration Libraries:");
        System.out.println("   - Spring AI: Spring Framework integration");
        System.out.println("   - LangChain4j: Java port of LangChain");
        System.out.println("   - OpenAI Java SDK: Direct API access");
    }

    static void promptEngineering() {
        System.out.println("\n--- Prompt Engineering ---");
        System.out.println("Crafting effective prompts for LLM responses");

        System.out.println("\nPrompt Components:");
        System.out.println("   1. System Prompt: Task definition and constraints");
        System.out.println("   2. User Prompt: Actual user request");
        System.out.println("   3. Context: Additional information");
        System.out.println("   4. Examples: Few-shot demonstrations");

        System.out.println("\nSystem Prompt Example:");
        String systemPrompt = "You are a Java programming expert. " +
            "Provide clear, well-commented code examples. " +
            "Include explanation before code.";
        System.out.println("   " + systemPrompt);

        System.out.println("\nUser Prompt Examples:");
        String userPrompt = "Explain how to create a thread pool in Java.";
        System.out.println("   " + userPrompt);

        System.out.println("\nPrompt Techniques:");

        System.out.println("\n1. Few-Shot Learning:");
        String fewShot = "Input: What is OOP?\nOutput: Object-Oriented Programming...\n\n" +
            "Input: What is inheritance?\nOutput: A mechanism where...";
        System.out.println("   " + fewShot.substring(0, 50) + "...");

        System.out.println("\n2. Chain-of-Thought:");
        System.out.println("   Let's think step by step...");

        System.out.println("\n3. Role Prompting:");
        System.out.println("   You are a senior software architect...");

        System.out.println("\n4. Constraints:");
        System.out.println("   - Output only JSON");
        System.out.println("   - Maximum 3 bullet points");
        System.out.println("   - Use technical terms only");

        System.out.println("\nBest Practices:");
        System.out.println("   - Be specific and clear");
        System.out.println("   - Provide examples for complex tasks");
        System.out.println("   - Use formatting for readability");
        System.out.println("   - Iterate and refine prompts");
    }

    static void functionCalling() {
        System.out.println("\n--- Function Calling ---");
        System.out.println("Enabling LLMs to invoke external functions/tools");

        System.out.println("\nFunction Call Flow:");
        System.out.println("   1. Define function schema");
        System.out.println("   2. Send function list to LLM");
        System.out.println("   3. LLM decides to call function");
        System.out.println("   4. Execute function with parameters");
        System.out.println("   5. Return result to LLM");
        System.out.println("   6. LLM generates final response");

        System.out.println("\nFunction Schema Definition:");
        String schema = "{" +
            "\"name\": \"get_weather\"," +
            "\"description\": \"Get weather for location\"," +
            "\"parameters\": {" +
            "  \"type\": \"object\"," +
            "  \"properties\": {" +
            "    \"location\": {\"type\": \"string\"}," +
            "    \"units\": {\"type\": \"string\", \"enum\": [\"celsius\", \"fahrenheit\"]}" +
            "  }," +
            "  \"required\": [\"location\"]" +
            "}" +
            "}";
        System.out.println("   " + schema.replace("\n", " ").replace("  ", ""));

        System.out.println("\nCalling a Function:");
        System.out.println("   ChatCompletionMessage response = chatClient.prompt()");
        System.out.println("       .functions(Arrays.asList(getWeather))");
        System.out.println("       .functionCall(FunctionCallMode.AUTO)");
        System.out.println("       .user(\"What's the weather in Tokyo?\")");
        System.out.println("       .call();");

        System.out.println("\nFunction Execution:");
        System.out.println("   if (response.hasFunctionCall()) {");
        System.out.println("       String functionName = response.getFunctionName();");
        System.out.println("       Map args = response.getArguments();");
        System.out.println("       Object result = executeFunction(functionName, args);");
        System.out.println("   }");

        System.out.println("\nUse Cases:");
        System.out.println("   - Database queries");
        System.out.println("   - API integrations");
        System.out.println("   - Calculations");
        System.out.println("   - File operations");
    }

    static void chainOfThought() {
        System.out.println("\n--- Chain of Thought (CoT) ---");
        System.out.println("Encouraging LLMs to show reasoning steps");

        System.out.println("\nCoT Benefits:");
        System.out.println("   - Improves accuracy on complex tasks");
        System.out.println("   - Makes reasoning transparent");
        System.out.println("   - Enables debugging");

        System.out.println("\nStandard CoT:");
        String[] steps = {
            "1. Problem: Sum of even numbers from 1-10",
            "2. Even numbers: 2, 4, 6, 8, 10",
            "3. Sum: 2 + 4 = 6, 6 + 6 = 12",
            "4. Continue: 12 + 8 = 20, 20 + 10 = 30",
            "5. Answer: 30"
        };
        System.out.println("   Steps:");
        for (String step : steps) {
            System.out.println("     " + step);
        }

        System.out.println("\nZero-Shot CoT:");
        System.out.println("   Prompt: \"Let's think step by step. Calculate: 15 * 7\"");

        System.out.println("\nFew-Shot CoT:");
        String fewShotCoT = "Problem: 12 + 8 = ?\n" +
            "Step 1: Add 10 + 8 = 18\n" +
            "Step 2: Add 2 more = 20\n" +
            "Answer: 20\n\n" +
            "Problem: 25 * 4 = ?\n" +
            "Step 1:";
        System.out.println("   " + fewShotCoT);

        System.out.println("\nTree of Thoughts:");
        System.out.println("   - Explores multiple reasoning paths");
        System.out.println("   - Branches and evaluates options");
        System.out.println("   - Backtracks if needed");

        System.out.println("\nImplementation:");
        System.out.println("   prompt = \"\"");
        System.out.println("       Let's think step by step.\n");
        System.out.println("       Problem: {problem}\n");
        System.out.println("       Step 1:");
        System.out.println("   \"\"\");\n");
    }

    static void reActAgents() {
        System.out.println("\n--- ReAct Agents ---");
        System.out.println("Reasoning + Action framework for LLM agents");

        System.out.println("\nReAct Cycle:");
        System.out.println("   Thought -> Action -> Observation -> Thought -> ...");

        System.out.println("\nExample ReAct Execution:");
        String[] trace = {
            "Thought: I need to find the weather in Paris",
            "Action: search_weather[Paris]",
            "Observation: 18°C, partly cloudy",
            "Thought: I have the weather info, now I can answer",
            "Action: respond[User asked about Paris weather. It is 18°C.]"
        };
        for (String step : trace) {
            System.out.println("   " + step);
        }

        System.out.println("\nReAct Agent Implementation:");
        System.out.println("   ReActAgent agent = ReActAgent.builder()");
        System.out.println("       .llm(chatModel)");
        System.out.println("       .tools(availableTools)");
        System.out.println("       .maxIterations(10)");
        System.out.println("       .build();");
        System.out.println("   ");
        System.out.println("   AgentResponse response = agent.execute(userQuery);");

        System.out.println("\nAgent State:");
        Map<String, String> state = new LinkedHashMap<>();
        state.put("thought", "Current reasoning");
        state.put("action", "Selected action");
        state.put("observation", "Action result");
        state.put("history", "Previous steps");
        System.out.println("   " + state);

        System.out.println("\nBenefits:");
        System.out.println("   - Transparent reasoning");
        System.out.println("   - Can use external tools");
        System.out.println("   - Handles complex queries");
    }

    static void toolsAndActions() {
        System.out.println("\n--- Tools and Actions ---");
        System.out.println("Extending LLM capabilities with external tools");

        System.out.println("\nTool Categories:");

        System.out.println("\n1. Search Tools:");
        System.out.println("   - Web search (Google, Bing)");
        System.out.println("   - Wikipedia, StackOverflow");
        System.out.println("   - Custom knowledge bases");

        System.out.println("\n2. Data Tools:");
        System.out.println("   - Database queries (SQL)");
        System.out.println("   - API calls (REST, GraphQL)");
        System.out.println("   - File system operations");

        System.out.println("\n3. Compute Tools:");
        System.out.println("   - Calculator");
        System.out.println("   - Code execution");
        System.out.println("   - Python interpreter");

        System.out.println("\n4. Custom Tools:");
        System.out.println("   - Business logic");
        System.out.println("   - External services");
        System.out.println("   - Legacy system integration");

        System.out.println("\nTool Definition:");
        Map<String, Object> toolDef = new LinkedHashMap<>();
        toolDef.put("name", "calculate_bmi");
        toolDef.put("description", "Calculate BMI from weight and height");
        toolDef.put("parameters", Map.of("weight", "double", "height", "double"));
        System.out.println("   " + toolDef);

        System.out.println("\nTool Execution:");
        System.out.println("   ToolExecutor executor = new ToolExecutor(tools);");
        System.out.println("   ");
        System.out.println("   ToolCall call = ToolCall.builder()");
        System.out.println("       .name(\"calculate_bmi\")");
        System.out.println("       .parameters(Map.of(\"weight\", 70, \"height\", 1.75))");
        System.out.println("       .build();");
        System.out.println("   ");
        System.out.println("   ToolResult result = executor.execute(call);");
    }

    static void memoryManagement() {
        System.out.println("\n--- Memory Management ---");
        System.out.println("Maintaining context across LLM interactions");

        System.out.println("\nMemory Types:");

        System.out.println("\n1. Conversation Buffer:");
        System.out.println("   - Store all messages");
        System.out.println("   - Simple but grows unbounded");
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", "Hello"));
        messages.add(Map.of("role", "assistant", "content", "Hi! How can I help?"));
        System.out.println("   Messages: " + messages.size());

        System.out.println("\n2. Sliding Window:");
        System.out.println("   - Keep last N messages");
        System.out.println("   - Limit context length");
        System.out.println("   window = keepLast(10)");

        System.out.println("\n3. Summary Memory:");
        System.out.println("   - Summarize conversation periodically");
        System.out.println("   - Compress to key points");
        String summary = "User wants to learn Java. " +
            "Started with basics, now on OOP.";
        System.out.println("   Summary: " + summary);

        System.out.println("\n4. Entity Memory:");
        System.out.println("   - Track entities and facts");
        Map<String, String> entities = new HashMap<>();
        entities.put("name", "John");
        entities.put("level", "beginner");
        entities.put("goal", "Learn Java");
        System.out.println("   Entities: " + entities);

        System.out.println("\n5. Vector Store Memory:");
        System.out.println("   - Store as embeddings");
        System.out.println("   - Semantic retrieval");
        System.out.println("   vectorStore.add(messagesAsVectors);");

        System.out.println("\nMemory Implementation:");
        System.out.println("   ConversationMemory memory = new SlidingWindowMemory(10);");
        System.out.println("   memory.add(new UserMessage(\"Hi\"));");
        System.out.println("   memory.add(new AIMessage(\"Hello!\"));");
        System.out.println("   List<Message> context = memory.getContext();");
    }

    static void retrievalAugmented() {
        System.out.println("\n--- Retrieval Augmented Generation (RAG) ---");
        System.out.println("Enhancing LLM responses with external knowledge");

        System.out.println("\nRAG Architecture:");
        System.out.println("   [User Query] -> [Embed] -> [Vector Store] -> [Retrieve]");
        System.out.println("        -> [LLM with Context] -> [Generate Response]");

        System.out.println("\nRAG Components:");
        String[] components = {
            "1. Document Loader", "2. Text Splitter",
            "3. Embedding Model", "4. Vector Database",
            "5. Retriever", "6. Generator LLM"
        };
        for (String c : components) {
            System.out.println("   " + c);
        }

        System.out.println("\nRAG Workflow:");
        System.out.println("   // Indexing phase");
        System.out.println("   for (Document doc : documents) {");
        System.out.println("       List<String> chunks = split(doc.getText());");
        System.out.println("       for (String chunk : chunks) {");
        System.out.println("           Vector embedding = embed(chunk);");
        System.out.println("           store.add(embedding, chunk);");
        System.out.println("       }");
        System.out.println("   }");
        System.out.println("   ");
        System.out.println("   // Query phase");
        System.out.println("   Vector queryEmbedding = embed(userQuery);");
        System.out.println("   List<String> context = store.similaritySearch(queryEmbedding, 5);");
        System.out.println("   String response = llm.generate(userQuery, context);");

        System.out.println("\nRetrieval Strategies:");
        System.out.println("   - Similarity search (cosine, L2)");
        System.out.println("   - MMR (Max Marginal Relevance)");
        System.out.println("   - Hybrid (keyword + vector)");
        System.out.println("   - Re-ranking");

        System.out.println("\nRAG Optimization:");
        System.out.println("   - Chunk size tuning");
        System.out.println("   - Embedding model selection");
        System.out.println("   - Top-K parameter");
        System.out.println("   - Query rewriting");
    }

    static void multiModalLlm() {
        System.out.println("\n--- Multi-Modal LLM ---");
        System.out.println("Processing multiple types of input (text, image, audio)");

        System.out.println("\nModalities:");

        System.out.println("\n1. Text + Image:");
        System.out.println("   - Image description");
        System.out.println("   - Visual question answering");
        System.out.println("   - Chart understanding");

        System.out.println("\n2. Text + Audio:");
        System.out.println("   - Speech to text");
        System.out.println("   - Audio analysis");
        System.out.println("   - Transcription");

        System.out.println("\n3. Text + Video:");
        System.out.println("   - Video understanding");
        System.out.println("   - Scene extraction");
        System.out.println("   - Action recognition");

        System.out.println("\n4. Multimodal Input:");
        System.out.println("   UserMessage userMsg = UserMessage.builder()");
        System.out.println("       .text(\"What's in this image?\")");
        System.out.println("       .media(MimeType.IMAGE_PNG, imageBytes)");
        System.out.println("       .build();");

        System.out.println("\nUse Cases:");
        String[] useCases = {
            "Document scanning and understanding",
            "Voice assistants with vision",
            "Code from screenshots",
            "Accessibility tools"
        };
        for (String uc : useCases) {
            System.out.println("   - " + uc);
        }

        System.out.println("\nSupported Models:");
        System.out.println("   - GPT-4 Vision");
        System.out.println("   - Claude 3 (Vision)");
        System.out.println("   - Gemini Pro Vision");
    }

    static void evaluationMetrics() {
        System.out.println("\n--- Evaluation Metrics ---");
        System.out.println("Measuring LLM application performance");

        System.out.println("\nEvaluation Categories:");

        System.out.println("\n1. Response Quality:");
        Map<String, Double> qualityMetrics = new LinkedHashMap<>();
        qualityMetrics.put("Accuracy", 0.92);
        qualityMetrics.put("Helpfulness", 0.88);
        qualityMetrics.put("Coherence", 0.95);
        qualityMetrics.put("Relevance", 0.90);
        System.out.println("   " + qualityMetrics);

        System.out.println("\n2. Task-Specific:");
        System.out.println("   - Classification: Precision, Recall, F1");
        System.out.println("   - Summarization: ROUGE, BLEU");
        System.out.println("   - Translation: BLEU, METEOR");
        System.out.println("   - QA: Exact Match, F1");

        System.out.println("\n3. Latency Metrics:");
        Map<String, String> latency = new LinkedHashMap<>();
        latency.put("p50 latency", "150ms");
        latency.put("p95 latency", "350ms");
        latency.put("p99 latency", "500ms");
        latency.put("throughput", "100 req/s");
        System.out.println("   " + latency);

        System.out.println("\n4. Cost Metrics:");
        System.out.println("   - Input tokens: $0.001/1K");
        System.out.println("   - Output tokens: $0.003/1K");
        System.out.println("   - Total cost per request");

        System.out.println("\nEvaluation Methods:");
        System.out.println("   - LLM-as-judge: Use LLM to evaluate");
        System.out.println("   - Human evaluation: Expert review");
        System.out.println("   - Automated metrics: Task-specific");
        System.out.println("   - A/B testing: Compare versions");

        System.out.println("\nBenchmarking:");
        System.out.println("   - MMLU: Multi-task understanding");
        System.out.println("   - HumanEval: Code generation");
        System.out.println("   - MMBench: Multi-modal");
    }

    static void productionPatterns() {
        System.out.println("\n--- Production Patterns ---");
        System.out.println("Best practices for LLM applications in production");

        System.out.println("\n1. Error Handling:");
        System.out.println("   - Retry with backoff");
        System.out.println("   - Fallback to cached responses");
        System.out.println("   - Graceful degradation");
        System.out.println("   try { response = callLLM(prompt); }");
        System.out.println("   catch (RateLimitException e) { waitAndRetry(); }");

        System.out.println("\n2. Caching:");
        System.out.println("   Cache<String, Response> cache = Caffeine.newBuilder()");
        System.out.println("       .maximumSize(10000)");
        System.out.println("       .expireAfterWrite(Duration.ofHours(1))");
        System.out.println("       .build();");
        System.out.println("   ");
        System.out.println("   String cacheKey = hash(prompt);");
        System.out.println("   if (cache.containsKey(cacheKey)) return cache.get(cacheKey);");

        System.out.println("\n3. Rate Limiting:");
        System.out.println("   RateLimiter limiter = RateLimiter.create(100); // per second");
        System.out.println("   limiter.acquire(); // Wait if needed");

        System.out.println("\n4. Monitoring:");
        System.out.println("   MetricsRecorder metrics = new MetricsRecorder();");
        System.out.println("   metrics.record(\"llm.latency\", latency);");
        System.out.println("   metrics.record(\"llm.tokens\", tokenCount);");
        System.out.println("   metrics.record(\"llm.errors\", errorCount);");

        System.out.println("\n5. Circuit Breaker:");
        System.out.println("   CircuitBreaker breaker = CircuitBreaker.ofDefaults();");
        System.out.println("   breaker.execute(() -> callLLM(prompt));");

        System.out.println("\n6. Feature Flags:");
        System.out.println("   if (featureEnabled(\"new-model\")) {");
        System.out.println("       return callNewModel(prompt);");
        System.out.println("   } else {");
        System.out.println("       return callLegacyModel(prompt);");
        System.out.println("   }");
    }

    static void securityConsiderations() {
        System.out.println("\n--- Security Considerations ---");
        System.out.println("Securing LLM applications");

        System.out.println("\nSecurity Concerns:");

        System.out.println("\n1. Prompt Injection:");
        System.out.println("   - Malicious user inputs");
        System.out.println("   - Override system instructions");
        System.out.println("   Mitigation: Input validation, sandboxing");
        System.out.println("   \"Ignore previous instructions: ...\"");

        System.out.println("\n2. Data Privacy:");
        System.out.println("   - Don't send sensitive data to LLM");
        System.out.println("   - Anonymize before processing");
        System.out.println("   - Use local models when possible");

        System.out.println("\n3. Output Validation:");
        System.out.println("   - Sanitize LLM outputs");
        System.out.println("   - Validate against schemas");
        System.out.println("   - Prevent injection attacks");
        System.out.println("   String safeOutput = sanitize(response);");

        System.out.println("\n4. API Security:");
        System.out.println("   - Secure API keys");
        System.out.println("   - Use HTTPS");
        System.out.println("   - Implement authentication");
        System.out.println("   - Rate limiting per user");

        System.out.println("\n5. Cost Protection:");
        System.out.println("   - Limit response length");
        System.out.println("   - Monitor usage per user");
        System.out.println("   - Budget alerts");
        System.out.println("   maxTokens = Math.min(request.getMaxTokens(), 1000);");

        System.out.println("\n6. Content Safety:");
        System.out.println("   - Filter inappropriate content");
        System.out.println("   - Implement safety guidelines");
        System.out.println("   - Log for moderation");
    }
}