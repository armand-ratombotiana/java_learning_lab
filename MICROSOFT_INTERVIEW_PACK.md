# MICROSOFT INTERVIEW PACK
## Technical Interview Preparation (Java Focus)

---

## Overview

Microsoft interviews focus on:
- ✅ Cloud infrastructure (Azure) thinking
- ✅ Enterprise systems and integration
- ✅ Data structures and algorithms
- ✅ Problem-solving approach
- ✅ Communication and collaboration

**Duration**: 45-60 minutes per round  
**Rounds**: Usually 2-3 technical + 1-2 system design  
**Focus**: Can you solve complex problems methodically?

---

## Part 1: Core Problems for Microsoft

### Problem 1: Expression Evaluator (Medium-Hard)

**Problem**: Design a calculator that can evaluate mathematical expressions with operators (+, -, *, /), parentheses, and order of operations.

**Constraints**:
- Support +, -, *, / operators
- Support parentheses for grouping
- Follow order of operations (PEMDAS)
- Handle negative numbers

**Example**:
```
Input: "3+2*2"
Output: 7

Input: "(3+2)*2"
Output: 10

Input: "6/2"
Output: 3
```

**Solution Using Stack**:

```java
public class ExpressionEvaluator {
    
    public int evaluate(String expression) {
        int num = 0;
        int result = 0;
        char operation = '+';
        Stack<Integer> stack = new Stack<>();
        
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            
            // Build multi-digit numbers
            if (Character.isDigit(c)) {
                num = num * 10 + (c - '0');
            }
            
            // Process operators and end of expression
            if (!Character.isDigit(c) && c != ' ' || i == expression.length() - 1) {
                if (operation == '+') {
                    stack.push(num);
                } else if (operation == '-') {
                    stack.push(-num);
                } else if (operation == '*') {
                    stack.push(stack.pop() * num);
                } else if (operation == '/') {
                    stack.push(stack.pop() / num);
                }
                
                if (!Character.isDigit(c) && c != ' ') {
                    operation = c;
                }
                num = 0;
            }
        }
        
        while (!stack.isEmpty()) {
            result += stack.pop();
        }
        
        return result;
    }
}
```

**Time Complexity**: O(n)  
**Space Complexity**: O(n)  
**Microsoft Tip**: They want to see clean, methodical problem-solving

---

### Problem 2: Serialize and Deserialize Binary Tree (Hard)

**Problem**: Design an algorithm to serialize and deserialize a binary tree. You may serialize the tree into any string representation and deserialize this string back into the original tree structure.

**Code**:

```java
public class TreeSerializer {
    
    public String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        serializeHelper(root, sb);
        return sb.toString();
    }
    
    private void serializeHelper(TreeNode node, StringBuilder sb) {
        if (node == null) {
            sb.append("null,");
            return;
        }
        
        sb.append(node.val).append(",");
        serializeHelper(node.left, sb);
        serializeHelper(node.right, sb);
    }
    
    public TreeNode deserialize(String data) {
        String[] values = data.split(",");
        Queue<String> queue = new LinkedList<>(Arrays.asList(values));
        return deserializeHelper(queue);
    }
    
    private TreeNode deserializeHelper(Queue<String> queue) {
        String val = queue.poll();
        
        if ("null".equals(val)) {
            return null;
        }
        
        TreeNode node = new TreeNode(Integer.parseInt(val));
        node.left = deserializeHelper(queue);
        node.right = deserializeHelper(queue);
        
        return node;
    }
    
    public class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int x) { val = x; }
    }
}
```

**What Microsoft Cares**:
- Can you think about edge cases? (null nodes, single node)
- Can you optimize space? (BFS vs DFS)
- Can you handle large trees?

---

### Problem 3: Design File System (Hard)

**Problem**: Design an in-memory file system that supports the following interfaces:

**Operations**:
```java
createPath(path)      // Create a path (any non-existent parent creates parents)
setContent(path, content)  // Set content for a file
getContent(path)      // Get content of a file
list(path)            // List contents (files: with extension, dirs: without)
```

**Solution**:

