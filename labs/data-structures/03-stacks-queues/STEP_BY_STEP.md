# Step by Step: Stack & Queue Operations

## Stack — Push then Pop Sequence

```
push(10):  stack = [10]
push(20):  stack = [10, 20]
push(30):  stack = [10, 20, 30]
pop():     returns 30, stack = [10, 20]
push(40):  stack = [10, 20, 40]
pop():     returns 40, stack = [10, 20]
pop():     returns 20, stack = [10]
pop():     returns 10, stack = []
```

## Queue — Enqueue then Dequeue Sequence

```
enqueue(A): queue = [A]
enqueue(B): queue = [A, B]
enqueue(C): queue = [A, B, C]
dequeue():  returns A, queue = [B, C]
dequeue():  returns B, queue = [C]
enqueue(D): queue = [C, D]
dequeue():  returns C, queue = [D]
dequeue():  returns D, queue = []
```

## Two-Stack Queue — Enqueue/Dequeue Sequence

```
enqueue(1): in = [1], out = []
enqueue(2): in = [1, 2], out = []
enqueue(3): in = [1, 2, 3], out = []
dequeue():
  out is empty → transfer:
    pop 3 from in, push to out: in = [1, 2], out = [3]
    pop 2 from in, push to out: in = [1], out = [3, 2]
    pop 1 from in, push to out: in = [], out = [3, 2, 1]
  pop out → returns 1
  in = [], out = [3, 2]
dequeue():
  out not empty → pop out → returns 2
  in = [], out = [3]
enqueue(4): in = [4], out = [3]
dequeue():
  out not empty → pop out → returns 3
  in = [4], out = []
dequeue():
  out is empty → transfer:
    pop 4 from in, push to out: in = [], out = [4]
  pop out → returns 4
```

## Monotonic Stack — Next Greater Element

```
nums = [2, 1, 2, 4, 3]

Processing from left to right:
i=0, val=2: stack empty → push 0
i=1, val=1: stack top is 2 > 1 → push 1
i=2, val=2: stack top is 1 < 2 → pop 1, nextGreater[1]=2
             stack top is 2 == 2 → stop
             push 2
i=3, val=4: stack top is 2 < 4 → pop 2, nextGreater[2]=4
             stack top is 2 < 4 → pop 0, nextGreater[0]=4
             stack empty → push 3
i=4, val=3: stack top is 4 > 3 → push 4

Result: nextGreater = [4, 2, 4, -1, -1]
```
