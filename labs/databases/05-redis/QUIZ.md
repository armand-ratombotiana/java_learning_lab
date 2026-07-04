# Quiz: Redis

## Question 1
What is the maximum value size for a Redis string?
- A) 64MB
- B) 128MB
- C) 256MB
- D) 512MB

**Answer: D) 512MB**

## Question 2
Which data structure is best for a real-time leaderboard?
- A) List
- B) Set
- C) Sorted Set
- D) Hash

**Answer: C) Sorted Set**

## Question 3
What does the `INCR` command return if the key doesn't exist?
- A) Error
- B) NULL
- C) 0
- D) 1

**Answer: D) 1 (creates key with value 0, then increments)**

## Question 4
How many hash slots does Redis Cluster use?
- A) 1024
- B) 4096
- C) 16384
- D) 65536

**Answer: C) 16384**

## Question 5
Which command is used to check if a key exists in Redis?
- A) HAS
- B) CHECK
- C) EXISTS
- D) CONTAINS

**Answer: C) EXISTS**

## Question 6
What is the default eviction policy in Redis?
- A) allkeys-lru
- B) volatile-lru
- C) noeviction
- D) allkeys-random

**Answer: C) noeviction**

## Question 7
Which Redis client is recommended for reactive Spring applications?
- A) Jedis
- B) Lettuce
- C) Redisson
- D) JRedis

**Answer: B) Lettuce**

## Question 8
What does BRPOP do differently than RPOP?
- A) Blocks if the list is empty
- B) Returns multiple elements
- C) Pops from the left instead of right
- D) 10x faster

**Answer: A) Blocks if the list is empty**

## Question 9
Which of the following is NOT a valid Redis persistence option?
- A) RDB
- B) AOF
- C) WAL
- D) None

**Answer: C) WAL**

## Question 10
What is the purpose of `SETNX`?
- A) Set value if key doesn't exist
- B) Set value with no expiration
- C) Set multiple keys
- D) Set with notification

**Answer: A) Set value if key doesn't exist**
