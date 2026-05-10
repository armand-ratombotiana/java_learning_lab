package com.learning.lab.module72;

import java.util.*;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Module 72: LangChain4j - LLM Integration & RAG");
        System.out.println("=".repeat(60));

        langchain4jOverview();
        chatModels();
        promptTemplates();
        chains();
        memory();
        ragArchitecture();
        documentLoaders();
        textSplitting();
        embeddings();
        vectorStores();
        retrieval();
        agents();
        tools();
        outputParsers();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("Module 72 Lab Complete!");
        System.out.println("=".repeat(60));
    }

    static void langchain4jOverview() {
        System.out.println("\n--- LangChain4j Overview ---");
        System.out.println("LangChain4j is a Java port of LangChain, providing:");
        System.out.println("   - LLM integration (OpenAI, Claude, local models)");
        System.out.println("   - Retrieval-Augmented Generation (RAG)");
        System.out.println("   - Agent frameworks");
        System.out.println("   - Memory management");

        System.out.println("\nCore Components:");
        System.out.println("   1. Models: LLMs, Chat Models, Embeddings");
        System.out.println("   2. Prompts: Templates, Few-shot examples");
        System.out.println("   3. Chains: Sequential processing");
        System.out.println("   4. Memory: Conversation history");
        System.out.println("   5. Tools: External integrations");
        System.out.println("   6. Agents: Autonomous decision-making");

        System.out.println("\nSupported LLMs:");
        System.out.println("   - OpenAI (GPT-4, GPT-3.5)");
        System.out.println("   - Anthropic (Claude)");
        System.out.println("   - Ollama (local models)");
        System.out.println("   - HuggingFace");
        System.out.println("   - Google Gemini");
    }

    static void chatModels() {
        System.out.println("\n--- Chat Models ---");
        System.out.println("Chat models process conversations and return responses");

        System.out.println("\nChat Message Types:");
        System.out.println("   - SystemMessage: Instructions for the AI");
        System.out.println("   - UserMessage: User input");
        System.out.println("   - AiMessage: AI response");
        System.out.println("   - ToolMessage: Tool execution results");

        System.out.println("\nMessage Examples:");
        String systemMessage = "You are a helpful Java programming assistant.";
        String userMessage = "How do I create a thread in Java?";
        System.out.println("   System: " + systemMessage);
        System.out.println("   User: " + userMessage);

        System.out.println("\nChat Options:");
        System.out.println("   - Temperature: Controls randomness (0-2)");
        System.out.println("   - Max Tokens: Limits response length");
        System.out.println("   - Top P: Nucleus sampling");
        System.out.println("   - Response Format: JSON, text");

        Map<String, Object> options = new HashMap<>();
        options.put("temperature", 0.7);
        options.put("maxTokens", 500);
        options.put("topP", 0.9);
        System.out.println("   Options: " + options);

        System.out.println("\nStreaming Responses:");
        System.out.println("   - Real-time token-by-token output");
        System.out.println("   - Improved user experience");
        System.out.println("   - Requires AsyncIterator pattern");

        System.out.println("\nModel Selection Criteria:");
        System.out.println("   - Task requirements");
        System.out.println("   - Latency needs");
        System.out.println("   - Cost considerations");
        System.out.println("   - Data privacy requirements");
    }

    static void promptTemplates() {
        System.out.println("\n--- Prompt Templates ---");
        System.out.println("Reusable prompt patterns with variable substitution");

        System.out.println("\nTemplate Types:");
        System.out.println("   1. ChatPromptTemplate:");
        System.out.println("      - Multiple message roles");
        System.out.println("      - Example: System + User messages");

        String systemTemplate = "You are a {role} specializing in {specialty}.";
        String userTemplate = "Explain {topic} to a {audience}.";
        System.out.println("   System Template: " + systemTemplate);
        System.out.println("   User Template: " + userTemplate);

        System.out.println("\n   2. PromptTemplate:");
        System.out.println("      - Single string prompt");
        System.out.println("      - Variables: {variable_name}");

        String prompt = "What are the key benefits of {technology} for {useCase}?";
        System.out.println("   Template: " + prompt);

        System.out.println("\nTemplate Variables:");
        Map<String, String> variables = new LinkedHashMap<>();
        variables.put("role", "software architect");
        variables.put("specialty", "cloud computing");
        variables.put("topic", "microservices");
        variables.put("audience", "beginner developer");
        System.out.println("   Variables: " + variables);

        System.out.println("\nFew-Shot Examples:");
        String[][] examples = {
            {"Input: 2+2", "Output: 4"},
            {"Input: 5*3", "Output: 15"},
            {"Input: 10-4", "Output: 6"}
        };
        System.out.println("   Examples for math assistant:");
        for (String[] ex : examples) {
            System.out.println("     " + ex[0] + " -> " + ex[1]);
        }

        System.out.println("\nPrompt Engineering Tips:");
        System.out.println("   - Be specific and clear");
        System.out.println("   - Use examples for complex tasks");
        System.out.println("   - Structure complex requests");
        System.out.println("   - Include constraints when needed");
    }

    static void chains() {
        System.out.println("\n--- Chains ---");
        System.out.println("Chains combine multiple components for sequential processing");

        System.out.println("\nChain Types:");

        System.out.println("\n1. LLMChain:");
        System.out.println("   - Basic: Prompt + Model + Output Parser");
        System.out.println("   - Most common building block");
        String[] llmChainComponents = {"PromptTemplate", "ChatModel", "OutputParser"};
        System.out.println("   Components: " + Arrays.toString(llmChainComponents));

        System.out.println("\n2. SequentialChain:");
        System.out.println("   - Output of one chain becomes input of next");
        System.out.println("   - Simple: A -> B -> C");
        System.out.println("   - Transform: Input -> Transform -> Output");
        String[] sequentialSteps = {"Preprocess", "Generate", "Post-process"};
        System.out.println("   Steps: " + Arrays.toString(sequentialSteps));

        System.out.println("\n3. RouterChain:");
        System.out.println("   - Dynamically selects next chain");
        System.out.println("   - Based on input content");
        System.out.println("   - Used for intent routing");

        System.out.println("\n4. TransformationChain:");
        System.out.println("   - Applies transformations to input");
        System.out.println("   - String manipulation, parsing");

        System.out.println("\nExample Chain Flow:");
        Map<String, String> chainFlow = new LinkedHashMap<>();
        chainFlow.put("Input", "User question about Java");
        chainFlow.put("Step 1", "Classify intent");
        chainFlow.put("Step 2", "Retrieve relevant docs");
        chainFlow.put("Step 3", "Generate answer");
        chainFlow.put("Step 4", "Format output");
        System.out.println("   Flow: " + chainFlow);
    }

    static void memory() {
        System.out.println("\n--- Memory ---");
        System.out.println("Maintains conversation context across interactions");

        System.out.println("\nMemory Types:");

        System.out.println("\n1. BufferMemory:");
        System.out.println("   - Stores all messages");
        System.out.println("   - Simple but unbounded");
        List<Map<String, String>> buffer = new ArrayList<>();
        buffer.add(Map.of("role", "user", "content", "What is OOP?"));
        buffer.add(Map.of("role", "ai", "content", "Object-Oriented Programming..."));
        buffer.add(Map.of("role", "user", "content", "Tell me more about classes"));
        System.out.println("   Messages: " + buffer.size());

        System.out.println("\n2. SummaryMemory:");
        System.out.println("   - Summarizes conversation");
        System.out.println("   - Limits context length");
        System.out.println("   - Uses LLM for summarization");

        System.out.println("\n3. EntityMemory:");
        System.out.println("   - Tracks specific entities");
        System.out.println("   - Extracts and remembers facts");
        Map<String, String> entities = new HashMap<>();
        entities.put("person", "John");
        entities.put("project", "Java API");
        entities.put("deadline", "Next week");
        System.out.println("   Entities: " + entities);

        System.out.println("\n4. KnowledgeGraphMemory:");
        System.out.println("   - Stores relationships");
        System.out.println("   - Graph-based structure");

        System.out.println("\nConversation Window:");
        System.out.println("   - Limit recent messages");
        System.out.println("   - Token-based truncation");
        System.out.println("   - Sliding window approach");
    }

    static void ragArchitecture() {
        System.out.println("\n--- RAG Architecture ---");
        System.out.println("Retrieval-Augmented Generation combines search with LLM generation");

        System.out.println("\nRAG Workflow:");
        System.out.println("   1. Ingest: Load and process documents");
        System.out.println("   2. Index: Create embeddings and store in vector DB");
        System.out.println("   3. Query: Convert user question to embedding");
        System.out.println("   4. Retrieve: Find similar documents");
        System.out.println("   5. Generate: LLM generates answer with context");

        System.out.println("\nRAG Components:");
        String[] ragComponents = {
            "Document Loader", "Text Splitter", "Embedding Model",
            "Vector Store", "Retriever", "Generator LLM"
        };
        for (String comp : ragComponents) {
            System.out.println("   - " + comp);
        }

        System.out.println("\nRAG Benefits:");
        System.out.println("   - Current information access");
        System.out.println("   - Reduced hallucinations");
        System.out.println("   - Source attribution");
        System.out.println("   - Cost-effective (no retraining)");

        System.out.println("\nRAG Variants:");
        System.out.println("   - Naive RAG: Simple retrieval -> generate");
        System.out.println("   - Corrective RAG: Self-correction loop");
        System.out.println("   - HyDE: Generate hypothetical answer");
        System.out.println("   - Agentic RAG: Multi-step reasoning");
    }

    static void documentLoaders() {
        System.out.println("\n--- Document Loaders ---");
        System.out.println("Load documents from various sources and formats");

        System.out.println("\nSupported Formats:");
        System.out.println("   - Text: .txt, .md");
        System.out.println("   - PDF: .pdf");
        System.out.println("   - Word: .docx");
        System.out.println("   - HTML: .html, .htm");
        System.out.println("   - CSV/JSON: Structured data");
        System.out.println("   - Database: SQL, NoSQL");

        System.out.println("\nLoader Types:");
        System.out.println("   1. FileSystemLoader: Local files");
        System.out.println("   2. URLLoader: Web pages");
        System.out.println("   3. S3Loader: AWS S3 files");
        System.out.println("   4. AzureBlobLoader: Azure storage");
        System.out.println("   5. DatabaseLoader: SQL queries");

        System.out.println("\nExample File Loading:");
        Map<String, Object> loadConfig = new HashMap<>();
        loadConfig.put("path", "/documents/java-guide.md");
        loadConfig.put("encoding", "UTF-8");
        loadConfig.put("metadata", Map.of("source", "docs", "type", "tutorial"));
        System.out.println("   Config: " + loadConfig);

        System.out.println("\nDocument Metadata:");
        System.out.println("   - source: Origin of document");
        System.out.println("   - title: Document title");
        System.out.println("   - author: Document author");
        System.out.println("   - created: Creation date");
        System.out.println("   - custom: User-defined fields");

        System.out.println("\nError Handling:");
        System.out.println("   - Skip malformed documents");
        System.out.println("   - Log skipped files");
        System.out.println("   - Retry with timeout");
    }

    static void textSplitting() {
        System.out.println("\n--- Text Splitting ---");
        System.out.println("Break large documents into smaller, manageable chunks");

        System.out.println("\nSplitting Strategies:");

        System.out.println("\n1. Character Splitting:");
        System.out.println("   - Fixed number of characters");
        System.out.println("   - Simple but may break words");
        String text = "This is a sample text that needs to be split into smaller chunks.";
        String[] charChunks = splitByChars(text, 20);
        System.out.println("   Text length: " + text.length());
        System.out.println("   Chunks: " + charChunks.length);

        System.out.println("\n2. Recursive Character Splitting:");
        System.out.println("   - Tries multiple separators");
        System.out.println("   - Respects paragraph boundaries");
        System.out.println("   - Recommended default");

        System.out.println("\n3. Document-specific Splitting:");
        System.out.println("   - Markdown: Headers, lists");
        System.out.println("   - Code: Functions, classes");
        System.out.println("   - JSON: Objects");

        System.out.println("\nChunk Parameters:");
        Map<String, Object> chunkConfig = new HashMap<>();
        chunkConfig.put("chunkSize", 500);
        chunkConfig.put("chunkOverlap", 50);
        chunkConfig.put("separators", new String[]{"\n\n", "\n", ". "});
        System.out.println("   Config: " + chunkConfig);

        System.out.println("\nSplitting Code Example:");
        String codeSnippet = "public class Main { public static void main(String[] args) { System.out.println(\"Hello\"); } }";
        String[] codeChunks = splitCode(codeSnippet);
        System.out.println("   Original: " + codeSnippet.substring(0, 30) + "...");
        System.out.println("   Chunks: " + Arrays.toString(codeChunks));
    }

    static String[] splitByChars(String text, int size) {
        int chunks = (int) Math.ceil((double) text.length() / size);
        String[] result = new String[chunks];
        for (int i = 0; i < chunks; i++) {
            int start = i * size;
            int end = Math.min(start + size, text.length());
            result[i] = text.substring(start, end);
        }
        return result;
    }

    static String[] splitCode(String code) {
        return code.split("(?<=[{};])\\s*");
    }

    static void embeddings() {
        System.out.println("\n--- Embeddings ---");
        System.out.println("Convert text to dense vector representations");

        System.out.println("\nEmbedding Concepts:");
        System.out.println("   - Captures semantic meaning");
        System.out.println("   - Similar texts have similar vectors");
        System.out.println("   - Typically 384-4096 dimensions");

        System.out.println("\nEmbedding Models:");
        System.out.println("   - OpenAI: text-embedding-ada-002");
        System.out.println("   - HuggingFace: sentence-transformers");
        System.out.println("   - Ollama: Local models");
        System.out.println("   - PaLM: Google embeddings");

        System.out.println("\nEmbedding Types:");
        System.out.println("   - Word embeddings: Individual words");
        System.out.println("   - Sentence embeddings: Full sentences");
        System.out.println("   - Document embeddings: Paragraphs/docs");

        System.out.println("\nSimilarity Metrics:");
        double[] v1 = {0.1, 0.3, 0.5, 0.7};
        double[] v2 = {0.2, 0.3, 0.4, 0.6};

        double cosineSim = cosineSimilarity(v1, v2);
        double dotProduct = dotProduct(v1, v2);
        double euclidean = euclideanDistance(v1, v2);

        System.out.printf("   V1: %s%n", Arrays.toString(v1));
        System.out.printf("   V2: %s%n", Arrays.toString(v2));
        System.out.printf("   Cosine Similarity: %.4f%n", cosineSim);
        System.out.printf("   Dot Product: %.4f%n", dotProduct);
        System.out.printf("   Euclidean Distance: %.4f%n", euclidean);

        System.out.println("\nEmbedding Use Cases:");
        System.out.println("   - Semantic search");
        System.out.println("   - Clustering");
        System.out.println("   - Recommendations");
        System.out.println("   - Anomaly detection");
    }

    static double cosineSimilarity(double[] a, double[] b) {
        double dot = dotProduct(a, b);
        double magA = Math.sqrt(Arrays.stream(a).map(x -> x * x).sum());
        double magB = Math.sqrt(Arrays.stream(b).map(x -> x * x).sum());
        return dot / (magA * magB);
    }

    static double dotProduct(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    static double euclideanDistance(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow(a[i] - b[i], 2);
        }
        return Math.sqrt(sum);
    }

    static void vectorStores() {
        System.out.println("\n--- Vector Stores ---");
        System.out.println("Specialized databases for storing and searching embeddings");

        System.out.println("\nPopular Vector Databases:");
        System.out.println("   - Pinecone: Managed vector search");
        System.out.println("   - Weaviate: Open-source, GraphQL");
        System.out.println("   - Milvus: Scalable, multi-format");
        System.out.println("   - Chroma: Lightweight, Python-first");
        System.out.println("   - Qdrant: Rust-based, fast");
        System.out.println("   - pgvector: PostgreSQL extension");

        System.out.println("\nIn-Memory Options:");
        System.out.println("   - InMemoryEmbeddingStore");
        System.out.println("   - For development/testing");
        System.out.println("   - No persistence");

        System.out.println("\nVector Store Operations:");
        System.out.println("   1. Add: Store embeddings + metadata");
        System.out.println("   2. Search: Find similar vectors");
        System.out.println("   3. Update: Modify existing entries");
        System.out.println("   4. Delete: Remove vectors");

        System.out.println("\nSearch Parameters:");
        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put("topK", 5);
        searchParams.put("scoreThreshold", 0.7);
        searchParams.put("filter", "category=='tutorial'");
        System.out.println("   Params: " + searchParams);

        System.out.println("\nIndexing Strategies:");
        System.out.println("   - Flat: Brute-force exact search");
        System.out.println("   - HNSW: Graph-based approximate");
        System.out.println("   - IVF: Clustering + search");
    }

    static void retrieval() {
        System.out.println("\n--- Retrieval Strategies ---");
        System.out.println("Methods to find relevant documents for queries");

        System.out.println("\n1. Similarity Search:");
        System.out.println("   - Find closest vectors");
        System.out.println("   - Cosine, L2, or dot product");
        System.out.println("   - Simple but effective");

        System.out.println("\n2. MMR (Max Marginal Relevance):");
        System.out.println("   - Balances relevance and diversity");
        System.out.println("   - Avoids duplicate results");
        double mmrScore = 0.65;
        System.out.printf("   MMR Score: %.2f%n", mmrScore);

        System.out.println("\n3. Hybrid Search:");
        System.out.println("   - Combines keyword and vector search");
        System.out.println("   - BM25 + Semantic search");
        System.out.println("   - Better for mixed queries");

        System.out.println("\n4. Parent Document Retrieval:");
        System.out.println("   - Retrieve at chunk level");
        System.out.println("   - Return full parent document");
        System.out.println("   - More context preservation");

        System.out.println("\n5. Compression:");
        System.out.println("   - Compress retrieved context");
        System.out.println("   - Select most relevant passages");
        System.out.println("   - Reduce token usage");

        System.out.println("\nQuery Processing:");
        System.out.println("   1. Convert query to embedding");
        System.out.println("   2. Search vector store");
        System.out.println("   3. Apply filters");
        System.out.println("   4. Re-rank results");
        System.out.println("   5. Return with metadata");
    }

    static void agents() {
        System.out.println("\n--- Agents ---");
        System.out.println("Autonomous systems that use LLMs to decide actions");

        System.out.println("\nAgent Architecture:");
        System.out.println("   LLM + Tools + Memory + Planning");

        System.out.println("\nAgent Types:");

        System.out.println("\n1. ReAct Agent:");
        System.out.println("   - Reasoning + Action loop");
        System.out.println("   - Think -> Act -> Observe");
        String[] reactSteps = {"Thought: Need to search", "Action: Search[query]", "Observation: Found results"};
        System.out.println("   Steps: " + Arrays.toString(reactSteps));

        System.out.println("\n2. Tool Agent:");
        System.out.println("   - Uses predefined tools");
        System.out.println("   - Structured tool selection");
        String[] availableTools = {"Search", "Calculator", "CodeExecutor", "WebScraper"};
        System.out.println("   Available: " + Arrays.toString(availableTools));

        System.out.println("\n3. Plan-and-Execute Agent:");
        System.out.println("   - Creates plan first");
        System.out.println("   - Executes step by step");
        System.out.println("   - More deliberate");

        System.out.println("\n4. Self-Ask Agent:");
        System.out.println("   - Breaks down complex questions");
        System.out.println("   - Asks follow-up questions");
        System.out.println("   - Intermediate reasoning");

        System.out.println("\nAgent Loop:");
        System.out.println("   while not done:");
        System.out.println("     1. Observe environment");
        System.out.println("     2. Think (LLM)");
        System.out.println("     3. Plan next action");
        System.out.println("     4. Execute action");
        System.out.println("     5. Update memory");
    }

    static void tools() {
        System.out.println("\n--- Tools ---");
        System.out.println("External capabilities agents can invoke");

        System.out.println("\nTool Types:");

        System.out.println("\n1. Search Tools:");
        System.out.println("   - Web search (Google, Bing)");
        System.out.println("   - Wikipedia search");
        System.out.println("   - Custom index search");

        System.out.println("\n2. Compute Tools:");
        System.out.println("   - Calculator");
        System.out.println("   - Python interpreter");
        System.out.println("   - Code executor");

        System.out.println("\n3. API Tools:");
        System.out.println("   - REST API calls");
        System.out.println("   - Database queries");
        System.out.println("   - External services");

        System.out.println("\n4. File Tools:");
        System.out.println("   - Read/Write files");
        System.out.println("   - List directory");
        System.out.println("   - Search files");

        System.out.println("\nTool Definition:");
        Map<String, Object> toolDef = new HashMap<>();
        toolDef.put("name", "search_java_docs");
        toolDef.put("description", "Search Java documentation for API details");
        toolDef.put("parameters", Map.of("query", "String", "version", "String"));
        System.out.println("   Definition: " + toolDef);

        System.out.println("\nTool Execution:");
        System.out.println("   1. Parse tool call from LLM output");
        System.out.println("   2. Validate parameters");
        System.out.println("   3. Execute tool");
        System.out.println("   4. Format results as ToolMessage");
        System.out.println("   5. Feed back to LLM");
    }

    static void outputParsers() {
        System.out.println("\n--- Output Parsers ---");
        System.out.println("Structure LLM responses into usable formats");

        System.out.println("\nParser Types:");

        System.out.println("\n1. Pydantic Parser:");
        System.out.println("   - Type-safe output validation");
        System.out.println("   - Schema-based validation");
        String pydanticSchema = "Person { name: String, age: int, email: String }";
        System.out.println("   Schema: " + pydanticSchema);

        System.out.println("\n2. JSON Parser:");
        System.out.println("   - Extract JSON from text");
        System.out.println("   - Handle markdown code blocks");
        String jsonOutput = "{\"intent\": \"search\", \"query\": \"Java tutorials\"}";
        System.out.println("   Example: " + jsonOutput);

        System.out.println("\n3. CSV Parser:");
        System.out.println("   - Tabular data extraction");
        String csvOutput = "name,age,city\nJohn,30,NYC\nJane,25,LA";
        System.out.println("   Example: " + csvOutput.replace("\n", " | "));

        System.out.println("\n4. Custom Parser:");
        System.out.println("   - Regex-based extraction");
        System.out.println("   - Custom validation logic");

        System.out.println("\nParser Configuration:");
        Map<String, Object> parserConfig = new HashMap<>();
        parserConfig.put("schema", "Person");
        parserConfig.put("strict", true);
        parserConfig.put("fallback", "ERROR");
        System.out.println("   Config: " + parserConfig);

        System.out.println("\nRetry Logic:");
        System.out.println("   - Retry on parse failure");
        System.out.println("   - Include format instructions");
        System.out.println("   - Max 3 attempts");
    }
}