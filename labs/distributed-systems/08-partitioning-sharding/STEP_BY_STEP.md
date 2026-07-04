# Partitioning: Step by Step

## Implementing Range Sharding

### Step 1: Determine shard key
```java
// User ID as shard key ensures all user data on same shard
String shardKey = "user:" + userId;
```

### Step 2: Create shard mapping
```java
Map<String, Integer> shardMap = new HashMap<>();
shardMap.put("shard1", 0); // keys A-M
shardMap.put("shard2", 1); // keys N-Z
```

### Step 3: Route query
```java
int getShardId(String key) {
    char firstChar = Character.toUpperCase(key.charAt(0));
    return (firstChar <= 'M') ? 0 : 1;
}
```

### Step 4: Execute on correct shard
```java
void put(String key, Object value) {
    int shardId = getShardId(key);
    shards[shardId].put(key, value);
}
```
