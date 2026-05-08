# Quizzes: Deep Learning Fundamentals

Test your understanding with 100+ questions across 4 difficulty levels.

---

## Level 1: Beginner (Questions 1-25)

### Q1: What is deep learning?
A) A type of database
B) Subset of ML using neural networks with multiple layers
C) A programming language
D) A hardware component

### Q2: What does a neuron compute?
A) Random values
B) Weighted sum of inputs plus activation
C) Only the bias
D) The gradient

### Q3: Which activation outputs values between 0 and 1?
A) ReLU
B) Tanh
C) Sigmoid
D) Leaky ReLU

### Q4: What is the purpose of a loss function?
A) Speed up training
B) Measure prediction error
C) Initialize weights
D) Regularize the model

### Q5: Which optimizer uses momentum?
A) SGD only
B) Adam
C) Only RMSprop
D) None of the above

### Q6: What is backpropagation?
A) Forward computation
B) Computing gradients and updating weights
C) Data augmentation
D) Model evaluation

### Q7: What does ReLU return for negative input?
A) Negative value
B) Zero
C) One
D) The input unchanged

### Q8: Which layer is used for classification output?
A) Convolution layer
B) Pooling layer
C) Fully connected layer
D) Batch norm layer

### Q9: What is overfitting?
A) Model trains too fast
B) Model performs well on training but poorly on test
C) Model is too simple
D) Learning rate is too low

### Q10: Dropout randomly:
A) Adds new neurons
B) Removes neurons during training
C) Changes activation function
D) Updates weights

### Q11: What does CNN stand for?
A) Computer Neural Network
B) Convolutional Neural Network
C) Classical Neural Network
D) Complex Neural Network

### Q12: What is pooling used for?
A) Increase spatial dimensions
B) Reduce spatial dimensions
C) Add colors to images
D) Initialize weights

### Q13: Which is NOT a CNN component?
A) Convolution
B) Pooling
C) Recurrence
D) Fully Connected

### Q14: What does RNN stand for?
A) Real Neural Network
B) Recurrent Neural Network
C) Reverse Neural Network
D) Random Neural Network

### Q15: L2 regularization adds to loss:
A) Sum of absolute weights
B) Sum of squared weights
C) Number of neurons
D) Learning rate

### Q16: Adam optimizer combines:
A) Only momentum
B) Momentum and RMSProp
C) Only learning rate
D) Batch size and epochs

### Q17: What is a batch?
A) Entire dataset
B) Subset of data processed together
C) Single sample
D) Learning rate

### Q18: Which metric is used for classification?
A) MSE
B) Cross-entropy
C) MAE
D) R²

### Q19: What does GPU stand for?
A) General Processing Unit
B) Graphics Processing Unit
C) Gradient Processing Unit
D) Graph Processing Unit

### Q20: What is transfer learning?
A) Moving data between databases
B) Using pre-trained model for new task
C) Training from scratch
D) Data transformation

### Q21: Which problem does vanishing gradient occur?
A) Very deep networks
B) Very shallow networks
C) Only with ReLU
D) Never with CNN

### Q22: What is a tensor?
A) A neural network layer
B) Multi-dimensional array
C) A type of activation
D) A loss function

### Q23: Epoch means:
A) One batch
B) One complete pass through dataset
C) One weight update
D) One layer

### Q24: Which framework is developed by Facebook?
A) TensorFlow
B) PyTorch
C) JAX
D) Keras

### Q25: What does CNN use to detect features?
A) Filters/Kernels
B) Recurrence
C) Loops
D) SQL queries

---

## Level 2: Intermediate (Questions 26-50)

### Q26: What does the dot product in convolution measure?
A) Random similarity
B) Pattern similarity between filter and image patch
C) Color intensity
D) Image size

### Q27: What problem does skip connection in ResNet solve?
A) Overfitting
B) Vanishing gradients in very deep networks
C) GPU memory
D) Data augmentation

### Q28: In LSTM, what does the forget gate do?
A) Decides what to output
B) Decides what to keep from previous state
C) Decides what to input
D) Updates the cell state

### Q29: What is the attention mechanism's purpose?
A) Reduce model size
B) Focus on relevant parts of input
C) Speed up training
D) Initialize weights

