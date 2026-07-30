# Lab 05 Interview: K-Nearest Neighbors

## Q1: Why is KNN called a "lazy" learner?
It memorizes the training data and defers computation until prediction time.

## Q2: How do you choose the optimal K?
Use cross-validation; try K from 1 to √n and pick the one with lowest validation error.

## Q3: What is the curse of dimensionality in KNN?
As dimensions increase, all points become roughly equidistant, making distance-based methods ineffective.

## Q4: How does weighted voting improve KNN?
Gives more influence to closer neighbors, reducing the impact of far-away points.

## Q5: Should features be normalized for KNN?
Yes — KNN is distance-sensitive; features with larger scales dominate the distance.
