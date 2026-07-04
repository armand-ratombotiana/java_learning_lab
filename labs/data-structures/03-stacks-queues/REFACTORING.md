# Refactoring Stacks & Queues

## Replace Stack with ArrayDeque

```java
// Before — legacy
Stack<String> stack = new Stack<>();

// After — modern
Deque<String> stack = new ArrayDeque<>();
stack.push("item");
String item = stack.pop();
```

## Replace Manual Queue with Deque

```java
// Before — manual array queue
int[] queue = new int[100];
int head = 0, tail = 0;

// After
Deque<Integer> queue = new ArrayDeque<>();
queue.addLast(value);
Integer value = queue.pollFirst();
```

## Use Queue Interface

```java
// Before — concrete type
LinkedList<Task> queue = new LinkedList<>();
queue.addLast(task);
Task t = queue.removeFirst();

// After — interface
Queue<Task> queue = new LinkedList<>();
queue.offer(task);
Task t = queue.poll();
```

## Replace Loops with Collections Methods

```java
// Before — pop all manually
while (!stack.isEmpty()) {
    result.add(stack.pop());
}

// After
result.addAll(stack);  // for some collections
```

## Extract Monotonic Stack Pattern

```java
// Before — inline
for (int i = 0; i < nums.length; i++) {
    while (!stack.isEmpty() && nums[stack.peek()] < nums[i]) {
        result[stack.pop()] = nums[i];
    }
    stack.push(i);
}

// After — reusable function
int[] nextGreaterElement(int[] nums, boolean greater) {
    Deque<Integer> stack = new ArrayDeque<>();
    int[] result = new int[nums.length];
    Arrays.fill(result, -1);
    for (int i = 0; i < nums.length; i++) {
        while (!stack.isEmpty() &&
               (greater ? nums[stack.peek()] < nums[i]
                        : nums[stack.peek()] > nums[i])) {
            result[stack.pop()] = nums[i];
        }
        stack.push(i);
    }
    return result;
}
```
