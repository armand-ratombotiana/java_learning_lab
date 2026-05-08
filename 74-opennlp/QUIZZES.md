# OpenNLP Quizzes - Assessment and Knowledge Check

## Easy Questions (1-10)

### Question 1
What is Apache OpenNLP primarily used for?

A) Image processing
B) Natural Language Processing
C) Database operations
D) Network programming

**Answer**: B

---

### Question 2
Which class is used for sentence detection in OpenNLP?

A) TokenizerME
B) SentenceDetectorME
C) POSTaggerME
D) NameFinderME

**Answer**: B

---

### Question 3
What does POS stand for in NLP?

A) Processing Operation System
B) Part of Speech
C) Post-Operation Service
D) Pattern Output System

**Answer**: B

---

### Question 4
What is the purpose of the NameFinderME class?

A) Finding names in text files
B) Named Entity Recognition
C) Name validation
D) Creating name databases

**Answer**: B

---

### Question 5
Which file format does OpenNLP use for trained models?

A) JSON
B) XML
C) Binary (.bin)
D) YAML

**Answer**: C

---

### Question 6
What is tokenization in NLP?

A) Converting text to images
B) Breaking text into smaller units (tokens)
C) Encrypting text
D) Compressing text

**Answer**: B

---

### Question 7
What type of machine learning algorithm does OpenNLP primarily use?

A) Clustering
B) Maximum Entropy
C) Neural Networks
D) Random Forest

**Answer**: B

---

### Question 8
How do you load a pre-trained model in OpenNLP?

A) Using ClassLoader
B) Using FileInputStream with model file
C) Using Database connection
D) Using Network request

**Answer**: B

---

### Question 9
Which POS tag represents a noun?

A) VB
B) JJ
C) NN
D) RB

**Answer**: C

---

### Question 10
What does the method sentDetect() return?

A) Single String
B) Array of Strings
C) Integer
D) Double array

**Answer**: B

---

## Medium Questions (11-20)

### Question 11
What is the purpose of clearAdaptiveData() in NameFinderME?

A) Clear all cached data
B) Clear adaptive data for next document
C) Reset confidence scores
D) Close the model

**Answer**: B

---

### Question 12
Which component would you use for document categorization?

A) SentenceDetectorME
B) TokenizerME
C) DocumentCategorizerME
D) ChunkerME

**Answer**: C

---

### Question 13
What is chunking in NLP?

A) Compressing data
B) Identifying phrase groups
C) Splitting files
D) Encrypting content

**Answer**: B

---

### Question 14
How do you get confidence scores for sentence detection?

A) getConfidence()
B) getSentenceProbabilities()
C) getScores()
D) getProbability()

**Answer**: B

---

### Question 15
What is the difference between SimpleTokenizer and TokenizerME?

A) Same functionality
B) SimpleTokenizer is rule-based, TokenizerME is ML-based
C) TokenizerME requires more memory
D) SimpleTokenizer is faster

**Answer**: B

---

### Question 16
Which entity type is NOT typically supported by OpenNLP?

A) PERSON
B) ORGANIZATION
C) LOCATION
D) EMAIL

**Answer**: D

---

### Question 17
What is the training data format for NER in OpenNLP?

A) JSON
B) CSV
C) XML-like tags
D) Plain text

**Answer**: C

---

### Question 18
What does the tagger.tag() method return?

A) Single String
B) String array
C) Integer array
D) Double array

**Answer**: B

---

### Question 19
How should resources be handled in OpenNLP?

A) No cleanup needed
B) Close models after use
C) Only close when application exits
D) Never close

**Answer**: B

---

### Question 20
What is Language Detection used for?

A) Detect programming languages
B) Identify natural language of text
C) Find language files
D) Translate languages

**Answer**: B

---

## Advanced Questions (21-30)

### Question 21
What is the purpose of AdaptiveData in OpenNLP?

A) Store model parameters
B) Store context-specific learning
C) Cache results
D) Manage memory

**Answer**: B

---

### Question 22
How do you implement custom NER?

A) Not possible
B) Train custom model with training data
C) Modify existing models
D) Use regex only

**Answer**: B

---

### Question 23
What is the typical pipeline order?

A) NER -> Tokenize -> POS -> Sentences
B) Sentences -> Tokenize -> POS -> NER
C) Tokenize -> Sentences -> POS -> NER
D) POS -> Tokenize -> Sentences -> NER

**Answer**: B

---

### Question 24
What is beam search in POS tagging?

A) Searching in memory
B) Maintaining multiple hypotheses
C) Compressing data
D) Parallel processing

**Answer**: B

---

### Question 25
How do you handle out-of-vocabulary words?

A) Skip them
B) Use adaptive features in tokenizer
C) Replace with random words
D) Throw exception

**Answer**: B

---

### Question 26
What is the purpose of Span in NER?

A) Time tracking
B) Marking character positions in text
C) Memory allocation
D) Error handling

**Answer**: B

---

### Question 27
How can you improve NER accuracy?

A) Use more training data
B) Tune parameters
C) Both of the above
D) Use larger models

**Answer**: C

---

### Question 28
What is coreference resolution?

A) Finding core data
B) Linking pronouns to entities
C) Finding errors
D) Encryption

**Answer**: B

---

### Question 29
What is the typical accuracy range for POS tagging?

A) 50-60%
B) 70-80%
C) 90-98%
D) 40-50%

**Answer**: C

---

### Question 30
When would you use WhitespaceTokenizer?

A) For production systems
B) When token boundaries are whitespace
C) For best accuracy
D) For multi-language support

**Answer**: B

---

## Answer Key

| Q | A | Q | A | Q | A |
|---|---|---|---|---|---|
| 1 | B | 11 | B | 21 | B |
| 2 | B | 12 | C | 22 | B |
| 3 | B | 13 | B | 23 | B |
| 4 | B | 14 | B | 24 | B |
| 5 | C | 15 | B | 25 | B |
| 6 | B | 16 | D | 26 | B |
| 7 | B | 17 | C | 27 | C |
| 8 | B | 18 | B | 28 | B |
| 9 | C | 19 | B | 29 | C |
| 10 | B | 20 | B | 30 | B |

---

## Scoring

- Easy: 8+ correct
- Medium: 7+ correct
- Advanced: 7+ correct
- Total 22+: Expert
- Total 15-21: Proficient
- Below 15: Review concepts

---

*Continue to EXERCISES.md and EDGE_CASES.md.*