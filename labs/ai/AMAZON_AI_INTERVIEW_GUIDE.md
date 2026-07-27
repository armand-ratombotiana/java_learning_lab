# Amazon AI Interview Guide

Interview preparation for ML/AI roles at Amazon (including Alexa, AWS AI, AGI, and science teams).

---

## 1. Role Types at Amazon AI

### Applied Scientist
- PhD required or equivalent
- ML models for products: Alexa, Search, Recommendations, Prime Air
- Strong research background
- Publication record expected
- Level: L4 (entry) through L8 (Distinguished)

### ML Engineer (SageMaker/AWS AI)
- Builds ML platforms, tools, and infrastructure
- SageMaker, Bedrock, Rekognition, Comprehend, Personalize
- Strong software engineering background
- Level: L4-L7

### Data Scientist (ML)
- Applied ML on business problems
- Forecasting, personalization, supply chain optimization
- Less focus on publications, more on application
- Level: L4-L6

### Research Scientist (AGI/AWS AI Labs)
- Fundamental research
- Amazon AGI, Alexa Prize, AWS AI Labs
- Publication at top venues required

---

## 2. Interview Process

### Process Timeline

| Step | Duration | Format |
|------|----------|--------|
| Recruiter Screen | 30 min | Background, Leadership Principles |
| Online Assessment | 90 min | Coding + work style (optional) |
| Technical Phone Screen | 60 min | ML fundamentals + coding |
| Virtual On-site | 4-5 hours | 4-5 rounds |
| Debrief | - | Hiring committee |
| Bar Raiser Decision | - | Final approval |
| Offer | - | Negotiation |

### Round Breakdown

| Round | Duration | Focus |
|-------|----------|-------|
| ML Deep Dive | 60 min | In-depth ML fundamentals |
| Coding | 45 min | Algorithms + data structures |
| ML System Design | 45 min | ML pipeline + architecture |
| System Design (senior) | 45 min | General distributed systems |
| Bar Raiser | 60 min | Leadership Principles |
| Research Presentation | 45 min | Research roles only |

---

## 3. ML Deep Dive Round

### What They Assess

- **Depth**: How deep is your ML knowledge? Can you derive equations?
- **Experience**: Have you actually built and deployed ML systems?
- **Decision-making**: Why did you choose specific techniques?
- **Problem-solving**: How do you approach novel ML problems?

### Topics Covered

**Modeling**:
- Model selection criteria (linear vs tree vs neural net)
- Loss function design
- Regularization strategy
- Handling imbalanced data
- Feature engineering decisions

**Training**:
- Optimization algorithm selection
- Learning rate scheduling
- Batch size selection
- Early stopping strategy

**Evaluation**:
- Cross-validation strategy
- Metric selection
- Statistical significance
- A/B testing methodology

**Production**:
- Training-serving skew
- Model monitoring
- Retraining strategy
- Inference optimization

### Sample Deep Dive Questions

```
1. Derive the gradient update for logistic regression with L2 regularization
2. Explain the bias-variance trade-off for random forest vs gradient boosting
3. How would you handle a dataset with 1000 features and only 1000 samples?
4. Compare cross-entropy loss vs. hinge loss for classification
5. How does batch normalization work during training vs. inference?
6. Explain the math behind the Adam optimizer
7. How do you determine if a model improvement is statistically significant?
8. Describe the cold start problem and how you've solved it
```

---

## 4. ML System Design Round

### Common Systems

| System | Key Components | Amazon Context |
|--------|---------------|----------------|
| Product Recommendations | Item-to-item CF, deep learning retrieval, ranking | Amazon.com |
| Alexa Conversation | ASR, NLU, dialogue management, TTS | Alexa |
| Demand Forecasting | Time series, hierarchical forecasting | Supply chain |
| Fraud Detection | Real-time scoring, graph features, ensemble | Payments |
| Search Ranking | Query understanding, retrieval, ranking | Product search |
| Personalization | Session-based, real-time personalization | Homepage |
| Supply Chain Optimization | Inventory, routing, demand prediction | Operations |

### Amazon-Specific Design Factors

