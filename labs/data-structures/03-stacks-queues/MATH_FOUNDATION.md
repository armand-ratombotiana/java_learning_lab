# Math Foundation: Stacks & Queues

## Array-Based Stack Size

For a stack implemented with a dynamic array:
- Initial capacity C, growth factor f (e.g., 1.5)
- After n pushes: capacity = C × f^{⌈log_f(n/C)⌉}
- Total cost of resizing: sum of copies = C(f^{k+1} - 1)/(f - 1) where k = number of resizes
- Amortized cost per push: O(1)

## Circular Queue Index Math

For a circular queue of capacity m:
- `head` = index of first element
- `tail` = index where next element will be inserted
- Number of elements: `(tail - head + m) % m`
- `enqueue`: `elements[tail] = value; tail = (tail + 1) % m`
- `dequeue`: `value = elements[head]; head = (head + 1) % m`
- Full: `(tail + 1) % m == head`
- Empty: `head == tail`

## Two-Stack Queue Analysis

For m enqueue operations followed by n dequeue operations:
- Each element is pushed to in-stack at most once
- Each element is transferred from in to out at most once
- Each element is popped from out at most once
- Total operations: 2(m + n) transfers + pushes/pops
- Amortized O(1) per operation

## Priority Queue (Binary Heap) Heights

For a heap with n elements:
- Height: h = ⌊log₂(n)⌋
- Max nodes at depth d: 2^d
- Last non-leaf node index: ⌊n/2⌋ - 1

## Monotonic Stack Analysis

For an array of n elements, each element is:
- Pushed onto the stack exactly once
- Popped from the stack at most once
- Total operations: O(n)

## Queue Theory Fundamentals

- **Arrival rate** λ (items per time unit)
- **Service rate** μ (items per time unit)
- **Utilization** ρ = λ / μ
- **Average queue length** (M/M/1 queue): L_q = ρ² / (1 - ρ)
- **Average wait time**: W_q = L_q / λ