### Q30: Which is true about softmax?
A) Outputs can be negative
B) Outputs sum to 1
C) Always returns 0
D) Only used in hidden layers

### Q31: What does a variational autoencoder learn?
A) Deterministic encoding
B) Probability distribution of data
C) Only compression
D) Classification boundaries

### Q32: In GANs, what does the generator do?
A) Classifies real vs fake
B) Creates fake samples
C) Computes loss
D) Updates discriminator

### Q33: What is the main advantage of CNN over FNN for images?
A) Fewer parameters
B) Captures spatial relationships
C) Uses RNN
D) Simpler architecture

### Q34: Which technique helps prevent overfitting?
A) Increase learning rate
B) Use more parameters
C) Add dropout
D) Train longer

### Q35: What is the vanishing gradient problem?
A) Gradients become extremely large
B) Gradients become extremely small
C) Gradients become zero
D) Gradients oscillate

### Q36: BERT is an example of:
A) Autoencoder
B) Encoder-only transformer
C) Encoder-decoder
D) GAN

### Q37: What is the purpose of padding in convolution?
A) Increase image size
B) Preserve spatial dimensions
C) Remove borders
D) Add noise

### Q38: Which optimizer is best for most deep learning?
A) Only SGD
B) Adam
C) Only Adagrad
D) Only RMSprop

### Q39: What does early stopping do?
A) Stops training immediately
B) Stops when validation loss increases
C) Stops at first epoch
D) Removes validation data

### Q40: What is data augmentation?
A) Making data smaller
B) Artificially increasing training data
C) Removing data
D) Using pre-trained data

### Q41: In transformer, what is positional encoding?
A) Network architecture
B) Adding position information to input
C) Type of attention
D) Loss function

### Q42: What is fine-tuning?
A) Training only output layer
B) Continuing training on new task
C) Using very small model
D) Reducing learning rate to zero

### Q43: Which is NOT a regularization technique?
A) L1
B) L2
C) Dropout
D) Increase batch size

### Q44: What is gradient clipping?
A) Making gradients larger
B) Capping gradients to prevent explosion
C) Removing gradients
D) Computing gradients

### Q45: What does one-hot encoding represent?
A) Numbers
B) Categorical variables as binary vectors
C) Images
D) Audio

### Q46: What is the purpose of batch normalization?
A) Normalize input only
B) Normalize layer inputs, stabilizes training
C) Remove batches
D) Create batches

### Q47: Which is true about pooling types?
A) Max pooling takes average
B) Average pooling takes maximum
C) Both do different things
D) Neither is used in CNN

### Q48: What does feature map represent?
A) Original image
B) Output of convolution with learned filters
C) Fully connected layer
D) Loss value

### Q49: In reinforcement learning, what is a policy?
A) Data storage
B) Mapping from states to actions
C) Reward function
D) Environment model

### Q50: What is the ReLU "dying ReLU" problem?
A) ReLU becomes too fast
B) Neurons output zero forever
C) ReLU has vanishing gradient
D) ReLU causes overfitting

---

## Level 3: Advanced (Questions 51-75)

### Q51: How does attention compute relevance between positions?
A) Random selection
B) Query-Key-Value dot products
C) Direct multiplication
D) Subtraction

### Q52: What is mode collapse in GANs?
A) Generator produces diverse outputs
B) Generator produces limited variety of outputs
C) Discriminator fails
D) Training is unstable

### Q53: In transformer, multi-head attention allows:
A) Single attention pattern
B) Multiple attention patterns simultaneously
C) No attention
D) Fixed attention

### Q54: What is the difference between ViT and CNN?
A) ViT uses only convolution
B) ViT treats images as sequences of patches
C) CNN doesn't use patches
D) No difference

### Q55: What does label smoothing do?
A) Makes labels more extreme
B) Converts hard labels to soft distributions
C) Removes labels
D) Adds more labels

### Q56: What is warmup in learning rate schedule?
A) Start with high learning rate
B) Start with low, then increase, then decrease
C) No learning rate
D) Constant learning rate

### Q57: What is gradient accumulation?
A) Computing multiple gradients
B) Simulating larger batch with smaller batches
C) Adding gradients together
D) Removing gradients