**Scale**: Handle Amazon's scale (hundreds of millions of customers, billions of items)
**Latency**: Sub-100ms for real-time (search, recommendations, Alexa)
**Cost**: Optimize for cost-effectiveness (AWS infrastructure)
**Resilience**: Highly available, fault-tolerant ML systems
**Data**: Massive data volumes, complex data pipelines

### Design Framework

**1. Problem Scope (5 min)**
- ML task definition
- Success metrics (business + ML)
- Constraints (latency, cost, scale)
- Data availability

**2. Data Strategy (10 min)**
- Data sources and volumes
- Feature engineering approach
- Data quality and validation
- Feature store architecture

**3. Model Design (15 min)**
- Model architecture
- Training methodology
- Offline evaluation
- Baselines and experiments

**4. Serving Architecture (10 min)**
- Batch vs real-time
- Model server infrastructure
- Scaling and caching
- Latency optimization

**5. Production Operations (5 min)**
- Model monitoring
- Retraining strategy
- A/B testing framework
- Rollback procedures

---

## 5. Coding Round

### Amazon Coding Patterns

| Pattern | Frequency | Example Problems |
|---------|-----------|-----------------|
| Arrays | Very High | Two Sum, Container Water, 3Sum |
| Strings | High | Longest Substring, Group Anagrams |
| Trees | High | Validate BST, Level Order, Max Path |
| DP | High | Coin Change, Longest Increasing, Edit Distance |
| Graphs | Medium | Number Islands, Course Schedule |
| Design | High | LRU Cache, Design Tic-Tac-Toe |
| Recursion | Medium | Permutations, Combinations |
| Binary Search | High | Search Rotated Array, Find Peak |

### Amazon-Specific Tips

- **Object-oriented design**: Amazon values clean, extensible code
- **Test your code**: Walk through examples to verify correctness
- **Consider scale**: How would your solution work with 1B elements?
- **Optimization**: Discuss time/space complexity trade-offs

```java
// Example: Amazon-favorite problem - LRU Cache
import java.util.*;

class LRUCache {
    private int capacity;
    private Map<Integer, Node> cache;
    private Node head, tail;

    class Node {
        int key, value;
        Node prev, next;
        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.head = new Node(0, 0);
        this.tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        if (!cache.containsKey(key)) return -1;
        Node node = cache.get(key);
        remove(node);
        addToFront(node);
        return node.value;
    }

    public void put(int key, int value) {
        if (cache.containsKey(key)) {
            remove(cache.get(key));
        } else if (cache.size() >= capacity) {
            cache.remove(tail.prev.key);
            remove(tail.prev);
        }
        Node node = new Node(key, value);
        cache.put(key, node);
        addToFront(node);
    }

    private void addToFront(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    private void remove(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
}
```

---

## 6. Bar Raiser Round

### Role of the Bar Raiser

The Bar Raiser is an interviewer who:
- Does not belong to the hiring team
- Ensures the bar is consistently high
- Evaluates Leadership Principles (LPs) across all candidates
- Has veto power over hiring decisions

### Amazon Leadership Principles for ML

| LP | ML Application | Question Pattern |
|----|---------------|------------------|
| Customer Obsession | "How did you ensure your model serves customer needs?" | Start with customer problem, not ML solution |
| Ownership | "How did you handle a model failure?" | Take responsibility, drive resolution |
| Dive Deep | "Tell me about a deep analysis you performed" | Show technical depth and curiosity |
| Learn and Be Curious | "How do you stay current with ML research?" | Continuous learning habits |
| Insist on Highest Standards | "What quality metrics did you enforce?" | Rigor in evaluation and monitoring |
| Think Big | "What would you build with unlimited resources?" | Vision for ML impact at scale |
| Bias for Action | "When did you ship vs wait for more data?" | Speed without sacrificing quality |
| Deliver Results | "What was the measurable impact?" | Quantified business outcomes |
| Have Backbone | "Tell me about a technical disagreement" | Stand by data-driven decisions |

### Bar Raiser Interview Structure

**60 minutes covering 3-4 Leadership Principles**:

