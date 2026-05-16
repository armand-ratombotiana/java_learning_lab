# Probability for ML - MINI PROJECT

## Project: Spam Classifier using Naive Bayes

Build a spam classifier using Bayes' theorem with Gaussian distributions.

### Implementation

```java
public class SpamClassifier {
    private Map<String, GaussianDistribution> wordMeans;
    private Map<String, Double> classPriors;
    
    public void train(List<Email> trainingData) {
        // Calculate priors
        int spamCount = (int) trainingData.stream()
            .filter(e -> e.isSpam()).count();
        classPriors.put("spam", (double) spamCount / trainingData.size());
        classPriors.put("ham", 1 - classPriors.get("spam"));
        
        // Calculate word distributions per class
        List<String> vocabulary = extractVocabulary(trainingData);
        for (String word : vocabulary) {
            double[] spamFreqs = getWordFreqs(trainingData, word, true);
            double[] hamFreqs = getWordFreqs(trainingData, word, false);
            
            wordMeans.put(word + "_spam_mean", 
                new GaussianDistribution(mean(spamFreqs), stddev(spamFreqs)));
            wordMeans.put(word + "_ham_mean",
                new GaussianDistribution(mean(hamFreqs), stddev(hamFreqs)));
        }
    }
    
    public boolean predict(Email email) {
        double spamLogProb = Math.log(classPriors.get("spam"));
        double hamLogProb = Math.log(classPriors.get("ham"));
        
        for (String word : email.getWords()) {
            GaussianDistribution spamDist = wordMeans.get(word + "_spam_mean");
            GaussianDistribution hamDist = wordMeans.get(word + "_ham_mean");
            
            if (spamDist != null) {
                spamLogProb += Math.log(spamDist.pdf(email.getWordFreq(word)));
            }
            if (hamDist != null) {
                hamLogProb += Math.log(hamDist.pdf(email.getWordFreq(word)));
            }
        }
        
        return spamLogProb > hamLogProb;
    }
}
```

### Test It

```java
@Test
public void testSpamClassifier() {
    SpamClassifier classifier = new SpamClassifier();
    List<Email> training = loadTrainingData();
    classifier.train(training);
    
    Email testEmail = new Email("FREE money click here NOW");
    boolean isSpam = classifier.predict(testEmail);
    
    assertTrue(isSpam);
}
```

## Deliverables

- [ ] Load and preprocess training data
- [ ] Calculate class priors
- [ ] Compute word frequency distributions
- [ ] Implement Gaussian Naive Bayes prediction
- [ ] Evaluate on test set with accuracy, precision, recall