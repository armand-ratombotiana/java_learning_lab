# Mental Models for Real-Time Feature Store

## 1. The Library
Feature store is a library. Feature definitions = catalog cards. Offline store = archives. Online store = reference desk (quick lookup). Librarian (serving API) fetches books (features) by call number (entity ID).

## 2. The Recipe Book
Features are ingredients. Feature recipes = transformations. Training = cooking one big batch. Serving = cooking individual portions. Same recipe must produce same ingredient for training and serving.

## 3. The Time Machine
Point-in-time join is a time machine. When you generate training labels (event at time T), the time machine ensures you only see features from before time T. No peeking into the future!
