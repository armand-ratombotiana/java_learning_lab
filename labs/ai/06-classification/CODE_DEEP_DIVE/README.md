# Classification - Code Deep Dive

```python
import numpy as np
from sklearn.linear_model import LogisticRegression
from sklearn.svm import SVC
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import RandomForestClassifier, GradientBoostingClassifier
from sklearn.metrics import accuracy_score, classification_report, confusion_matrix
from sklearn.preprocessing import StandardScaler

# === LOGISTIC REGRESSION ===

model = LogisticRegression(max_iter=1000)
model.fit(X_train, y_train)
y_pred = model.predict(X_test)

# Multi-class
model_multi = LogisticRegression(multi_class='multinomial', max_iter=1000)
model_multi.fit(X_train, y_train)

# === SVM ===

# Linear SVM
svm_linear = SVC(kernel='linear', C=1.0)
svm_linear.fit(X_train, y_train)

# RBF SVM
svm_rbf = SVC(kernel='rbf', C=1.0, gamma='scale')
svm_rbf.fit(X_train, y_train)

# === DECISION TREE ===

dt = DecisionTreeClassifier(max_depth=5, min_samples_split=10)
dt.fit(X_train, y_train)

# Feature importance
print(dt.feature_importances_)

# === RANDOM FOREST ===

rf = RandomForestClassifier(n_estimators=100, max_depth=10, random_state=42)
rf.fit(X_train, y_train)

# === GRADIENT BOOSTING ===

gb = GradientBoostingClassifier(n_estimators=100, learning_rate=0.1, max_depth=3)
gb.fit(X_train, y_train)

# === EVALUATION ===

cm = confusion_matrix(y_test, y_pred)
print(classification_report(y_test, y_pred))

# ROC curve
from sklearn.metrics import roc_curve, auc
y_prob = model.predict_proba(X_test)
fpr, tpr, thresholds = roc_curve(y_test, y_prob[:, 1])
roc_auc = auc(fpr, tpr)

# === XGBOOST ===
from xgboost import XGBClassifier

xgb = XGBClassifier(n_estimators=100, max_depth=5, learning_rate=0.1)
xgb.fit(X_train, y_train)

# === LIGHTGBM ===
from lightgbm import LGBMClassifier

lgbm = LGBMClassifier(n_estimators=100, max_depth=5, learning_rate=0.1)
lgbm.fit(X_train, y_train)
```