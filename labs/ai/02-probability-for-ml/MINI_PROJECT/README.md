# Mini Project: Naive Bayes Text Classifier

## Project Overview
Implement a Naive Bayes classifier for spam detection or sentiment analysis.

## Steps

### 1. Data Preparation
```python
from sklearn.datasets import fetch_20newsgroups
import numpy as np

# Load dataset
categories = ['alt.atheism', 'comp.graphics', 'sci.med', 'soc.religion Christian']
train = fetch_20newsgroups(subset='train', categories=categories)
test = fetch_20newsgroups(subset='test', categories=categories)
```

### 2. Feature Extraction
```python
from sklearn.feature_extraction.text import CountVectorizer

vectorizer = CountVectorizer(stop_words='english', max_features=10000)
X_train = vectorizer.fit_transform(train.data)
X_test = vectorizer.transform(test.data)
```

### 3. Implement Naive Bayes
```python
class NaiveBayesMultinomial:
    def __init__(self, alpha=1.0):
        self.alpha = alpha  # Laplace smoothing

    def fit(self, X, y):
        self.classes = np.unique(y)
        n_docs, n_words = X.shape
        n_classes = len(self.classes)

        # P(y)
        self.class_probs = np.zeros(n_classes)
        for i, c in enumerate(self.classes):
            self.class_probs[i] = np.sum(y == c) / n_docs

        # P(x|y) with Laplace smoothing
        self.word_probs = np.zeros((n_classes, n_words))
        for i, c in enumerate(self.classes):
            X_c = X[y == c]
            word_counts = np.array(X_c.sum(axis=0)).flatten()
            total_words = word_counts.sum()
            self.word_probs[i] = (word_counts + self.alpha) / (total_words + self.alpha * n_words)

    def predict(self, X):
        log_probs = X @ np.log(self.word_probs.T) + np.log(self.class_probs)
        return self.classes[np.argmax(log_probs, axis=1)]
```

### 4. Evaluation
```python
from sklearn.metrics import classification_report, accuracy_score

model = NaiveBayesMultinomial(alpha=1.0)
model.fit(X_train, train.target)
predictions = model.predict(X_test)

print(f"Accuracy: {accuracy_score(test.target, predictions)}")
print(classification_report(test.target, predictions, target_names=categories))
```

### 5. Comparison with sklearn
```python
from sklearn.naive_bayes import MultinomialNB
nb = MultinomialNB()
nb.fit(X_train, train.target)
sklearn_pred = nb.predict(X_test)
```

## Deliverables
- Custom Naive Bayes implementation
- Performance comparison with sklearn
- Analysis of smoothing parameter effects