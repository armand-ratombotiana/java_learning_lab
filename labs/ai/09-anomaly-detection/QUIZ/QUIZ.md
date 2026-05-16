# Anomaly Detection - Quiz

### Question 1
What is an anomaly in the context of machine learning?

A) Missing data points
B) Data points that deviate significantly from the majority of the data
C) Normal patterns in data
D) The mean of the data

**Answer: B** - Anomalies (also called outliers) are data points that behave differently from the majority of data.

---

### Question 2
What is the difference between supervised and unsupervised anomaly detection?

A) Supervised requires labeled anomalies, unsupervised doesn't
B) Supervised is faster
C) Unsupervised is more accurate
D) No difference

**Answer: A** - Supervised anomaly detection requires labeled training data with anomalies. Unsupervised detects anomalies without labels.

---

### Question 3
What does the Z-score measure?

A) The cluster assignment
B) How many standard deviations a point is from the mean
C) The probability of being normal
D) The distance to nearest neighbor

**Answer: B** - Z-score = (x - mean) / std_dev, indicating how far a point is from the mean in terms of standard deviations.

---

### Question 4
What is the Isolation Forest based on?

A) Distance to nearest neighbors
B) The fact that anomalies are easier to isolate than normal points
C) Density estimation
D) Statistical distributions

**Answer: B** - Isolation Forest isolates anomalies by random partitioning - anomalies require fewer splits to be isolated.

---

### Question 5
What is a limitation of using Z-score for anomaly detection?

A) It works for any distribution
B) It assumes data is normally distributed and is sensitive to outliers
C) It requires labeled data
D) It's too slow

**Answer: B** - Z-score assumes normality and can be affected by the outliers it's trying to detect.

---

### Question 6
What is the IQR method for outlier detection?

A) Interquartile range method - points beyond Q1-1.5*IQR or Q3+1.5*IQR are outliers
B) Infinite query range method
C) Internal query response method
D) Interval quartile range method

**Answer: A** - IQR = Q3 - Q1, outliers are below Q1 - 1.5*IQR or above Q3 + 1.5*IQR.

---

### Question 7
What is Local Outlier Factor (LOF) based on?

A) Global density
B) Local density comparison to neighbors
C) Distance from mean
D) Tree depth

**Answer: B** - LOF compares the density of a point to its neighbors - lower density than neighbors indicates outlier.

---

### Question 8
In what scenario would you use one-class SVM?

A) When you have labeled anomalies
B) When you only have normal data available
C) When data is normally distributed
D) When you need real-time detection

**Answer: B** - One-class SVM learns the boundary of normal data when only normal samples are available.

---

### Question 9
What is the challenge with imbalanced datasets in anomaly detection?

A) Too many anomalies to process
B) The model may predict everything as normal due to few anomalies
C) Anomalies are easier to find
D) No challenge

**Answer: B** - With few anomaly examples, models tend to predict normal class to maximize accuracy.

---

### Question 10
What is reconstruction error in autoencoder-based anomaly detection?

A) How well the autoencoder was trained
B) The difference between input and reconstructed output - high error indicates anomaly
C) The loss function value
D) The training time

**Answer: B** - Autoencoders trained on normal data will poorly reconstruct anomalies, resulting in high reconstruction error.

---

### Question 11 (Bonus)
What is the difference between point anomaly and contextual anomaly?

A) No difference
B) Point anomaly is a single anomalous data point; contextual anomaly is anomalous only in specific context
C) Contextual anomaly is always easy to detect
D) Point anomalies are always in clusters

**Answer: B** - Point anomalies stand out globally. Contextual anomalies are normal in certain conditions but abnormal in others.

---

### Question 12 (Bonus)
What is ensemble anomaly detection?

A) Using only one model
B) Combining multiple anomaly detection methods for better results
C) Using the same model multiple times
D) Using multiple feature types

**Answer: B** - Ensemble methods combine multiple detectors to improve detection accuracy and robustness.