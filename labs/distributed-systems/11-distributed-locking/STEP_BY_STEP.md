# Distributed Locking: Step by Step

## ZooKeeper Lock Implementation

### Step 1: Connect to ZooKeeper
```java
ZooKeeper zk = new ZooKeeper("localhost:2181", 3000, watcher);
```

### Step 2: Create lock path
```java
zk.create("/locks/mylock", null, Ids.OPEN_ACL_UNSAFE, 
    CreateMode.PERSISTENT);
```

### Step 3: Create sequential ephemeral node
```java
String node = zk.create("/locks/mylock/lock-", null,
    Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
```

### Step 4: Check if we're the lowest
```java
List<String> children = zk.getChildren("/locks/mylock", false);
Collections.sort(children);
if (node.equals(children.get(0))) {
    // We have the lock
}
```

### Step 5: Watch predecessor
```java
zk.exists("/locks/mylock/" + children.get(prevIndex), true);
```

### Step 6: Release
```java
zk.delete(node, -1);
```
