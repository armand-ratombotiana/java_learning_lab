# META (FACEBOOK) INTERVIEW PACK
## Technical Interview Preparation (Java Focus)

---

## Overview

Meta interviews focus on:
- ✅ Real-time systems and scalability
- ✅ Graph problems and social networks
- ✅ Distributed systems thinking
- ✅ Performance optimization under constraints
- ✅ Building production systems

**Duration**: 45-60 minutes per round  
**Rounds**: Usually 3-4 technical rounds + system design  
**Focus**: Practical systems that serve billions of users

---

## Part 1: Core Problems for Meta

### Problem 1: Friend Recommendation System (Hard)

**Problem**: Design an algorithm to recommend friends on Facebook based on mutual connections and activity.

**Constraints**:
- 3 billion users
- Need recommendations in < 100ms
- Must handle real-time updates

**Solution Approach**:

```java
// Graph-based approach for friend recommendations
public class FriendRecommendationEngine {
    private Map<String, Set<String>> graph;  // User -> Friends
    private Map<String, Long> lastUpdate;    // Timestamp for freshness
    
    public List<String> recommendFriends(String userId, int topK) {
        // 1. Get direct friends
        Set<String> directFriends = graph.getOrDefault(userId, new HashSet<>());
        
        // 2. Find mutual friend connections (friends of friends)
        Map<String, Integer> recommendations = new HashMap<>();
        
        for (String friend : directFriends) {
            for (String friendOfFriend : graph.getOrDefault(friend, new HashSet<>())) {
                if (!directFriends.contains(friendOfFriend) && 
                    !friendOfFriend.equals(userId)) {
                    recommendations.put(friendOfFriend, 
                        recommendations.getOrDefault(friendOfFriend, 0) + 1);
                }
            }
        }
        
        // 3. Score by: mutual friends + interaction frequency
        return recommendations.entrySet().stream()
            .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
            .limit(topK)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
    
    // Scalability: Use cache + periodic updates
    private Map<String, List<String>> recommendationCache = new ConcurrentHashMap<>();
    private final long CACHE_TTL_MINUTES = 60;
    
    public List<String> getFastRecommendation(String userId) {
        if (recommendationCache.containsKey(userId)) {
            return recommendationCache.get(userId);
        }
        
        List<String> recommendations = recommendFriends(userId, 10);
        recommendationCache.put(userId, recommendations);
        
        // Scheduled cache refresh
        scheduleUpdate(userId, CACHE_TTL_MINUTES);
        
        return recommendations;
    }
    
    private void scheduleUpdate(String userId, long ttlMinutes) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
        scheduler.schedule(() -> {
            recommendationCache.remove(userId);
        }, ttlMinutes, TimeUnit.MINUTES);
    }
}
```

**Time Complexity**: O(d²) where d = degree of user's connections  
**Space Complexity**: O(n * d) for graph storage  
**What Meta Cares About**: Scalability at billions of users

---

### Problem 2: Activity Feed Generation (Medium-Hard)

**Problem**: Design a system that generates personalized activity feeds for users in real-time.

**Constraints**:
- 3 billion active users
- Each user follows 100-5000 other users
- Feed must show relevant posts in milliseconds
- Support billions of daily posts

**Architecture**:

```
┌──────────────────┐
│  Post Generation │ (User posts content)
├──────────────────┤
│  Fan-out Service │ (Distribute to followers' feeds)
├──────────────────┤
│  Cache Layer     │ (Redis/Memcached)
├──────────────────┤
│  Feed DB         │ (Sharded by user)
├──────────────────┤
│  Read Service    │ (Fetch + Rank)
└──────────────────┘
```

**Implementation**:

