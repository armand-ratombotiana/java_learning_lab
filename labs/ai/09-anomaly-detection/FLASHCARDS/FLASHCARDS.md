# Anomaly Detection - Flashcards

### Card 1: Z-score Method
**Q:** How does Z-score anomaly detection work?
**A:** Points with |Z-score| > threshold (typically 3) are flagged as anomalies - they are far from mean.

### Card 2: IQR Method
**Q:** What is the IQR outlier detection rule?
**A:** Outliers are below Q1 - 1.5*IQR or above Q3 + 1.5*IQR where IQR = Q3 - Q1.

### Card 3: Isolation Forest
**Q:** How does Isolation Forest detect anomalies?
**A:** Anomalies are easier to isolate - they require fewer random splits to be separated from data.

### Card 4: One-Class SVM
**Q:** When is one-class SVM used?
**A:** When training data contains only normal instances - it learns the boundary of normal behavior.

### Card 5: Local Outlier Factor
**Q:** What does LOF measure?
**A:** The ratio of local density around a point vs its neighbors - low LOF = normal, high LOF = anomaly.

### Card 6: Autoencoder Anomaly Detection
**Q:** How does autoencoder detect anomalies?
**A:** Trained on normal data, high reconstruction error indicates anomaly - abnormal patterns can't be reconstructed well.

### Card 7: Types of Anomalies
**Q:** What are the types of anomalies?
**A:** Point anomalies (single points), contextual anomalies (context-dependent), collective anomalies (patterns).

### Card 8: Imbalanced Data Challenge
**Q:** Why is anomaly detection challenging with imbalanced data?
**A:** With few anomalies, models predict everything as normal - use sampling, threshold tuning, or specialized metrics.

### Card 9: Statistical vs Machine Learning
**Q:** What is the difference?
**A:** Statistical uses distribution assumptions (Z-score, IQR). ML learns patterns from data (Isolation Forest, Autoencoders).

### Card 10: Mahalanobis Distance
**Q:** How is Mahalanobis distance different from Euclidean?
**A:** Accounts for correlation between features and uses covariance - better for multivariate data.

### Card 11: DBSCAN for Anomaly Detection
**Q:** Can DBSCAN be used for anomaly detection?
**A:** Yes - points labeled as noise (-1) by DBSCAN are potential outliers.

### Card 12: Evaluation Metrics
**Q:** What metrics are important for anomaly detection?
**A:** Precision, Recall, F1, AUC-ROC - accuracy is misleading due to class imbalance.