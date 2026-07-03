# Module 03: Collections Framework - Mini Project

**Project Name**: Text Analyzer & Word Counter  
**Difficulty Level**: Intermediate  
**Estimated Time**: 2-3 hours

---

## 🎯 Objective
Utilize the Java Collections Framework (specifically `List`, `Set`, and `Map`) to parse, store, and analyze text data efficiently.

## 📝 Requirements

### Core Features
1. **Input Processing**:
   - Write a method that takes a large multi-line string (e.g., a paragraph of text) and splits it into individual words.
   - Strip out punctuation and convert all words to lowercase.

2. **List Operations (Order & Duplicates)**:
   - Store all processed words in a `List<String>`.
   - Calculate and print the total number of words.

3. **Set Operations (Uniqueness)**:
   - Pass the list of words into a `Set<String>` to eliminate duplicates.
   - Calculate and print the total number of *unique* words.

4. **Map Operations (Frequencies)**:
   - Create a `Map<String, Integer>` to count the frequency of each word in the text.
   - Iterate over the original list, updating the count for each word in the map.

5. **Sorting (Advanced)**:
   - Extract the entries from the map and sort them by frequency in descending order.
   - Print the Top 5 most frequently used words.

---

## 💡 Solution Blueprint

1. **Splitting Text**: Use `text.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+")` to clean and split the text into an array.
2. **List**: `List<String> wordList = Arrays.asList(wordsArray);`
3. **Set**: `Set<String> uniqueWords = new HashSet<>(wordList);`
4. **Map**: 
   ```java
   Map<String, Integer> wordCounts = new HashMap<>();
   for (String word : wordList) {
       wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
   }
   ```
5. **Sorting**: Convert the map's entry set to a list, and use `Collections.sort()` with a custom `Comparator` comparing the values.