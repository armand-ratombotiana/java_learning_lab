# Exercises: Information Theory

## Theoretical Exercises

### Problem 1: Entropy Bounds
Prove that for any discrete random variable X, 0 ≤ H(X) ≤ log₂|X|. When does each bound hold?

### Problem 2: Chain Rule
Prove the chain rule for entropy: H(X, Y) = H(X) + H(Y|X).

### Problem 3: Mutual Information
Show that I(X; Y) = 0 if and only if X and Y are independent.

### Problem 4: KL Divergence
Prove that KL divergence is non-negative (Gibbs' inequality). Show that D_KL(P||Q) = 0 iff P = Q.

### Problem 5: Data Processing Inequality
Prove the data processing inequality: if X → Y → Z form a Markov chain, then I(X; Y) ≥ I(X; Z).

### Problem 6: Fano's Inequality
State and prove Fano's inequality, which relates the probability of error to conditional entropy.

### Problem 7: Shannon's Source Coding Theorem
Prove that for any uniquely decodable code, the expected code length L satisfies L ≥ H(X).

### Problem 8: Channel Capacity
Compute the capacity of a binary erasure channel (BEC) with erasure probability ε.

## Programming Exercises

### Exercise 1: Entropy Calculator
Write a program that reads a text file and computes its empirical character entropy.

### Exercise 2: Joint and Conditional Entropy
Implement functions for joint entropy H(X,Y) and conditional entropy H(Y|X). Verify that H(X,Y) = H(X) + H(Y|X).

### Exercise 3: Mutual Information
Implement mutual information for discrete distributions. Show that I(X;Y) ≥ 0 and equals 0 for independence.

### Exercise 4: KL Divergence
Implement KL divergence. Verify that it is non-negative and equals 0 for identical distributions.

### Exercise 5: Channel Capacity
Implement the Blahut-Arimoto algorithm to compute the capacity of an arbitrary discrete memoryless channel.

### Exercise 6: Huffman Coding
Implement Huffman encoding and decoding. Compute the average code length and compare to the entropy.

### Exercise 7: Shannon-Fano Coding
Implement Shannon-Fano encoding. Compare its average code length to Huffman coding.

### Exercise 8: Data Compression
Write a program that compresses a text file using Huffman coding. Calculate the compression ratio.

### Exercise 9: Cross Entropy Loss
Implement cross entropy and use it as a loss function for a simple binary classifier.

### Exercise 10: Redundancy Analysis
Analyze the redundancy of natural language by computing n-gram entropies of English text.

## Mini-Project: File Compressor
Build a file compression tool using Huffman coding. The tool should:
1. Read any file (text or binary)
2. Build Huffman tree from symbol frequencies
3. Encode the file
4. Save the compressed output with a header for decoding
5. Decode the compressed file back to original
6. Calculate and display compression ratio

## Real-World Project: Communication System Simulator
Simulate a complete communication system:
1. Generate a random binary message
2. Apply Huffman source coding
3. Add channel coding (e.g., repetition code or Hamming code)
4. Transmit through a noisy channel (BSC or AWGN)
5. Decode and error-correct
6. Decode the source
7. Compute bit error rate and compare to channel capacity limits
8. Plot the trade-off between code rate and error probability