| Time | Principle | Question Type |
|------|-----------|---------------|
| 0-15 min | Customer Obsession / Ownership | "Tell me about an ML project you delivered" |
| 15-30 min | Dive Deep / Highest Standards | "How did you ensure model quality?" |
| 30-45 min | Bias for Action / Deliver Results | "When did you make a fast decision?" |
| 45-60 min | Learn & Be Curious / Think Big | "What new techniques have you learned?" |

### Sample Bar Raiser Questions

```
Customer Obsession:
"Tell me about a time you had to redesign an ML system because it wasn't
meeting customer needs"

Dive Deep:
"Describe a situation where you discovered a subtle bug in your ML pipeline
through deep investigation"

Bias for Action:
"Tell me about a time you decided to launch an imperfect model over waiting"

Deliver Results:
"What was the most impactful ML project you worked on? How did you measure success?"

Ownership:
"Describe a model that failed in production and how you handled it"

Learn and Be Curious:
"Tell me about a new ML technique you learned recently and how you applied it"
```

---

## 7. Amazon ML Stack Knowledge

### SageMaker

- **Built-in algorithms**: Linear learner, XGBoost, K-Means, PCA, DeepAR
- **Features**: Pipelines, Debugger, Model Monitor, Clarify (bias)
- **Deployment**: Real-time endpoints, batch transforms, asynchronous inference
- **Training**: Distributed training, spot instance training

### Bedrock

- **Foundation models**: Access to Claude, Llama, Mistral, Titan
- **Agents**: RAG, function calling, knowledge bases
- **Guardrails**: Content filtering, topic bans, PII redaction

### Personalize

- Recipes: user-personalization, related-items, personalized-ranking
- Real-time recommendations API
- Automatic model updates

### Forecast

- Time series forecasting
- DeepAR+ (proprietary)
- What-if analysis

### Rekognition, Comprehend, Translate, Polly

- Pre-built AI services
- Customization options
- Pay-per-use pricing

---

## 8. Research Preparation

### Amazon AI Papers to Know

- "Deep Neural Networks for YouTube Recommendations" (2016) - applied at Amazon
- "Amazon Aurora: Design Considerations for High Throughput Cloud-Native Relational Databases" (2017)
- "Alexa Conversations: An Extensible Data-driven Approach" (2021)
- "The Natural Language Decathlon: Multitask Learning as Question Answering" (2019)
- Amazon's Demand Forecasting papers

### Research Presentation (for science roles)

**Structure**:
1. Problem and motivation (2 min)
2. Related work and positioning (3 min)
3. Method with key insights (10 min)
4. Experiments and results (10 min)
5. Limitations and future work (5 min)
6. Discussion (15 min)

**What they assess**:
- Depth of technical understanding
- Novelty and significance
- Experimental rigor
- Communication skills

---

## 9. Preparation Strategy

### Key Preparation Areas

1. **ML Fundamentals Mastery**: Be ready to derive key equations
2. **Leadership Principles**: Prepare 2-3 stories per principle
3. **System Design**: Focus on Amazon-scale systems
4. **Coding**: LeetCode medium, focus on arrays, trees, DP
5. **Domain Knowledge**: Understand Amazon AI services
6. **Research**: Recent papers (science roles)

### Sample 10-Week Timeline

**Weeks 1-3**: ML fundamentals + LeetCode basics
**Weeks 4-6**: System design + Leadership Principles stories
**Weeks 7-9**: Mock interviews + focused practice
**Week 10**: Review + rest

---

## 10. Key Resources

### Books
- "Working Backwards" (Bryar, Carr) - Amazon culture
- "Cracking the Coding Interview" (McDowell)
- "The Algorithm Design Manual" (Skiena)

### Amazon Resources
- Amazon Science (science.amazon.com)
- AWS Machine Learning documentation
- AWS re:Invent ML sessions (YouTube)
- Amazon Leadership Principles (internal docs)

### Online
- LeetCode (Amazon tagged questions)
- GeeksForGeeks (Amazon interview experiences)
- Glassdoor (recent interview questions)