### Q58: What does Focal Loss address?
A) Easy examples
B) Class imbalance with hard examples
C) Only binary classification
D) Regression

### Q59: What is knowledge distillation?
A) Training on real data
B) Transferring knowledge from large to small model
C) Model compression
D) Data compression

### Q60: What is the difference between generative and discriminative models?
A) No difference
B) Generative learns P(x,y), discriminative learns P(y|x)
C) Discursive generates data
D) Only generative models exist

### Q61: What is self-supervised learning?
A) Using labeled data
B) Creating labels from data structure itself
C) No learning
D) Unsupervised only

### Q62: How does contrastive learning work?
A) Push similar items closer, different items apart
B) Only pushes similar items
C) Only pushes different items
D) Random push

### Q63: What is a triplet loss?
A) Uses three samples: anchor, positive, negative
B) Only uses two samples
C) Uses random samples
D) Not used in deep learning

### Q64: What is the purpose of weight initialization?
A) Prevent dead neurons, enable gradient flow
B) Make weights larger
C) Make weights smaller
D) No purpose

### Q65: Xavier vs He initialization - when to use which?
A) Always Xavier
B) Xavier for tanh/sigmoid, He for ReLU
C) Always He
D) Neither

### Q66: What is curriculum learning?
A) Use random training order
B) Train on easy samples first, then harder
C) Skip hard samples
D) Use only hard samples

### Q67: What does mixup data augmentation do?
A) Mixes two images
B) Linear interpolation of samples and labels
C) Only mixes labels
D) Removes images

### Q68: What is the purpose of learning rate scheduler?
A) Constant learning rate
B) Adjust learning rate during training
C) Only increase learning rate
D) Remove learning rate

### Q69: What is pseudo-labeling?
A) Using only real labels
B) Using model predictions as labels for unlabeled data
C) Removing labels
D) Creating fake labels

### Q70: What is semi-supervised learning?
A) Only labeled data
B) Using both labeled and unlabeled data
C) No data
D) Only unlabeled data

### Q71: How does gradient checkpointing save memory?
A) Stores all activations
B) Recomputes activations during backward pass
C) Removes gradients
D) Uses larger batches

### Q72: What is the purpose of model ensembling?
A) Single model
B) Combining multiple models for better performance
C) Training faster
D) Reducing parameters

### Q73: What is SWA (Stochastic Weight Averaging)?
A) Random weight selection
B) Averaging weights from different training epochs
C) Single weight
D) No averaging

### Q74: What is the difference between SAM and regular Adam?
A) No difference
B) SAM seeks flat minima for better generalization
C) SAM is slower
D) SAM uses less memory

### Q75: What does Monte Carlo dropout provide?
A) Deterministic predictions
B) Uncertainty estimates via multiple dropout passes
C) No uncertainty
D) Only point estimates

---

## Level 4: Expert/Interview (Questions 76-100)

### Q76: Explain the backpropagation through time (BPTT) process for RNNs.

### Q77: How would you debug a neural network with NaN losses?

### Q78: What are the computational complexity differences between CNN and Transformer?

### Q79: How does gradient descent differ from genetic algorithms?

### Q80: What is the lottery ticket hypothesis?

### Q81: How would you implement efficient attention for long sequences?

### Q82: Explain how to prevent catastrophic forgetting in continual learning.

### Q83: What are the trade-offs between precision and recall in classification?

### Q84: How would you design a loss function for multi-task learning?

### Q85: Explain the differences between SVD and autoencoders for dimensionality reduction.

### Q86: How does the kernel trick in SVM relate to neural networks?

### Q87: What are the challenges of training on imbalanced datasets?

### Q88: How would you implement a custom activation function?

### Q89: What are the differences between beam search and greedy decoding?

### Q90: Explain how neural architecture search works.

### Q91: How does federated learning work and what are its challenges?

### Q92: What is the difference between parameter-efficient and full fine-tuning?

### Q93: How would you optimize a model for edge deployment?

### Q94: What are the differences between ONNX, TensorFlow Lite, and TorchScript?

### Q95: Explain how to measure and improve model fairness.

### Q96: What is gradient perturbation and why is it used?