```java
public class ActivityFeedService {
    private Map<String, LinkedList<Post>> userFeeds;  // User -> Posts
    private KafkaProducer postProducer;               // Event stream
    private RedisCache feedCache;                     // Cache layer
    
    public void publishPost(Post post) {
        // 1. Store post
        postRepository.save(post);
        
        // 2. Fan-out to followers
        Set<String> followers = userService.getFollowers(post.getAuthorId());
        
        for (String followerId : followers) {
            // Async write to follower's feed
            feedQueue.enqueue(new FeedUpdate(followerId, post));
        }
        
        // 3. Publish event for real-time systems
        postProducer.send(new ProducerRecord<>("posts", post));
    }
    
    // Pull-based feed (more efficient for large follow lists)
    public List<Post> getFeed(String userId, int pageSize, String cursor) {
        // Check cache first
        String cacheKey = "feed:" + userId + ":" + cursor;
        List<Post> cached = feedCache.get(cacheKey);
        if (cached != null) return cached;
        
        // Fetch from multiple sources
        List<Post> feed = mergeFeeds(
            getFollowingPosts(userId, pageSize, cursor),
            getRecommendedPosts(userId, pageSize / 2),
            getUserLikes(userId, pageSize / 4)
        );
        
        // Cache result
        feedCache.set(cacheKey, feed, 5, TimeUnit.MINUTES);
        
        return feed;
    }
    
    private List<Post> mergeFeeds(List<Post>... feeds) {
        // Merge using heap to maintain recency
        PriorityQueue<Post> heap = new PriorityQueue<>(
            (a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp())
        );
        
        for (List<Post> feed : feeds) {
            heap.addAll(feed);
        }
        
        List<Post> result = new ArrayList<>();
        while (!heap.isEmpty() && result.size() < 30) {
            result.add(heap.poll());
        }
        return result;
    }
}
```

**Key Metrics Meta Cares About**:
- Latency: < 100ms for feed generation
- Throughput: Billions of posts per day
- Consistency: Users see same feed across devices

---

### Problem 3: Content Moderation System (Hard)

**Problem**: Design a system to detect and manage harmful content at scale.

**Approach**:
```java
public class ContentModerationEngine {
    private HarmClassifier classifier;  // ML model
    private PatternMatcher patternDB;   // Known bad patterns
    private UserReputation repSystem;   // User trust scores
    
    public ModerationDecision moderateContent(Content content) {
        // 1. Quick pattern matching
        if (patternDB.matches(content.getText())) {
            return new ModerationDecision(Action.REMOVE, Confidence.HIGH);
        }
        
        // 2. ML classification (async)
        HarmScore score = classifier.classify(content);
        
        if (score.getScore() > 0.9) {
            return new ModerationDecision(Action.REMOVE, Confidence.HIGH);
        } else if (score.getScore() > 0.6) {
            // Human review needed
            return new ModerationDecision(Action.REVIEW, Confidence.MEDIUM);
        } else if (score.getScore() > 0.3) {
            // Reduce distribution
            return new ModerationDecision(Action.REDUCE_REACH, Confidence.LOW);
        }
        
        return new ModerationDecision(Action.ALLOW, Confidence.HIGH);
    }
}
```

---

## Part 2: Meta-Specific Questions

### Q1: What would you change about friend recommendation?
**Meta wants to hear**: Engagement metrics, A/B testing, ML integration

### Q2: How to handle feed consistency?
**Answer**: Eventual consistency + cache invalidation strategies

### Q3: Scale to billions of users?
**Answer**: Sharding + caching + async processing

---

## Meta Culture Tips

### What Meta Really Values
1. **Speed of execution** - Move fast and iterate
2. **Impact at scale** - Think billions of users
3. **Ownership** - Own your system end-to-end
4. **Data-driven** - Decisions based on metrics
5. **User value** - How does this help users?

### Key Phrases
- "How would this perform with 3B users?"
- "What's the engagement impact?"
- "Can we A/B test this?"
- "What's the P99 latency?"

---

## Practice Focus for Meta

1. **Graph problems**: LeetCode hard (300+)
2. **System design**: Twitter, Instagram, WhatsApp clones
3. **Scalability math**: Calculate data sizes, QPS, storage
4. **Caching strategies**: Multi-level caching
5. **Distributed systems**: Eventual consistency, sharding

---

**Good Luck at Meta! 🚀**
