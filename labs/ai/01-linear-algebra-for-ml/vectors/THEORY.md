# Vector Theory & Intuition

## 💡 Intuition
In the context of Machine Learning, a vector is simply a list of numbers. However, depending on who you ask, a vector has different interpretations:

1. **The Physics Perspective**: A vector is an arrow in space. It has a **magnitude** (length) and a **direction**. It can move around in space as long as its length and direction remain unchanged.
2. **The Computer Science Perspective**: A vector is an ordered list of numbers (an array). If you have a dataset of houses, a single house might be represented as `[size, bedrooms, age, price]`. This is a 4-dimensional vector.
3. **The Mathematical Perspective**: A vector is an element of a vector space, meaning it can be added to other vectors and scaled by numbers (scalars), and the results will still be in that same space.

## 🚀 Why Vectors Matter in AI
Machine Learning models cannot process raw text, images, or audio. They only understand numbers. 
- An image is flattened into a vector of pixel intensities.
- A word is embedded into a dense vector representing its semantic meaning (Word2Vec, embeddings).
- A user's preferences are represented as a vector to calculate recommendations.

## ⚙️ Core Operations Intuition
- **Addition**: Combining two vectors. If you step 2 meters East (Vector A) and then 3 meters North (Vector B), your total displacement is Vector A + Vector B.
- **Scalar Multiplication**: Stretching or shrinking a vector. Multiplying a vector by 2 makes it twice as long in the same direction.
- **Dot Product**: A measure of how much two vectors "point in the same direction". It is the fundamental building block of neural networks (weights dot inputs).