```java
public class FileSystem {
    private class File {
        boolean isFile;
        String content;
        Map<String, File> children;
        
        File() {
            isFile = false;
            content = "";
            children = new HashMap<>();
        }
    }
    
    private File root;
    
    public FileSystem() {
        root = new File();
    }
    
    public List<String> ls(String path) {
        File node = findNode(path);
        if (node.isFile) {
            return Arrays.asList(path.substring(path.lastIndexOf('/') + 1));
        }
        
        return new ArrayList<>(node.children.keySet()).stream()
            .sorted()
            .collect(Collectors.toList());
    }
    
    public void mkdir(String path) {
        findNode(path);  // This will create all parent directories
    }
    
    public void addContentToFile(String filePath, String content) {
        File node = findNode(filePath);
        node.isFile = true;
        node.content += content;
    }
    
    public String readContentFromFile(String filePath) {
        File node = findNode(filePath);
        return node.content;
    }
    
    private File findNode(String path) {
        String[] parts = path.split("/");
        File node = root;
        
        for (String part : parts) {
            if (part.isEmpty()) continue;
            
            if (!node.children.containsKey(part)) {
                node.children.put(part, new File());
            }
            node = node.children.get(part);
        }
        
        return node;
    }
}
```

**Time Complexity**: O(m) where m is path length  
**Space Complexity**: O(n) for all files/directories

---

## Part 2: System Design at Microsoft

### Problem: Design Azure Blob Storage (Distributed Storage Service)

**Requirements**:
- Store files up to TB size
- 99.99% availability
- Global distribution
- Cost-effective

**Architecture**:

```
┌─────────────────────────────────────┐
│      Client (Upload/Download)       │
├─────────────────────────────────────┤
│      Load Balancer / API Gateway    │
├─────────────────────────────────────┤
│      Blob Service (Metadata)        │
├─────────────────────────────────────┤
│  Data Nodes (Replicated, Sharded)   │
├─────────────────────────────────────┤
│      Replication Service            │
├─────────────────────────────────────┤
│    Backup/Archival (Cold Storage)   │
└─────────────────────────────────────┘
```

**Key Design Decisions**:
1. **Replication**: 3x replication for durability
2. **Sharding**: Distribute data by blob ID
3. **Caching**: Edge locations for fast downloads
4. **Consistency**: Strong consistency on writes
5. **Monitoring**: Health checks + automatic failover

---

## Part 3: Microsoft-Specific Tips

### What Microsoft Values
1. **Problem-solving methodology** - Think step-by-step
2. **Communication** - Explain while solving
3. **Partnerships** - How does this integrate with Azure?
4. **Code quality** - Clean, maintainable code
5. **Testing** - Consider edge cases

### Common Microsoft Follow-ups
- "How would you extend this for concurrency?"
- "What if the input was 10x larger?"
- "How would you test this?"
- "Design the error handling?"
- "What about thread safety?"

---

## Interview Tips for Microsoft

### Preparation Strategy
1. **Solve 25-30 LeetCode problems** (medium + hard)
2. **Practice system design** (4-5 real systems)
3. **Focus on core data structures**: Trees, Stacks, Queues, Graphs
4. **Study Azure concepts** if interviewing for cloud role
5. **Be ready to code on whiteboard** (they may use VS Code)

### During Interview
- ✅ Read problem carefully
- ✅ Ask clarifying questions
- ✅ Code cleanly and clearly
- ✅ Test with examples
- ✅ Discuss complexity
- ✅ Suggest optimizations

### Common Mistakes to Avoid
- ❌ Not asking questions before coding
- ❌ Writing untested code
- ❌ Missing edge cases
- ❌ Not explaining your approach
- ❌ Rushing to finish

---

## Practice Problems

| Problem | Difficulty | Category |
|---------|-----------|----------|
| Two Sum | Easy | Hash Map |
| Longest Substring Without Repeating | Medium | Sliding Window |
| Median of Two Sorted Arrays | Hard | Binary Search |
| Serialize Binary Tree | Hard | Tree + Recursion |
| LRU Cache | Hard | Design |
| Word Ladder II | Hard | BFS + Graph |
| Trapping Rain Water | Hard | Stack |
| Expression Evaluator | Medium | Stack |

---

## Resources

- **LeetCode**: Top 50 by Microsoft
- **Microsoft Learn**: Azure documentation
- **YouTube**: Microsoft tech talks
- **Books**: Cracking the Coding Interview
- **Pramp**: Mock interviews

---

**Good Luck at Microsoft! 🎯**