### Q97: How does meta-learning differ from transfer learning?

### Q98: What are the challenges of training on distributed systems?

### Q99: Explain the relationship between capacity, overfitting, and underfitting.

### Q100: How would you implement a custom layer in PyTorch and TensorFlow?

---

## Answer Key

### Beginner (1-25)
1-B, 2-B, 3-C, 4-B, 5-B, 6-B, 7-B, 8-C, 9-B, 10-B, 11-B, 12-B, 13-C, 14-B, 15-B, 16-B, 17-B, 18-B, 19-B, 20-B, 21-A, 22-B, 23-B, 24-B, 25-A

### Intermediate (26-50)
26-B, 27-B, 28-B, 29-B, 30-B, 31-B, 32-B, 33-B, 34-C, 35-B, 36-B, 37-B, 38-B, 39-B, 40-B, 41-B, 42-B, 43-D, 44-B, 45-B, 46-B, 47-C, 48-B, 49-B, 50-B

### Advanced (51-75)
51-B, 52-B, 53-B, 54-B, 55-B, 56-B, 57-B, 58-B, 59-B, 60-B, 61-B, 62-A, 63-A, 64-A, 65-B, 66-B, 67-B, 68-B, 69-B, 70-B, 71-B, 72-B, 73-B, 74-B, 75-B

### Expert (76-100)
Answers require detailed explanations (see below)

---

## Expert Answers Summary

**Q76:** BPTT unrolls RNN through time, computes gradients for each timestep, and accumulates gradients across the sequence.

**Q77:** Check data for NaN/Inf, reduce learning rate, add gradient clipping, verify data pipeline, check for division by zero.

**Q78:** CNN: O(n²) for convolution; Transformer attention: O(n²) for sequence length, but can be optimized to O(n).

**Q79:** Gradient descent is gradient-based optimization; genetic algorithms use population-based evolutionary search.

**Q80:** Dense networks contain sparse subnetworks that, when trained in isolation, can match full network performance.

**Q81:** Use sparse attention, linear attention, flash attention, or sliding window attention.

**Q82:** Use regularization, rehearsal, or dynamic architecture to prevent forgetting previous tasks.

**Q83:** Precision = TP/(TP+FP), Recall = TP/(TP+FN); tradeoff depends on application cost of false positives vs negatives.

**Q84:** Combine losses with task-specific weights: L_total = Σ λᵢLᵢ

**Q85:** SVD is linear, automatic; autoencoders can learn nonlinear mappings.

**Q86:** Both map to high-dimensional feature spaces to find separations, but neural networks learn features automatically.

**Q87:** Use class weights, oversampling, undersampling, SMOTE, focal loss, or appropriate metrics.

**Q88:** Subclass torch.nn.Module or tf.keras.layers.Layer, implement forward/call method.

**Q89:** Beam search keeps top-k candidates; greedy picks best at each step. Beam is more accurate but slower.

**Q90:** NAS uses search algorithms (RL, evolution, gradient) to find optimal architecture from search space.

**91:** Train across distributed devices, aggregate model updates without sharing raw data. Challenges: communication, heterogeneity, privacy.

**92:** PEFT (LoRA, adapters) updates few parameters; full fine-tuning updates all.

**93:** Quantization, pruning, knowledge distillation, efficient architectures.

**94:** ONNX = format; TFLite = mobile/edge; TorchScript = PyTorch export.

**95:** Analyze model predictions across demographic groups, debias techniques, fairness constraints.

**96:** Add noise to gradients during training for better generalization (Sharpness-Aware Minimization uses this).

**97:** Meta-learning learns to learn; transfer learns from one task for another.

**98:** Data parallelism vs model parallelism, communication overhead, synchronization.

**99:** Capacity: model's ability to fit complex patterns; too high = overfitting, too low = underfitting.

**100:** PyTorch: class MyLayer(nn.Module); TensorFlow: class MyLayer(tf.keras.layers.Layer).

---

## Next Steps

- Practice with [EXERCISES.md](./EXERCISES.md)
- Review [EDGE_CASES.md](./EDGE_CASES.md)
- Read [PEDAGOGIC_GUIDE.md](./PEDAGOGIC_GUIDE.md) for learning path