# NLP Pipeline Projects - Module 78

This module covers NLP pipeline components including tokenization, lemmatization, stemming, POS tagging, NER, and text processing with real-world applications.

## Table of Contents
- [Mini-Projects (2 hours each)](#mini-projects-2-hours-each)
- [Real-World Projects](#real-world-projects)

---

## Mini-Projects (2 hours each)

### 1. Text Preprocessing Pipeline

**Objective:** Build a comprehensive text preprocessing pipeline that handles cleaning, normalization, and preparation of raw text data.

**Project Structure:**
```
text-preprocessing/
├── pom.xml
└── src/main/java/com/learning/nlppipeline/preprocessing/
    ├── TextPreprocessor.java
    ├── PreprocessingPipeline.java
    └── PreprocessingConfig.java
```

**Implementation:**

```java
package com.learning.nlppipeline.preprocessing;

import java.util.regex.*;

public class TextPreprocessor {
    
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]+>");
    private static final Pattern URL_PATTERN = Pattern.compile("https?://[^\\s]+");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    private static final Pattern MULTIPLE_SPACES = Pattern.compile("\\s+");
    private static final Pattern SPECIAL_CHARS = Pattern.compile("[^a-zA-Z0-9\\s.,!?-]");
    
    public String preprocess(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        
        text = removeHtmlTags(text);
        text = removeUrls(text);
        text = removeEmails(text);
        text = normalizeWhitespace(text);
        text = normalizePunctuation(text);
        
        return text.trim();
    }
    
    private String removeHtmlTags(String text) {
        return HTML_TAG_PATTERN.matcher(text).replaceAll(" ");
    }
    
    private String removeUrls(String text) {
        return URL_PATTERN.matcher(text).replaceAll(" ");
    }
    
    private String removeEmails(String text) {
        return EMAIL_PATTERN.matcher(text).replaceAll(" ");
    }
    
    private String normalizeWhitespace(String text) {
        return MULTIPLE_SPACES.matcher(text).replaceAll(" ");
    }
    
    private String normalizePunctuation(String text) {
        text = text.replaceAll("\\.{2,}", ".");
        text = text.replaceAll("\\!{2,}", "!");
        text = text.replaceAll("\\?{2,}", "?");
        text = text.replaceAll(",{2,}", ",");
        
        text = text.replaceAll("\\s([.,!?])", "$1");
        text = text.replaceAll("([.,!?])\\s", "$1 ");
        
        return text;
    }
    
    public String convertToLowercase(String text) {
        return text.toLowerCase();
    }
    
    public String removeSpecialCharacters(String text) {
        return SPECIAL_CHARS.matcher(text).replaceAll("");
    }
    
    public String normalizeText(String text) {
        text = preprocess(text);
        text = convertToLowercase(text);
        text = removeSpecialCharacters(text);
        return text;
    }
}

public class PreprocessingPipeline {
    
    private TextPreprocessor preprocessor;
    private List<PreprocessingStep> steps;
    
    public PreprocessingPipeline() {
        this.preprocessor = new TextPreprocessor();
        this.steps = new ArrayList<>();
    }
    
    public void addStep(PreprocessingStep step) {
        steps.add(step);
    }
    
    public String process(String text) {
        String result = text;
        
        for (PreprocessingStep step : steps) {
            result = step.execute(result);
        }
        
        return result;
    }
    
    public String processWithDefaultPipeline(String text) {
        text = preprocessor.normalizeText(text);
        return text;
    }
}

@FunctionalInterface
interface PreprocessingStep {
    String execute(String text);
}
```

**Build and Test:**
```bash
mvn clean compile
```

---

### 2. Tokenization Strategies

**Objective:** Implement various tokenization strategies including whitespace, regex-based, sentence, and word tokenization using Apache OpenNLP.

**Project Structure:**
```
tokenization/
├── pom.xml
└── src/main/java/com/learning/nlppipeline/tokenizer/
    ├── TokenizerService.java
    ├── WhitespaceTokenizerImpl.java
    ├── OpenNLPTokenizer.java
    └── TokenizerFactory.java
```

**Implementation:**

```java
package com.learning.nlppipeline.tokenizer;

import opennlp.tools.tokenize.*;
import opennlp.tools.util.*;

import java.io.*;
import java.util.*;

public class TokenizerService {
    
    private WhitespaceTokenizer whitespaceTokenizer;
    private SimpleTokenizer simpleTokenizer;
    private TokenizerModel tokenizeModel;
    
    public TokenizerService() {
        this.whitespaceTokenizer = WhitespaceTokenizer.INSTANCE;
        this.simpleTokenizer = SimpleTokenizer.INSTANCE;
    }
    
    public void loadOpenNLPModel(InputStream modelStream) throws IOException {
        this.tokenizeModel = new TokenizerModel(modelStream);
    }
    
    public String[] tokenizeWithWhitespace(String text) {
        return whitespaceTokenizer.tokenize(text);
    }
    
    public String[] tokenizeSimple(String text) {
        return simpleTokenizer.tokenize(text);
    }
    
    public String[] tokenizeWithOpenNLP(String text) throws IOException {
        Tokenizer tokenizer = new TokenizerME(tokenizeModel);
        return tokenizer.tokenize(text);
    }
    
    public List<String> tokenizeWithCustomPattern(String text, String pattern) {
        String[] tokens = text.split(pattern);
        return Arrays.asList(tokens);
    }
    
    public List<List<String>> sentenceTokenize(String text) {
        List<List<String>> sentences = new ArrayList<>();
        String[] sents = text.split("[.!?]");
        
        for (String sent : sents) {
            if (!sent.trim().isEmpty()) {
                String[] tokens = whitespaceTokenizer.tokenize(sent.trim());
                sentences.add(Arrays.asList(tokens));
            }
        }
        
        return sentences;
    }
    
    public Map<String, Integer> getTokenFrequency(String[] tokens) {
        Map<String, Integer> frequency = new HashMap<>();
        
        for (String token : tokens) {
            frequency.put(token, frequency.getOrDefault(token, 0) + 1);
        }
        
        return frequency;
    }
    
    public int getVocabularySize(String[] tokens) {
        Set<String> vocabulary = new HashSet<>(Arrays.asList(tokens));
        return vocabulary.size();
    }
}

public class TokenizerFactory {
    
    public static TokenizerService createTokenizer(String type) {
        return new TokenizerService();
    }
}
```

---

### 3. Stopwords Removal

**Objective:** Implement stopwords removal functionality with customizable stopword lists for different languages and domains.

**Project Structure:**
```
stopwords/
├── pom.xml
└── src/main/java/com/learning/nlppipeline/stopwords/
    ├── StopwordsRemover.java
    ├── StopwordsLoader.java
    └── StopwordsFilter.java
```

**Implementation:**

```java
package com.learning.nlppipeline.stopwords;

import java.util.*;

public class StopwordsRemover {
    
    private Set<String> stopwords;
    private boolean caseSensitive;
    
    public StopwordsRemover() {
        this.stopwords = new HashSet<>();
        this.caseSensitive = false;
        loadDefaultStopwords();
    }
    
    public StopwordsRemover(Set<String> stopwords) {
        this.stopwords = stopwords;
        this.caseSensitive = false;
    }
    
    private void loadDefaultStopwords() {
        stopwords.addAll(Arrays.asList(
            "a", "an", "and", "are", "as", "at", "be", "by", "for", "from",
            "has", "he", "in", "is", "it", "its", "of", "on", "that", "the",
            "to", "was", "were", "will", "with", "the", "this", "but", "they",
            "have", "had", "what", "when", "where", "who", "which", "why", "how"
        ));
    }
    
    public void loadStopwordsFromFile(String filePath) throws IOException {
        List<String> words = Files.readAllLines(Paths.get(filePath));
        stopwords.addAll(words);
    }
    
    public void addStopword(String word) {
        if (caseSensitive) {
            stopwords.add(word);
        } else {
            stopwords.add(word.toLowerCase());
        }
    }
    
    public void addStopwords(Collection<String> words) {
        if (caseSensitive) {
            stopwords.addAll(words);
        } else {
            words.forEach(w -> stopwords.add(w.toLowerCase()));
        }
    }
    
    public String[] removeStopwords(String[] tokens) {
        List<String> result = new ArrayList<>();
        
        for (String token : tokens) {
            String checkToken = caseSensitive ? token : token.toLowerCase();
            if (!stopwords.contains(checkToken)) {
                result.add(token);
            }
        }
        
        return result.toArray(new String[0]);
    }
    
    public List<String> filterTokens(List<String> tokens) {
        List<String> result = new ArrayList<>();
        
        for (String token : tokens) {
            String checkToken = caseSensitive ? token : token.toLowerCase();
            if (!stopwords.contains(checkToken)) {
                result.add(token);
            }
        }
        
        return result;
    }
    
    public Set<String> getStopwords() {
        return new HashSet<>(stopwords);
    }
    
    public int getStopwordsCount() {
        return stopwords.size();
    }
}

public class StopwordsFilter {
    
    private StopwordsRemover remover;
    private Map<String, StopwordsRemover> languageStopwords;
    
    public StopwordsFilter() {
        this.remover = new StopwordsRemover();
        this.languageStopwords = new HashMap<>();
        initializeLanguageStopwords();
    }
    
    private void initializeLanguageStopwords() {
        languageStopwords.put("en", new StopwordsRemover());
        
        Set<String> spanishStopwords = new HashSet<>(Arrays.asList(
            "el", "la", "los", "las", "un", "una", "de", "del", "en", "con",
            "por", "para", "que", "es", "son", "está", "está", "y", "o", "pero"
        ));
        languageStopwords.put("es", new StopwordsRemover(spanishStopwords));
        
        Set<String> frenchStopwords = new HashSet<>(Arrays.asList(
            "le", "la", "les", "un", "une", "des", "de", "du", "en", "avec",
            "pour", "que", "qui", "est", "sont", "et", "ou", "mais", "sur", "dans"
        ));
        languageStopwords.put("fr", new StopwordsRemover(frenchStopwords));
    }
    
    public String[] filterByLanguage(String[] tokens, String language) {
        StopwordsRemover langRemover = languageStopwords.getOrDefault(language, remover);
        return langRemover.removeStopwords(tokens);
    }
}
```

---

### 4. Stemming and Lemmatization

**Objective:** Implement stemming (Porter, Snowball) and lemmatization using Stanford CoreNLP for accurate word normalization.

**Project Structure:**
```
stemming-lemmatization/
├── pom.xml
└── src/main/java/com/learning/nlppipeline/morphology/
    ├── StemmerService.java
    ├── PorterStemmer.java
    ├── SnowballStemmer.java
    └── LemmatizerService.java
```

**Implementation:**

```java
package com.learning.nlppipeline.morphology;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import java.util.*;

public class StemmerService {
    
    private PorterStemmer porterStemmer;
    private SnowballStemmer snowballStemmer;
    
    public StemmerService() {
        this.porterStemmer = new PorterStemmer();
        this.snowballStemmer = new SnowballStemmer("english");
    }
    
    public String stemWithPorter(String word) {
        porterStemmer.setCurrent(word);
        porterStemmer.stem();
        return porterStemmer.getCurrent();
    }
    
    public String stemWithSnowball(String word) {
        snowballStemmer.setCurrent(word);
        snowballStemmer.stem();
        return snowballStemmer.getCurrent();
    }
    
    public String[] stemTokens(String[] tokens, String algorithm) {
        String[] stemmed = new String[tokens.length];
        
        for (int i = 0; i < tokens.length; i++) {
            if (algorithm.equals("porter")) {
                stemmed[i] = stemWithPorter(tokens[i]);
            } else if (algorithm.equals("snowball")) {
                stemmed[i] = stemWithSnowball(tokens[i]);
            } else {
                stemmed[i] = tokens[i];
            }
        }
        
        return stemmed;
    }
    
    public Map<String, String> stemDocument(String text, String algorithm) {
        String[] tokens = text.split("\\s+");
        String[] stemmed = stemTokens(tokens, algorithm);
        
        Map<String, String> mapping = new HashMap<>();
        for (int i = 0; i < tokens.length; i++) {
            mapping.put(tokens[i], stemmed[i]);
        }
        
        return mapping;
    }
}

public class LemmatizerService {
    
    private StanfordCoreNLP pipeline;
    
    public LemmatizerService() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
        props.setProperty("outputFormat", "text");
        this.pipeline = new StanfordCoreNLP(props);
    }
    
    public String lemmatize(String word, String posTag) {
        Annotation annotation = new Annotation(word);
        pipeline.annotate(annotation);
        
        List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        
        if (!tokens.isEmpty()) {
            return tokens.get(0).get(CoreAnnotations.LemmaAnnotation.class);
        }
        
        return word;
    }
    
    public String[] lemmatizeTokens(String[] tokens, String[] posTags) {
        String[] lemmas = new String[tokens.length];
        
        for (int i = 0; i < tokens.length; i++) {
            String pos = posTags != null && i < posTags.length ? posTags[i] : "NN";
            lemmas[i] = lemmatize(tokens[i], pos);
        }
        
        return lemmas;
    }
    
    public List<String> lemmatizeText(String text) {
        List<String> lemmas = new ArrayList<>();
        
        Annotation annotation = new Annotation(text);
        pipeline.annotate(annotation);
        
        List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        
        for (CoreLabel token : tokens) {
            lemmas.add(token.get(CoreAnnotations.LemmaAnnotation.class));
        }
        
        return lemmas;
    }
    
    public Map<String, String> getLemmaMapping(String text) {
        Map<String, String> mapping = new HashMap<>();
        
        Annotation annotation = new Annotation(text);
        pipeline.annotate(annotation);
        
        List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        
        for (CoreLabel token : tokens) {
            String word = token.get(CoreAnnotations.TextAnnotation.class);
            String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
            mapping.put(word, lemma);
        }
        
        return mapping;
    }
}

public class PorterStemmer {
    
    private String current;
    
    public void setCurrent(String word) {
        current = word.toLowerCase();
    }
    
    public String getCurrent() {
        return current;
    }
    
    public void stem() {
        if (current.length() <= 2) {
            return;
        }
        
        current = current.replaceAll("ing$", "");
        current = current.replaceAll("ed$", "");
        current = current.replaceAll("ness$", "");
        current = current.replaceAll("ly$", "");
        current = current.replaceAll("ment$", "");
    }
}

public class SnowballStemmer {
    
    private String current;
    private String language;
    
    public SnowballStemmer(String language) {
        this.language = language;
    }
    
    public void setCurrent(String word) {
        current = word.toLowerCase();
    }
    
    public String getCurrent() {
        return current;
    }
    
    public void stem() {
        if (language.equals("english")) {
            stemEnglish();
        }
    }
    
    private void stemEnglish() {
        current = current.replaceAll("ational$", "ate");
        current = current.replaceAll("tional$", "tion");
        current = current.replaceAll("enci$", "ence");
        current = current.replaceAll("anci$", "ance");
        current = current.replaceAll("izer$", "ize");
        current = current.replaceAll("isation$", "ize");
        current = current.replaceAll("ization$", "ize");
        current = current.replaceAll("ation$", "ate");
        current = current.replaceAll("ator$", "ate");
        current = current.replaceAll("alism$", "al");
        current = current.replaceAll("iveness$", "ive");
        current = current.replaceAll("fulness$", "ful");
        current = current.replaceAll("ousness$", "ous");
        current = current.replaceAll("aliti$", "al");
        current = current.replaceAll("iviti$", "ive");
        current = current.replaceAll("biliti$", "ble");
    }
}
```

---

### 5. POS Tagging Pipeline

**Objective:** Build a POS tagging pipeline using Stanford CoreNLP for accurate part-of-speech identification.

**Project Structure:**
```
pos-tagging/
├── pom.xml
└── src/main/java/com/learning/nlppipeline/pos/
    ├── POSTaggerService.java
    ├── POSTaggingPipeline.java
    └── POSAnalyzer.java
```

**Implementation:**

```java
package com.learning.nlppipeline.pos;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.*;
import java.util.*;

public class POSTaggerService {
    
    private StanfordCoreNLP pipeline;
    
    public POSTaggerService() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos");
        props.setProperty("pos.model", "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger");
        this.pipeline = new StanfordCoreNLP(props);
    }
    
    public String tagSentence(String sentence) {
        Annotation annotation = new Annotation(sentence);
        pipeline.annotate(annotation);
        
        List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        
        StringBuilder result = new StringBuilder();
        for (CoreLabel token : tokens) {
            result.append(token.get(CoreAnnotations.TextAnnotation.class))
                  .append("/")
                  .append(token.get(CoreAnnotations.PartOfSpeechAnnotation.class))
                  .append(" ");
        }
        
        return result.toString().trim();
    }
    
    public Map<String, String> getPosTags(String sentence) {
        Map<String, String> tags = new LinkedHashMap<>();
        
        Annotation annotation = new Annotation(sentence);
        pipeline.annotate(annotation);
        
        List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        
        for (CoreLabel token : tokens) {
            String word = token.get(CoreAnnotations.TextAnnotation.class);
            String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
            tags.put(word, pos);
        }
        
        return tags;
    }
    
    public List<TaggedToken> getTaggedTokens(String sentence) {
        List<TaggedToken> taggedTokens = new ArrayList<>();
        
        Annotation annotation = new Annotation(sentence);
        pipeline.annotate(annotation);
        
        List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        
        for (CoreLabel token : tokens) {
            TaggedToken tagged = new TaggedToken(
                token.get(CoreAnnotations.TextAnnotation.class),
                token.get(CoreAnnotations.PartOfSpeechAnnotation.class),
                token.get(CoreAnnotations.IndexAnnotation.class)
            );
            taggedTokens.add(tagged);
        }
        
        return taggedTokens;
    }
    
    public Map<String, List<String>> groupByPosTag(List<TaggedToken> tokens) {
        Map<String, List<String>> grouped = new HashMap<>();
        
        for (TaggedToken token : tokens) {
            grouped.computeIfAbsent(token.getTag(), k -> new ArrayList<>())
                   .add(token.getToken());
        }
        
        return grouped;
    }
    
    public String[] extractNouns(String sentence) {
        Map<String, String> tags = getPosTags(sentence);
        
        return tags.entrySet().stream()
            .filter(e -> e.getValue().startsWith("NN"))
            .map(Map.Entry::getKey)
            .toArray(String[]::new);
    }
    
    public String[] extractVerbs(String sentence) {
        Map<String, String> tags = getPosTags(sentence);
        
        return tags.entrySet().stream()
            .filter(e -> e.getValue().startsWith("VB"))
            .map(Map.Entry::getKey)
            .toArray(String[]::new);
    }
    
    public String[] extractAdjectives(String sentence) {
        Map<String, String> tags = getPosTags(sentence);
        
        return tags.entrySet().stream()
            .filter(e -> e.getValue().startsWith("JJ"))
            .map(Map.Entry::getKey)
            .toArray(String[]::new);
    }
}

public class TaggedToken {
    
    private String token;
    private String tag;
    private int index;
    
    public TaggedToken(String token, String tag, int index) {
        this.token = token;
        this.tag = tag;
        this.index = index;
    }
    
    public String getToken() { return token; }
    public String getTag() { return tag; }
    public int getIndex() { return index; }
    
    @Override
    public String toString() {
        return token + "/" + tag;
    }
}

public class POSAnalyzer {
    
    private POSTaggerService taggerService;
    
    public POSAnalyzer() {
        this.taggerService = new POSTaggerService();
    }
    
    public Map<String, Integer> analyzePosDistribution(String text) {
        Map<String, Integer> distribution = new HashMap<>();
        
        String[] sentences = text.split("[.!?]");
        
        for (String sentence : sentences) {
            if (!sentence.trim().isEmpty()) {
                Map<String, String> tags = taggerService.getPosTags(sentence.trim());
                
                for (String tag : tags.values()) {
                    distribution.put(tag, distribution.getOrDefault(tag, 0) + 1);
                }
            }
        }
        
        return distribution;
    }
    
    public double calculateNounRatio(String text) {
        int totalWords = 0;
        int nouns = 0;
        
        String[] sentences = text.split("[.!?]");
        
        for (String sentence : sentences) {
            if (!sentence.trim().isEmpty()) {
                Map<String, String> tags = taggerService.getPosTags(sentence.trim());
                totalWords += tags.size();
                
                for (String tag : tags.values()) {
                    if (tag.startsWith("NN")) {
                        nouns++;
                    }
                }
            }
        }
        
        return totalWords > 0 ? (double) nouns / totalWords : 0.0;
    }
}
```

---

### 6. Dependency Parsing

**Objective:** Implement dependency parsing to analyze grammatical structure and syntactic relationships between words.

**Project Structure:**
```
dependency-parsing/
├── pom.xml
└── src/main/java/com/learning/nlppipeline/parsing/
    ├── DependencyParserService.java
    ├── SyntaxTreeAnalyzer.java
    └── RelationExtractor.java
```

**Implementation:**

```java
package com.learning.nlppipeline.parsing;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.trees.*;
import java.util.*;

public class DependencyParserService {
    
    private StanfordCoreNLP pipeline;
    private GrammaticalStructureFactory gsFactory;
    
    public DependencyParserService() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse");
        props.setProperty("parse.model", "edu/stanford/nlp/models/parser/lexparser/englishPCFG.ser.gz");
        this.pipeline = new StanfordCoreNLP(props);
        
        this.gsFactory = new EnglishGrammaticalStructureFactory();
    }
    
    public List<DependencyRelation> parseDependencies(String sentence) {
        List<DependencyRelation> relations = new ArrayList<>();
        
        Annotation annotation = new Annotation(sentence);
        pipeline.annotate(annotation);
        
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        
        for (CoreMap sent : sentences) {
            Tree tree = sent.get(TreeCoreAnnotations.TreeAnnotation.class);
            GrammaticalStructure gs = gsFactory.newGrammaticalStructure(tree);
            
            Collection<TypedDependency> deps = gs.typedDependencies();
            
            for (TypedDependency dep : deps) {
                relations.add(new DependencyRelation(
                    dep.reln().toString(),
                    dep.gov().toString(),
                    dep.dep().toString()
                ));
            }
        }
        
        return relations;
    }
    
    public String getDependencyTree(String sentence) {
        Annotation annotation = new Annotation(sentence);
        pipeline.annotate(annotation);
        
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        
        if (!sentences.isEmpty()) {
            Tree tree = sentences.get(0).get(TreeCoreAnnotations.TreeAnnotation.class);
            return tree.toString();
        }
        
        return "";
    }
    
    public List<Phrase> extractPhrases(String sentence) {
        List<Phrase> phrases = new ArrayList<>();
        
        Annotation annotation = new Annotation(sentence);
        pipeline.annotate(annotation);
        
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        
        for (CoreMap sent : sentences) {
            Tree tree = sent.get(TreeCoreAnnotations.TreeAnnotation.class);
            
            for (Tree subtree : tree) {
                if (subtree.isPhrasal()) {
                    String label = subtree.label().value();
                    String phrase = subtree.yield().toString();
                    
                    if (!phrase.isEmpty()) {
                        phrases.add(new Phrase(label, phrase));
                    }
                }
            }
        }
        
        return phrases;
    }
    
    public String findSubject(String sentence) {
        List<DependencyRelation> deps = parseDependencies(sentence);
        
        for (DependencyRelation dep : deps) {
            if (dep.getRelation().equals("nsubj") || 
                dep.getRelation().equals("nsubjpass")) {
                return dep.getDependent();
            }
        }
        
        return "";
    }
    
    public String findRootVerb(String sentence) {
        List<DependencyRelation> deps = parseDependencies(sentence);
        
        for (DependencyRelation dep : deps) {
            if (dep.getRelation().equals("root")) {
                return dep.getDependent();
            }
        }
        
        return "";
    }
}

public class DependencyRelation {
    
    private String relation;
    private String governor;
    private String dependent;
    
    public DependencyRelation(String relation, String governor, String dependent) {
        this.relation = relation;
        this.governor = governor;
        this.dependent = dependent;
    }
    
    public String getRelation() { return relation; }
    public String getGovernor() { return governor; }
    public String getDependent() { return dependent; }
    
    @Override
    public String toString() {
        return relation + "(" + governor + ", " + dependent + ")";
    }
}

public class Phrase {
    
    private String label;
    private String text;
    
    public Phrase(String label, String text) {
        this.label = label;
        this.text = text;
    }
    
    public String getLabel() { return label; }
    public String getText() { return text; }
}
```

---

### 7. Named Entity Recognition Pipeline

**Objective:** Build an NER pipeline using Stanford CoreNLP and OpenNLP to identify and classify named entities.

**Project Structure:**
```
ner/
├── pom.xml
└── src/main/java/com/learning/nlppipeline/ner/
    ├── NERService.java
    ├── EntityExtractor.java
    └── NERPipeline.java
```

**Implementation:**

```java
package com.learning.nlppipeline.ner;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import opennlp.tools.namefind.*;
import opennlp.tools.util.*;
import java.util.*;

public class NERService {
    
    private StanfordCoreNLP stanfordPipeline;
    private TokenNameFinderModel openNLPModel;
    private boolean useStanford;
    
    public NERService() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
        props.setProperty("ner.model", "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
        this.stanfordPipeline = new StanfordCoreNLP(props);
        this.useStanford = true;
    }
    
    public NERService(String openNLPModelPath) throws IOException {
        this.openNLPModel = new TokenNameFinderModel(new File(openNLPModelPath));
        this.useStanford = false;
    }
    
    public List<NamedEntity> recognizeEntities(String text) {
        List<NamedEntity> entities = new ArrayList<>();
        
        if (useStanford) {
            return recognizeWithStanford(text);
        } else {
            return recognizeWithOpenNLP(text);
        }
    }
    
    private List<NamedEntity> recognizeWithStanford(String text) {
        List<NamedEntity> entities = new ArrayList<>();
        
        Annotation annotation = new Annotation(text);
        stanfordPipeline.annotate(annotation);
        
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        
        for (CoreMap sentence : sentences) {
            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
            
            String currentEntity = "";
            String currentType = "";
            
            for (CoreLabel token : tokens) {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                
                if (!ner.equals("O")) {
                    if (ner.equals(currentType)) {
                        currentEntity += " " + word;
                    } else {
                        if (!currentEntity.isEmpty()) {
                            entities.add(new NamedEntity(currentEntity.trim(), currentType));
                        }
                        currentEntity = word;
                        currentType = ner;
                    }
                } else {
                    if (!currentEntity.isEmpty()) {
                        entities.add(new NamedEntity(currentEntity.trim(), currentType));
                        currentEntity = "";
                        currentType = "";
                    }
                }
            }
            
            if (!currentEntity.isEmpty()) {
                entities.add(new NamedEntity(currentEntity.trim(), currentType));
            }
        }
        
        return entities;
    }
    
    private List<NamedEntity> recognizeWithOpenNLP(String text) {
        List<NamedEntity> entities = new ArrayList<>();
        
        try (NameFinderME finder = new NameFinderME(openNLPModel)) {
            String[] tokens = text.split("\\s+");
            Span[] spans = finder.find(tokens);
            
            for (Span span : spans) {
                StringBuilder entityText = new StringBuilder();
                for (int i = span.getStart(); i < span.getEnd(); i++) {
                    entityText.append(tokens[i]).append(" ");
                }
                
                entities.add(new NamedEntity(entityText.toString().trim(), span.getType()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return entities;
    }
    
    public Map<String, List<String>> groupEntitiesByType(String text) {
        List<NamedEntity> entities = recognizeEntities(text);
        Map<String, List<String>> grouped = new HashMap<>();
        
        for (NamedEntity entity : entities) {
            grouped.computeIfAbsent(entity.getType(), k -> new ArrayList<>())
                   .add(entity.getText());
        }
        
        return grouped;
    }
    
    public String extractPersonNames(String text) {
        List<NamedEntity> entities = recognizeEntities(text);
        
        return entities.stream()
            .filter(e -> e.getType().equals("PERSON"))
            .map(NamedEntity::getText)
            .reduce((a, b) -> a + ", " + b)
            .orElse("");
    }
    
    public String extractOrganizations(String text) {
        List<NamedEntity> entities = recognizeEntities(text);
        
        return entities.stream()
            .filter(e -> e.getType().equals("ORGANIZATION"))
            .map(NamedEntity::getText)
            .reduce((a, b) -> a + ", " + b)
            .orElse("");
    }
    
    public String extractLocations(String text) {
        List<NamedEntity> entities = recognizeEntities(text);
        
        return entities.stream()
            .filter(e -> e.getType().equals("LOCATION"))
            .map(NamedEntity::getText)
            .reduce((a, b) -> a + ", " + b)
            .orElse("");
    }
}

public class NamedEntity {
    
    private String text;
    private String type;
    
    public NamedEntity(String text, String type) {
        this.text = text;
        this.type = type;
    }
    
    public String getText() { return text; }
    public String getType() { return type; }
    
    @Override
    public String toString() {
        return type + ": " + text;
    }
}

public class NERPipeline {
    
    private NERService nerService;
    private TextPreprocessor preprocessor;
    
    public NERPipeline() {
        this.nerService = new NERService();
        this.preprocessor = new TextPreprocessor();
    }
    
    public List<NamedEntity> processText(String text) {
        String cleanedText = preprocessor.preprocess(text);
        return nerService.recognizeEntities(cleanedText);
    }
    
    public Map<String, Object> analyzeDocument(String document) {
        Map<String, Object> analysis = new HashMap<>();
        
        String[] sentences = document.split("[.!?]");
        
        List<NamedEntity> allEntities = new ArrayList<>();
        
        for (String sentence : sentences) {
            if (!sentence.trim().isEmpty()) {
                allEntities.addAll(processText(sentence.trim()));
            }
        }
        
        analysis.put("entities", allEntities);
        analysis.put("entityCount", allEntities.size());
        analysis.put("entityTypes", nerService.groupEntitiesByType(document));
        
        return analysis;
    }
}
```

---

### 8. Word Embeddings

**Objective:** Implement word embeddings including Word2Vec training and pre-trained embedding loading for semantic similarity.

**Project Structure:**
```
word-embeddings/
├── pom.xml
└── src/main/java/com/learning/nlppipeline/embeddings/
    ├── Word2VecModel.java
    ├── EmbeddingService.java
    └── SimilarityCalculator.java
```

**Implementation:**

```java
package com.learning.nlppipeline.embeddings;

import java.util.*;

public class Word2VecModel {
    
    private Map<String, double[]> wordVectors;
    private int vectorSize;
    private int windowSize;
    private int iterations;
    
    public Word2VecModel(int vectorSize, int windowSize) {
        this.wordVectors = new HashMap<>();
        this.vectorSize = vectorSize;
        this.windowSize = windowSize;
        this.iterations = 100;
    }
    
    public void train(List<String> sentences) {
        Map<String, List<String>> contextMap = buildContextMap(sentences);
        
        for (String word : contextMap.keySet()) {
            double[] vector = new double[vectorSize];
            List<String> context = contextMap.get(word);
            
            for (String contextWord : context) {
                if (wordVectors.containsKey(contextWord)) {
                    double[] contextVector = wordVectors.get(contextWord);
                    
                    for (int i = 0; i < vectorSize; i++) {
                        vector[i] += contextVector[i];
                    }
                }
            }
            
            if (!context.isEmpty()) {
                for (int i = 0; i < vectorSize; i++) {
                    vector[i] /= context.size();
                }
            }
            
            wordVectors.put(word, vector);
        }
    }
    
    private Map<String, List<String>> buildContextMap(List<String> sentences) {
        Map<String, List<String>> contextMap = new HashMap<>();
        
        for (String sentence : sentences) {
            String[] tokens = sentence.toLowerCase().split("\\s+");
            
            for (int i = 0; i < tokens.length; i++) {
                String word = tokens[i];
                
                if (!contextMap.containsKey(word)) {
                    contextMap.put(word, new ArrayList<>());
                }
                
                int start = Math.max(0, i - windowSize);
                int end = Math.min(tokens.length, i + windowSize + 1);
                
                for (int j = start; j < end; j++) {
                    if (j != i) {
                        contextMap.get(word).add(tokens[j]);
                    }
                }
            }
        }
        
        return contextMap;
    }
    
    public double[] getVector(String word) {
        return wordVectors.get(word.toLowerCase());
    }
    
    public boolean hasVector(String word) {
        return wordVectors.containsKey(word.toLowerCase());
    }
    
    public Set<String> getVocabulary() {
        return wordVectors.keySet();
    }
}

public class EmbeddingService {
    
    private Word2VecModel model;
    private Map<String, double[]> preloadedEmbeddings;
    
    public EmbeddingService() {
        this.preloadedEmbeddings = new HashMap<>();
        loadPredefinedEmbeddings();
    }
    
    private void loadPredefinedEmbeddings() {
        preloadedEmbeddings.put("king", new double[]{0.8, 0.9, 0.7, 0.6});
        preloadedEmbeddings.put("queen", new double[]{0.7, 0.9, 0.8, 0.5});
        preloadedEmbeddings.put("man", new double[]{0.9, 0.3, 0.4, 0.8});
        preloadedEmbeddings.put("woman", new double[]{0.8, 0.4, 0.3, 0.9});
        preloadedEmbeddings.put("prince", new double[]{0.6, 0.7, 0.6, 0.7});
        preloadedEmbeddings.put("princess", new double[]{0.5, 0.8, 0.7, 0.6});
        preloadedEmbeddings.put("apple", new double[]{0.2, 0.8, 0.9, 0.3});
        preloadedEmbeddings.put("orange", new double[]{0.3, 0.7, 0.8, 0.4});
    }
    
    public double[] getEmbedding(String word) {
        if (preloadedEmbeddings.containsKey(word.toLowerCase())) {
            return preloadedEmbeddings.get(word.toLowerCase());
        }
        
        return model != null ? model.getVector(word) : null;
    }
    
    public void setModel(Word2VecModel model) {
        this.model = model;
    }
    
    public double cosineSimilarity(double[] vec1, double[] vec2) {
        if (vec1 == null || vec2 == null || vec1.length != vec2.length) {
            return 0.0;
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            norm1 += vec1[i] * vec1[i];
            norm2 += vec2[i] * vec2[i];
        }
        
        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
    
    public List<String> findMostSimilar(String word, int topN) {
        double[] targetVector = getEmbedding(word);
        
        if (targetVector == null) {
            return Collections.emptyList();
        }
        
        List<SimilarityScore> scores = new ArrayList<>();
        
        for (Map.Entry<String, double[]> entry : preloadedEmbeddings.entrySet()) {
            if (!entry.getKey().equals(word.toLowerCase())) {
                double similarity = cosineSimilarity(targetVector, entry.getValue());
                scores.add(new SimilarityScore(entry.getKey(), similarity));
            }
        }
        
        scores.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        
        return scores.stream()
            .limit(topN)
            .map(SimilarityScore::getWord)
            .toList();
    }
}

public class SimilarityCalculator {
    
    private EmbeddingService embeddingService;
    
    public SimilarityCalculator() {
        this.embeddingService = new EmbeddingService();
    }
    
    public double calculateSimilarity(String word1, String word2) {
        double[] vec1 = embeddingService.getEmbedding(word1);
        double[] vec2 = embeddingService.getEmbedding(word2);
        
        return embeddingService.cosineSimilarity(vec1, vec2);
    }
    
    public double calculateWordSimilarity(String word1, String word2) {
        return calculateSimilarity(word1, word2);
    }
    
    public double[] getDocumentVector(String[] tokens) {
        double[] docVector = null;
        int count = 0;
        
        for (String token : tokens) {
            double[] wordVector = embeddingService.getEmbedding(token);
            
            if (wordVector != null) {
                if (docVector == null) {
                    docVector = new double[wordVector.length];
                }
                
                for (int i = 0; i < wordVector.length; i++) {
                    docVector[i] += wordVector[i];
                }
                count++;
            }
        }
        
        if (count > 0 && docVector != null) {
            for (int i = 0; i < docVector.length; i++) {
                docVector[i] /= count;
            }
        }
        
        return docVector;
    }
}

class SimilarityScore {
    
    private String word;
    private double score;
    
    public SimilarityScore(String word, double score) {
        this.word = word;
        this.score = score;
    }
    
    public String getWord() { return word; }
    public double getScore() { return score; }
}
```

---

### 9. Sentence Embeddings

**Objective:** Implement sentence-level embeddings for document representation and semantic similarity.

**Project Structure:**
```
sentence-embeddings/
├── pom.xml
└── src/main/java/com/learning/nlppipeline/sentence/
    ├── SentenceEncoder.java
    ├── DocumentVectorizer.java
    └── SemanticSearch.java
```

**Implementation:**

```java
package com.learning.nlppipeline.sentence;

import java.util.*;

public class SentenceEncoder {
    
    private Map<String, double[]> wordEmbeddings;
    private int embeddingSize;
    
    public SentenceEncoder() {
        this.embeddingSize = 384;
        this.wordEmbeddings = new HashMap<>();
        initializeWordEmbeddings();
    }
    
    private void initializeWordEmbeddings() {
        wordEmbeddings.put("the", new double[embeddingSize]);
        wordEmbeddings.put("quick", new double[embeddingSize]);
        wordEmbeddings.put("brown", new double[embeddingSize]);
        wordEmbeddings.put("fox", new double[embeddingSize]);
        wordEmbeddings.put("jumps", new double[embeddingSize]);
        wordEmbeddings.put("over", new double[embeddingSize]);
        wordEmbeddings.put("lazy", new double[embeddingSize]);
        wordEmbeddings.put("dog", new double[embeddingSize]);
        
        for (int i = 0; i < embeddingSize; i++) {
            wordEmbeddings.get("the")[i] = Math.random() * 0.1;
        }
        
        for (int i = 0; i < embeddingSize; i++) {
            wordEmbeddings.get("quick")[i] = 0.8 + Math.random() * 0.1;
            wordEmbeddings.get("brown")[i] = 0.7 + Math.random() * 0.1;
            wordEmbeddings.get("fox")[i] = 0.9 + Math.random() * 0.1;
        }
    }
    
    public double[] encode(String sentence) {
        String[] tokens = sentence.toLowerCase().split("\\s+");
        
        double[] sentenceVector = new double[embeddingSize];
        int validTokens = 0;
        
        for (String token : tokens) {
            String cleanedToken = token.replaceAll("[^a-z]", "");
            
            if (wordEmbeddings.containsKey(cleanedToken)) {
                double[] wordVec = wordEmbeddings.get(cleanedToken);
                
                for (int i = 0; i < embeddingSize; i++) {
                    sentenceVector[i] += wordVec[i];
                }
                validTokens++;
            }
        }
        
        if (validTokens > 0) {
            for (int i = 0; i < embeddingSize; i++) {
                sentenceVector[i] /= validTokens;
            }
        }
        
        return sentenceVector;
    }
    
    public double[] encodeWithWeighting(String sentence) {
        String[] tokens = sentence.toLowerCase().split("\\s+");
        
        double[] sentenceVector = new double[embeddingSize];
        Map<String, Integer> tf = calculateTF(tokens);
        
        for (Map.Entry<String, Integer> entry : tf.entrySet()) {
            String word = entry.getKey();
            int frequency = entry.getValue();
            
            if (wordEmbeddings.containsKey(word)) {
                double[] wordVec = wordEmbeddings.get(word);
                double weight = (1 + Math.log(frequency));
                
                for (int i = 0; i < embeddingSize; i++) {
                    sentenceVector[i] += weight * wordVec[i];
                }
            }
        }
        
        return sentenceVector;
    }
    
    private Map<String, Integer> calculateTF(String[] tokens) {
        Map<String, Integer> tf = new HashMap<>();
        
        for (String token : tokens) {
            String cleaned = token.replaceAll("[^a-z]", "");
            if (!cleaned.isEmpty()) {
                tf.put(cleaned, tf.getOrDefault(cleaned, 0) + 1);
            }
        }
        
        return tf;
    }
    
    public double computeSimilarity(double[] vec1, double[] vec2) {
        if (vec1 == null || vec2 == null || vec1.length != vec2.length) {
            return 0.0;
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            norm1 += vec1[i] * vec1[i];
            norm2 += vec2[i] * vec2[i];
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}

public class DocumentVectorizer {
    
    private SentenceEncoder encoder;
    
    public DocumentVectorizer() {
        this.encoder = new SentenceEncoder();
    }
    
    public double[] vectorizeDocument(String document) {
        String[] sentences = document.split("[.!?]");
        
        double[] docVector = new double[encoder.encode("").length];
        int validSentences = 0;
        
        for (String sentence : sentences) {
            if (!sentence.trim().isEmpty()) {
                double[] sentVector = encoder.encode(sentence.trim());
                
                for (int i = 0; i < docVector.length; i++) {
                    docVector[i] += sentVector[i];
                }
                validSentences++;
            }
        }
        
        if (validSentences > 0) {
            for (int i = 0; i < docVector.length; i++) {
                docVector[i] /= validSentences;
            }
        }
        
        return docVector;
    }
    
    public List<DocumentVector> vectorizeCorpus(List<String> documents) {
        List<DocumentVector> vectors = new ArrayList<>();
        
        for (int i = 0; i < documents.size(); i++) {
            double[] vector = vectorizeDocument(documents.get(i));
            vectors.add(new DocumentVector(i, vector));
        }
        
        return vectors;
    }
    
    public double computeDocumentSimilarity(String doc1, String doc2) {
        double[] vec1 = vectorizeDocument(doc1);
        double[] vec2 = vectorizeDocument(doc2);
        
        return encoder.computeSimilarity(vec1, vec2);
    }
}

public class SemanticSearch {
    
    private List<DocumentVector> indexedDocuments;
    private SentenceEncoder encoder;
    
    public SemanticSearch() {
        this.indexedDocuments = new ArrayList<>();
        this.encoder = new SentenceEncoder();
    }
    
    public void indexDocument(String id, String content) {
        double[] vector = encoder.encode(content);
        indexedDocuments.add(new DocumentVector(id, vector));
    }
    
    public List<SearchResult> search(String query, int topK) {
        double[] queryVector = encoder.encode(query);
        
        List<SearchResult> results = new ArrayList<>();
        
        for (DocumentVector doc : indexedDocuments) {
            double similarity = encoder.computeSimilarity(queryVector, doc.getVector());
            results.add(new SearchResult(doc.getId(), similarity));
        }
        
        results.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        
        return results.stream().limit(topK).toList();
    }
    
    public int getIndexedCount() {
        return indexedDocuments.size();
    }
}

class DocumentVector {
    
    private String id;
    private double[] vector;
    
    public DocumentVector(String id, double[] vector) {
        this.id = id;
        this.vector = vector;
    }
    
    public String getId() { return id; }
    public double[] getVector() { return vector; }
}

class SearchResult {
    
    private String documentId;
    private double score;
    
    public SearchResult(String documentId, double score) {
        this.documentId = documentId;
        this.score = score;
    }
    
    public String getId() { return documentId; }
    public double getScore() { return score; }
}
```

---

### 10. Feature Extraction

**Objective:** Implement various text feature extraction methods including TF-IDF, bag-of-words, and n-grams.

**Project Structure:**
```
feature-extraction/
├── pom.xml
└── src/main/java/com/learning/nlppipeline/features/
    ├── TFIDFCalculator.java
    ├── BagOfWordsExtractor.java
    ├── NGramExtractor.java
    └── FeatureVectorBuilder.java
```

**Implementation:**

```java
package com.learning.nlppipeline.features;

import java.util.*;

public class TFIDFCalculator {
    
    private Map<String, Integer> documentFrequency;
    private int totalDocuments;
    
    public TFIDFCalculator() {
        this.documentFrequency = new HashMap<>();
        this.totalDocuments = 0;
    }
    
    public void addDocument(String document) {
        String[] tokens = tokenize(document);
        Set<String> uniqueTokens = new HashSet<>(Arrays.asList(tokens));
        
        for (String token : uniqueTokens) {
            documentFrequency.put(token, documentFrequency.getOrDefault(token, 0) + 1);
        }
        
        totalDocuments++;
    }
    
    private String[] tokenize(String text) {
        return text.toLowerCase().split("\\s+");
    }
    
    public double calculateTF(String term, String document) {
        String[] tokens = tokenize(document);
        
        int termCount = 0;
        for (String token : tokens) {
            if (token.equals(term)) {
                termCount++;
            }
        }
        
        return (double) termCount / tokens.length;
    }
    
    public double calculateIDF(String term) {
        int df = documentFrequency.getOrDefault(term, 0);
        
        if (df == 0) {
            return 0.0;
        }
        
        return Math.log((double) totalDocuments / df);
    }
    
    public double calculateTFIDF(String term, String document) {
        double tf = calculateTF(term, document);
        double idf = calculateIDF(term);
        
        return tf * idf;
    }
    
    public Map<String, Double> getTFIDFVector(String document) {
        String[] tokens = tokenize(document);
        Set<String> uniqueTerms = new HashSet<>(Arrays.asList(tokens));
        
        Map<String, Double> tfidfVector = new HashMap<>();
        
        for (String term : uniqueTerms) {
            tfidfVector.put(term, calculateTFIDF(term, document));
        }
        
        return tfidfVector;
    }
    
    public List<Map<String, Double>> computeTFIDFForCorpus(List<String> documents) {
        for (String doc : documents) {
            addDocument(doc);
        }
        
        List<Map<String, Double>> vectors = new ArrayList<>();
        
        for (String doc : documents) {
            vectors.add(getTFIDFVector(doc));
        }
        
        return vectors;
    }
}

public class BagOfWordsExtractor {
    
    private Set<String> vocabulary;
    
    public BagOfWordsExtractor() {
        this.vocabulary = new HashSet<>();
    }
    
    public void buildVocabulary(List<String> documents) {
        for (String doc : documents) {
            String[] tokens = tokenize(doc);
            vocabulary.addAll(Arrays.asList(tokens));
        }
    }
    
    private String[] tokenize(String text) {
        return text.toLowerCase().split("\\s+");
    }
    
    public int[] extractBowVector(String document) {
        String[] tokens = tokenize(document);
        
        int[] vector = new int[vocabulary.size()];
        
        int index = 0;
        for (String term : vocabulary) {
            int count = 0;
            
            for (String token : tokens) {
                if (term.equals(token)) {
                    count++;
                }
            }
            
            vector[index] = count;
            index++;
        }
        
        return vector;
    }
    
    public Map<String, Integer> extractBowMap(String document) {
        String[] tokens = tokenize(document);
        Map<String, Integer> bow = new HashMap<>();
        
        for (String token : tokens) {
            bow.put(token, bow.getOrDefault(token, 0) + 1);
        }
        
        return bow;
    }
    
    public int getVocabularySize() {
        return vocabulary.size();
    }
    
    public Set<String> getVocabulary() {
        return new HashSet<>(vocabulary);
    }
}

public class NGramExtractor {
    
    private int n;
    
    public NGramExtractor(int n) {
        this.n = n;
    }
    
    public List<String> extractNgrams(String text) {
        String[] tokens = tokenize(text);
        List<String> ngrams = new ArrayList<>();
        
        if (tokens.length < n) {
            return ngrams;
        }
        
        for (int i = 0; i <= tokens.length - n; i++) {
            StringBuilder ngram = new StringBuilder();
            
            for (int j = 0; j < n; j++) {
                if (j > 0) {
                    ngram.append(" ");
                }
                ngram.append(tokens[i + j]);
            }
            
            ngrams.add(ngram.toString());
        }
        
        return ngrams;
    }
    
    public Map<String, Integer> getNgramFrequency(String text) {
        List<String> ngrams = extractNgrams(text);
        Map<String, Integer> frequency = new HashMap<>();
        
        for (String ngram : ngrams) {
            frequency.put(ngram, frequency.getOrDefault(ngram, 0) + 1);
        }
        
        return frequency;
    }
    
    private String[] tokenize(String text) {
        return text.toLowerCase().split("\\s+");
    }
}

public class FeatureVectorBuilder {
    
    private TFIDFCalculator tfidfCalculator;
    private BagOfWordsExtractor bowExtractor;
    private NGramExtractor unigramExtractor;
    private NGramExtractor bigramExtractor;
    
    public FeatureVectorBuilder() {
        this.tfidfCalculator = new TFIDFCalculator();
        this.bowExtractor = new BagOfWordsExtractor();
        this.unigramExtractor = new NGramExtractor(1);
        this.bigramExtractor = new NGramExtractor(2);
    }
    
    public Map<String, Object> extractAllFeatures(String text) {
        Map<String, Object> features = new HashMap<>();
        
        features.put("bow", bowExtractor.extractBowMap(text));
        features.put("unigrams", unigramExtractor.getNgramFrequency(text));
        features.put("bigrams", bigramExtractor.getNgramFrequency(text));
        
        return features;
    }
    
    public double[] buildFeatureVector(String document, List<String> vocabulary) {
        String[] tokens = document.toLowerCase().split("\\s+");
        
        double[] vector = new double[vocabulary.size()];
        
        for (int i = 0; i < vocabulary.size(); i++) {
            String term = vocabulary.get(i);
            int count = 0;
            
            for (String token : tokens) {
                if (term.equals(token)) {
                    count++;
                }
            }
            
            vector[i] = count;
        }
        
        return vector;
    }
}
```

---

## Real-World Projects

### 1. Email Classification System

**Objective:** Build an email classification system that categorizes incoming emails into predefined categories using NLP pipelines.

**Project Structure:**
```
email-classifier/
├── pom.xml
└── src/main/java/com/learning/nlppipeline/email/
    ├── EmailClassifierApplication.java
    ├── controller/EmailController.java
    ├── service/EmailClassificationService.java
    ├── model/Email.java
    ├── pipeline/EmailProcessingPipeline.java
    └── classifier/TextClassifier.java
```

**Implementation:**

```java
package com.learning.nlppipeline.email;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.*;
import java.util.*;
import java.util.regex.*;

public class EmailProcessingPipeline {
    
    private TextPreprocessor preprocessor;
    private POSTaggerService posTagger;
    private NERService nerService;
    private TFIDFCalculator tfidfCalculator;
    private Map<String, double[]> categoryProfiles;
    
    public EmailProcessingPipeline() {
        this.preprocessor = new TextPreprocessor();
        this.posTagger = new POSTaggerService();
        this.nerService = new NERService();
        this.tfidfCalculator = new TFIDFCalculator();
        this.categoryProfiles = new HashMap<>();
        initializeCategoryProfiles();
    }
    
    private void initializeCategoryProfiles() {
        categoryProfiles.put("work", new double[]{0.8, 0.1, 0.05, 0.05});
        categoryProfiles.put("personal", new double[]{0.2, 0.7, 0.05, 0.05});
        categoryProfiles.put("spam", new double[]{0.1, 0.1, 0.8, 0.0});
        categoryProfiles.put("promotional", new double[]{0.1, 0.1, 0.2, 0.6});
    }
    
    public EmailAnalysisResult processEmail(String subject, String body) {
        EmailAnalysisResult result = new EmailAnalysisResult();
        
        String fullText = subject + " " + body;
        
        String cleanedText = preprocessor.normalizeText(fullText);
        result.setCleanedText(cleanedText);
        
        Map<String, String> posTags = posTagger.getPosTags(fullText);
        result.setPosTags(posTags);
        
        List<NamedEntity> entities = nerService.recognizeEntities(fullText);
        result.setEntities(entities);
        
        Map<String, Double> tfidf = tfidfCalculator.getTFIDFVector(cleanedText);
        result.setTfidfScores(tfidf);
        
        String category = classifyEmail(fullText, cleanedText);
        result.setCategory(category);
        
        result.setUrgency(extractUrgency(fullText));
        
        return result;
    }
    
    private String classifyEmail(String rawText, String cleanedText) {
        String[] tokens = cleanedText.split("\\s+");
        
        double[] workScore = calculateFeatureScore(tokens, new String[]{
            "meeting", "project", "deadline", "report", "team", "task", "client"
        });
        
        double[] personalScore = calculateFeatureScore(tokens, new String[]{
            "family", "friend", "home", "weekend", "birthday", "love"
        });
        
        double[] spamScore = calculateFeatureScore(tokens, new String[]{
            "free", "winner", "offer", "click", "subscribe", "prize", "winner"
        });
        
        double[] promoScore = calculateFeatureScore(tokens, new String[]{
            "sale", "discount", "offer", "shop", "buy", "deal", "promo"
        });
        
        double maxScore = Math.max(Math.max(workScore[0], personalScore[0]),
                                   Math.max(spamScore[0], promoScore[0]));
        
        if (maxScore == workScore[0]) return "work";
        if (maxScore == personalScore[0]) return "personal";
        if (maxScore == spamScore[0]) return "spam";
        return "promotional";
    }
    
    private double[] calculateFeatureScore(String[] tokens, String[] keywords) {
        double score = 0.0;
        
        for (String token : tokens) {
            for (String keyword : keywords) {
                if (token.toLowerCase().contains(keyword)) {
                    score += 1.0;
                }
            }
        }
        
        return new double[]{score / tokens.length};
    }
    
    private String extractUrgency(String text) {
        String[] urgentKeywords = {"urgent", "asap", "immediately", "critical", "important"};
        String[] mediumKeywords = {"soon", "needed", "please", "deadline"};
        
        text = text.toLowerCase();
        
        for (String keyword : urgentKeywords) {
            if (text.contains(keyword)) {
                return "high";
            }
        }
        
        for (String keyword : mediumKeywords) {
            if (text.contains(keyword)) {
                return "medium";
            }
        }
        
        return "low";
    }
    
    public void trainWithLabeledData(List<LabeledEmail> emails) {
        for (LabeledEmail email : emails) {
            tfidfCalculator.addDocument(email.getText());
        }
    }
}

class EmailAnalysisResult {
    
    private String cleanedText;
    private Map<String, String> posTags;
    private List<NamedEntity> entities;
    private Map<String, Double> tfidfScores;
    private String category;
    private String urgency;
    
    public String getCleanedText() { return cleanedText; }
    public void setCleanedText(String cleanedText) { this.cleanedText = cleanedText; }
    public Map<String, String> getPosTags() { return posTags; }
    public void setPosTags(Map<String, String> posTags) { this.posTags = posTags; }
    public List<NamedEntity> getEntities() { return entities; }
    public void setEntities(List<NamedEntity> entities) { this.entities = entities; }
    public Map<String, Double> getTfidfScores() { return tfidfScores; }
    public void setTfidfScores(Map<String, Double> tfidfScores) { this.tfidfScores = tfidfScores; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }
}

class LabeledEmail {
    
    private String text;
    private String label;
    
    public LabeledEmail(String text, String label) {
        this.text = text;
        this.label = label;
    }
    
    public String getText() { return text; }
    public String getLabel() { return label; }
}

class TextClassifier {
    
    private Map<String, double[]> trainedProfiles;
    
    public TextClassifier() {
        this.trainedProfiles = new HashMap<>();
    }
    
    public void train(String label, double[] features) {
        trainedProfiles.put(label, features);
    }
    
    public String predict(double[] features) {
        String bestLabel = null;
        double bestScore = Double.MIN_VALUE;
        
        for (Map.Entry<String, double[]> entry : trainedProfiles.entrySet()) {
            double similarity = cosineSimilarity(features, entry.getValue());
            
            if (similarity > bestScore) {
                bestScore = similarity;
                bestLabel = entry.getKey();
            }
        }
        
        return bestLabel;
    }
    
    private double cosineSimilarity(double[] a, double[] b) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        
        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
```

---

### 2. Customer Feedback Analyzer

**Objective:** Build a system to analyze customer feedback, extract sentiment, topics, and key entities for business insights.

**Project Structure:**
```
feedback-analyzer/
├── pom.xml
└── src/main/java/com/learning/nlppipeline/feedback/
    ├── FeedbackAnalyzerApplication.java
    ├── service/FeedbackAnalysisService.java
    ├── analyzer/SentimentAnalyzer.java
    ├── analyzer/TopicExtractor.java
    └── model/FeedbackAnalysis.java
```

**Implementation:**

```java
package com.learning.nlppipeline.feedback;

import java.util.*;

public class FeedbackAnalysisService {
    
    private TextPreprocessor preprocessor;
    private NERService nerService;
    private POSTaggerService posTagger;
    private SentimentAnalyzer sentimentAnalyzer;
    private TopicExtractor topicExtractor;
    
    public FeedbackAnalysisService() {
        this.preprocessor = new TextPreprocessor();
        this.nerService = new NERService();
        this.posTagger = new POSTaggerService();
        this.sentimentAnalyzer = new SentimentAnalyzer();
        this.topicExtractor = new TopicExtractor();
    }
    
    public FeedbackAnalysis analyzeFeedback(String feedback) {
        FeedbackAnalysis analysis = new FeedbackAnalysis();
        
        String cleanedText = preprocessor.normalizeText(feedback);
        analysis.setCleanedText(cleanedText);
        
        SentimentResult sentiment = sentimentAnalyzer.analyze(feedback);
        analysis.setSentiment(sentiment);
        
        List<String> topics = topicExtractor.extractTopics(feedback);
        analysis.setTopics(topics);
        
        List<NamedEntity> entities = nerService.recognizeEntities(feedback);
        analysis.setEntities(entities);
        
        Map<String, String> aspects = extractAspects(feedback, sentiment);
        analysis.setAspectSentiments(aspects);
        
        List<String> keywords = extractKeywords(feedback);
        analysis.setKeywords(keywords);
        
        analysis.setUrgency(detectUrgency(feedback));
        
        return analysis;
    }
    
    private Map<String, String> extractAspects(String text, SentimentResult sentiment) {
        Map<String, String> aspects = new HashMap<>();
        
        String[] aspectKeywords = {
            "product", "service", "quality", "price", "delivery", 
            "support", "website", "app", "experience", "staff"
        };
        
        String lowerText = text.toLowerCase();
        
        for (String aspect : aspectKeywords) {
            if (lowerText.contains(aspect)) {
                if (sentiment.getScore() > 0.3) {
                    aspects.put(aspect, "positive");
                } else if (sentiment.getScore() < -0.3) {
                    aspects.put(aspect, "negative");
                } else {
                    aspects.put(aspect, "neutral");
                }
            }
        }
        
        return aspects;
    }
    
    private List<String> extractKeywords(String text) {
        Map<String, String> posTags = posTagger.getPosTags(text);
        
        List<String> keywords = new ArrayList<>();
        
        for (Map.Entry<String, String> entry : posTags.entrySet()) {
            if (entry.getValue().startsWith("JJ") || entry.getValue().startsWith("NN")) {
                if (entry.getKey().length() > 3) {
                    keywords.add(entry.getKey());
                }
            }
        }
        
        return keywords.stream().distinct().limit(10).toList();
    }
    
    private String detectUrgency(String text) {
        String[] urgentPhrases = {
            "terrible", "worst", "refund", "complaint", "never again",
            "very disappointed", "unacceptable", "horrible experience"
        };
        
        String lowerText = text.toLowerCase();
        
        for (String phrase : urgentPhrases) {
            if (lowerText.contains(phrase)) {
                return "high";
            }
        }
        
        return "low";
    }
    
    public Map<String, Integer> aggregateFeedbackBySentiment(List<String> feedbacks) {
        Map<String, Integer> aggregated = new HashMap<>();
        
        aggregated.put("positive", 0);
        aggregated.put("negative", 0);
        aggregated.put("neutral", 0);
        
        for (String feedback : feedbacks) {
            SentimentResult result = sentimentAnalyzer.analyze(feedback);
            
            if (result.getScore() > 0.2) {
                aggregated.put("positive", aggregated.get("positive") + 1);
            } else if (result.getScore() < -0.2) {
                aggregated.put("negative", aggregated.get("negative") + 1);
            } else {
                aggregated.put("neutral", aggregated.get("neutral") + 1);
            }
        }
        
        return aggregated;
    }
}

class SentimentAnalyzer {
    
    private Set<String> positiveWords;
    private Set<String> negativeWords;
    
    public SentimentAnalyzer() {
        this.positiveWords = new HashSet<>(Arrays.asList(
            "good", "great", "excellent", "amazing", "wonderful", "fantastic",
            "love", "perfect", "best", "awesome", "happy", "satisfied", "helpful"
        ));
        
        this.negativeWords = new HashSet<>(Arrays.asList(
            "bad", "terrible", "horrible", "worst", "poor", "disappointing",
            "hate", "awful", "wrong", "failed", "disappointed", "angry", "frustrated"
        ));
    }
    
    public SentimentResult analyze(String text) {
        String[] tokens = text.toLowerCase().split("\\s+");
        
        int positiveCount = 0;
        int negativeCount = 0;
        
        for (String token : tokens) {
            if (positiveWords.contains(token)) {
                positiveCount++;
            }
            if (negativeWords.contains(token)) {
                negativeCount++;
            }
        }
        
        int total = positiveCount + negativeCount;
        
        double score = total > 0 ? 
            (double) (positiveCount - negativeCount) / total : 0.0;
        
        String label = score > 0.2 ? "positive" : 
                      (score < -0.2 ? "negative" : "neutral");
        
        return new SentimentResult(score, label);
    }
}

class SentimentResult {
    
    private double score;
    private String label;
    
    public SentimentResult(double score, String label) {
        this.score = score;
        this.label = label;
    }
    
    public double getScore() { return score; }
    public String getLabel() { return label; }
}

class TopicExtractor {
    
    public List<String> extractTopics(String text) {
        List<String> topics = new ArrayList<>();
        
        String[] topicPatterns = {
            "product quality", "customer service", "delivery time", "price",
            "website", "mobile app", "user experience", "features", "support"
        };
        
        String lowerText = text.toLowerCase();
        
        for (String topic : topicPatterns) {
            if (lowerText.contains(topic)) {
                topics.add(topic);
            }
        }
        
        return topics;
    }
}

class FeedbackAnalysis {
    
    private String cleanedText;
    private SentimentResult sentiment;
    private List<String> topics;
    private List<NamedEntity> entities;
    private Map<String, String> aspectSentiments;
    private List<String> keywords;
    private String urgency;
    
    public String getCleanedText() { return cleanedText; }
    public void setCleanedText(String cleanedText) { this.cleanedText = cleanedText; }
    public SentimentResult getSentiment() { return sentiment; }
    public void setSentiment(SentimentResult sentiment) { this.sentiment = sentiment; }
    public List<String> getTopics() { return topics; }
    public void setTopics(List<String> topics) { this.topics = topics; }
    public List<NamedEntity> getEntities() { return entities; }
    public void setEntities(List<NamedEntity> entities) { this.entities = entities; }
    public Map<String, String> getAspectSentiments() { return aspectSentiments; }
    public void setAspectSentiments(Map<String, String> aspectSentiments) { this.aspectSentiments = aspectSentiments; }
    public List<String> getKeywords() { return keywords; }
    public void setKeywords(List<String> keywords) { this.keywords = keywords; }
    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }
}
```

---

### 3. Chatbot NLP Pipeline

**Objective:** Build an NLP pipeline for a chatbot that processes user messages, extracts intent, entities, and generates appropriate responses.

**Project Structure:**
```
chatbot-nlp/
├── pom.xml
└── src/main/java/com/learning/nlppipeline/chatbot/
    ├── ChatbotApplication.java
    ├── pipeline/ChatbotPipeline.java
    ├── intent/IntentClassifier.java
    ├── entity/EntityExtractor.java
    └── response/ResponseGenerator.java
```

**Implementation:**

```java
package com.learning.nlppipeline.chatbot;

import java.util.*;

public class ChatbotPipeline {
    
    private TextPreprocessor preprocessor;
    private IntentClassifier intentClassifier;
    private EntityExtractor entityExtractor;
    private ResponseGenerator responseGenerator;
    private NERService nerService;
    
    public ChatbotPipeline() {
        this.preprocessor = new TextPreprocessor();
        this.intentClassifier = new IntentClassifier();
        this.entityExtractor = new EntityExtractor();
        this.responseGenerator = new ResponseGenerator();
        this.nerService = new NERService();
        
        initializeIntents();
    }
    
    private void initializeIntents() {
        intentClassifier.addIntentPattern("greeting", 
            new String[]{"hello", "hi", "hey", "greetings", "good morning"});
        
        intentClassifier.addIntentPattern("goodbye",
            new String[]{"bye", "goodbye", "see you", "later", "farewell"});
        
        intentClassifier.addIntentPattern("help",
            new String[]{"help", "assistance", "support", "can you help"});
        
        intentClassifier.addIntentPattern("information",
            new String[]{"what", "how", "tell me", "explain", "describe"});
        
        intentClassifier.addIntentPattern("order_status",
            new String[]{"order", "status", "track", "delivery", "shipping"});
        
        intentClassifier.addIntentPattern("refund",
            new String[]{"refund", "money back", "return", "cancel"});
    }
    
    public ChatbotResponse processMessage(String userMessage) {
        ChatbotResponse response = new ChatbotResponse();
        
        String cleanedMessage = preprocessor.normalizeText(userMessage);
        response.setCleanedMessage(cleanedMessage);
        
        String intent = intentClassifier.classify(cleanedMessage);
        response.setIntent(intent);
        
        List<ExtractedEntity> entities = entityExtractor.extract(cleanedMessage);
        response.setEntities(entities);
        
        List<NamedEntity> nerEntities = nerService.recognizeEntities(userMessage);
        response.setNerEntities(nerEntities);
        
        String reply = responseGenerator.generateResponse(intent, entities);
        response.setReply(reply);
        
        response.setConfidence(calculateConfidence(intent, entities));
        
        return response;
    }
    
    private double calculateConfidence(String intent, List<ExtractedEntity> entities) {
        double baseConfidence = 0.8;
        
        if (entities.size() > 0) {
            baseConfidence += 0.1;
        }
        
        return Math.min(baseConfidence, 1.0);
    }
    
    public void trainWithConversations(List<Conversation> conversations) {
        for (Conversation conv : conversations) {
            String intent = intentClassifier.classify(conv.getUserMessage());
            
            if (!intent.equals("unknown")) {
                responseGenerator.registerResponse(intent, conv.getBotResponse());
            }
        }
    }
}

class IntentClassifier {
    
    private Map<String, String[]> intentPatterns;
    private Map<String, Integer> intentCounts;
    
    public IntentClassifier() {
        this.intentPatterns = new HashMap<>();
        this.intentCounts = new HashMap<>();
    }
    
    public void addIntentPattern(String intent, String[] patterns) {
        intentPatterns.put(intent, patterns);
    }
    
    public String classify(String message) {
        String lowerMessage = message.toLowerCase();
        
        int maxMatches = 0;
        String bestIntent = "unknown";
        
        for (Map.Entry<String, String[]> entry : intentPatterns.entrySet()) {
            int matches = 0;
            
            for (String pattern : entry.getValue()) {
                if (lowerMessage.contains(pattern.toLowerCase())) {
                    matches++;
                }
            }
            
            if (matches > maxMatches) {
                maxMatches = matches;
                bestIntent = entry.getKey();
            }
        }
        
        return maxMatches > 0 ? bestIntent : "general";
    }
    
    public void recordIntent(String message, String intent) {
        intentCounts.put(intent, intentCounts.getOrDefault(intent, 0) + 1);
    }
}

class EntityExtractor {
    
    public List<ExtractedEntity> extract(String message) {
        List<ExtractedEntity> entities = new ArrayList<>();
        
        extractPatterns(entities, message);
        
        return entities;
    }
    
    private void extractPatterns(List<ExtractedEntity> entities, String message) {
        extractOrderNumber(entities, message);
        extractDate(entities, message);
        extractProductName(entities, message);
        extractEmail(entities, message);
    }
    
    private void extractOrderNumber(List<ExtractedEntity> entities, String message) {
        java.util.regex.Pattern pattern = 
            java.util.regex.Pattern.compile("\\b(ORD|ORDER)[-]?\\d{6,}\\b", 
                java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = pattern.matcher(message);
        
        while (matcher.find()) {
            entities.add(new ExtractedEntity("order_number", matcher.group()));
        }
    }
    
    private void extractDate(List<ExtractedEntity> entities, String message) {
        java.util.regex.Pattern pattern = 
            java.util.regex.Pattern.compile("\\b(\\d{1,2}[-/]\\d{1,2}[-/]\\d{2,4})\\b");
        java.util.regex.Matcher matcher = pattern.matcher(message);
        
        while (matcher.find()) {
            entities.add(new ExtractedEntity("date", matcher.group()));
        }
    }
    
    private void extractProductName(List<ExtractedEntity> entities, String message) {
        String[] productKeywords = {"laptop", "phone", "tablet", "computer", "item", "product"};
        
        String lowerMessage = message.toLowerCase();
        
        for (String keyword : productKeywords) {
            if (lowerMessage.contains(keyword)) {
                int index = lowerMessage.indexOf(keyword);
                String product = message.substring(Math.max(0, index - 20), 
                    Math.min(message.length(), index + keyword.length() + 20));
                entities.add(new ExtractedEntity("product", product.trim()));
                break;
            }
        }
    }
    
    private void extractEmail(List<ExtractedEntity> entities, String message) {
        java.util.regex.Pattern pattern = 
            java.util.regex.Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b");
        java.util.regex.Matcher matcher = pattern.matcher(message);
        
        while (matcher.find()) {
            entities.add(new ExtractedEntity("email", matcher.group()));
        }
    }
}

class ExtractedEntity {
    
    private String type;
    private String value;
    
    public ExtractedEntity(String type, String value) {
        this.type = type;
        this.value = value;
    }
    
    public String getType() { return type; }
    public String getValue() { return value; }
}

class ResponseGenerator {
    
    private Map<String, List<String>> intentResponses;
    
    public ResponseGenerator() {
        this.intentResponses = new HashMap<>();
        initializeDefaultResponses();
    }
    
    private void initializeDefaultResponses() {
        intentResponses.put("greeting", Arrays.asList(
            "Hello! How can I help you today?",
            "Hi there! What can I assist you with?"
        ));
        
        intentResponses.put("goodbye", Arrays.asList(
            "Goodbye! Have a great day!",
            "Thank you for chatting with us. Goodbye!"
        ));
        
        intentResponses.put("help", Arrays.asList(
            "I can help you with order status, product information, or general questions.",
            "I'm here to help! What do you need assistance with?"
        ));
        
        intentResponses.put("information", Arrays.asList(
            "Let me get that information for you.",
            "I'd be happy to tell you more about that."
        ));
        
        intentResponses.put("order_status", Arrays.asList(
            "I can help you check your order status. Could you provide your order number?",
            "To check your order, please provide your order number."
        ));
        
        intentResponses.put("refund", Arrays.asList(
            "I understand you'd like a refund. Let me connect you with our support team.",
            "For refund requests, I'll need your order number to process this."
        ));
        
        intentResponses.put("general", Arrays.asList(
            "I didn't quite understand that. Could you please rephrase?",
            "I'm not sure I understood. Can you try again?"
        ));
    }
    
    public String generateResponse(String intent, List<ExtractedEntity> entities) {
        List<String> responses = intentResponses.getOrDefault(intent, 
            intentResponses.get("general"));
        
        String baseResponse = responses.get(0);
        
        for (ExtractedEntity entity : entities) {
            if (entity.getType().equals("order_number")) {
                baseResponse = "I found your order " + entity.getValue() + 
                    ". Let me check its status for you.";
            } else if (entity.getType().equals("email")) {
                baseResponse = "Thank you! I've noted your email " + entity.getValue();
            }
        }
        
        return baseResponse;
    }
    
    public void registerResponse(String intent, String response) {
        intentResponses.computeIfAbsent(intent, k -> new ArrayList<>()).add(response);
    }
}

class ChatbotResponse {
    
    private String cleanedMessage;
    private String intent;
    private List<ExtractedEntity> entities;
    private List<NamedEntity> nerEntities;
    private String reply;
    private double confidence;
    
    public String getCleanedMessage() { return cleanedMessage; }
    public void setCleanedMessage(String cleanedMessage) { this.cleanedMessage = cleanedMessage; }
    public String getIntent() { return intent; }
    public void setIntent(String intent) { this.intent = intent; }
    public List<ExtractedEntity> getEntities() { return entities; }
    public void setEntities(List<ExtractedEntity> entities) { this.entities = entities; }
    public List<NamedEntity> getNerEntities() { return nerEntities; }
    public void setNerEntities(List<NamedEntity> nerEntities) { this.nerEntities = nerEntities; }
    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
}

class Conversation {
    
    private String userMessage;
    private String botResponse;
    
    public Conversation(String userMessage, String botResponse) {
        this.userMessage = userMessage;
        this.botResponse = botResponse;
    }
    
    public String getUserMessage() { return userMessage; }
    public String getBotResponse() { return botResponse; }
}
```

---

### 4. Search Indexing Pipeline

**Objective:** Build a search indexing pipeline using Lucene for efficient full-text search and retrieval.

**Project Structure:**
```
search-indexing/
├── pom.xml
└── src/main/java/com/learning/nlppipeline/search/
    ├── SearchIndexerApplication.java
    ├── indexer/DocumentIndexer.java
    ├── search/SearchEngine.java
    └── analyzer/CustomAnalyzer.java
```

**Implementation:**

```java
package com.learning.nlppipeline.search;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.*;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.*;
import java.util.*;

public class DocumentIndexer {
    
    private IndexWriter indexWriter;
    private Analyzer analyzer;
    private String indexPath;
    
    public DocumentIndexer(String indexPath) throws Exception {
        this.indexPath = indexPath;
        this.analyzer = new StandardAnalyzer();
        
        Directory indexDir = FSDirectory.open(java.nio.file.Paths.get(indexPath));
        
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        this.indexWriter = new IndexWriter(indexDir, config);
    }
    
    public void indexDocument(String id, String title, String content, 
                              Map<String, String> metadata) throws Exception {
        Document doc = new Document();
        
        doc.add(new TextField("id", id, Field.Store.YES));
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new TextField("content", content, Field.Store.YES));
        
        if (metadata != null) {
            for (Map.Entry<String, String> entry : metadata.entrySet()) {
                doc.add(new TextField(entry.getKey(), entry.getValue(), 
                    Field.Store.YES));
                doc.add(new StringField(entry.getKey(), entry.getValue(), 
                    Field.Store.YES));
            }
        }
        
        indexWriter.addDocument(doc);
    }
    
    public void updateDocument(String id, String title, String content) throws Exception {
        Term term = new Term("id", id);
        indexWriter.updateDocument(term, createDocument(id, title, content));
    }
    
    private Document createDocument(String id, String title, String content) {
        Document doc = new Document();
        doc.add(new TextField("id", id, Field.Store.YES));
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new TextField("content", content, Field.Store.YES));
        return doc;
    }
    
    public void deleteDocument(String id) throws Exception {
        Term term = new Term("id", id);
        indexWriter.deleteDocuments(term);
    }
    
    public void commit() throws Exception {
        indexWriter.commit();
    }
    
    public void close() throws Exception {
        indexWriter.close();
    }
    
    public int getDocumentCount() throws Exception {
        return indexWriter.numDocs();
    }
}

class SearchEngine {
    
    private String indexPath;
    private Analyzer analyzer;
    private IndexReader indexReader;
    private IndexSearcher searcher;
    
    public SearchEngine(String indexPath) throws Exception {
        this.indexPath = indexPath;
        this.analyzer = new StandardAnalyzer();
        
        Directory indexDir = FSDirectory.open(java.nio.file.Paths.get(indexPath));
        this.indexReader = DirectoryReader.open(indexDir);
        this.searcher = new IndexSearcher(indexReader);
    }
    
    public List<SearchResult> search(String queryString, int maxResults) 
            throws Exception {
        QueryParser parser = new QueryParser("content", analyzer);
        Query query = parser.parse(queryString);
        
        TopDocs topDocs = searcher.search(query, maxResults);
        
        List<SearchResult> results = new ArrayList<>();
        
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            
            SearchResult result = new SearchResult(
                doc.get("id"),
                doc.get("title"),
                doc.get("content"),
                scoreDoc.score
            );
            
            results.add(result);
        }
        
        return results;
    }
    
    public List<SearchResult> searchWithFilters(String queryString, 
            Map<String, String> filters, int maxResults) throws Exception {
        
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        
        QueryParser parser = new QueryParser("content", analyzer);
        Query mainQuery = parser.parse(queryString);
        builder.add(mainQuery, BooleanClause.Occur.MUST);
        
        for (Map.Entry<String, String> filter : filters.entrySet()) {
            TermQuery termQuery = new TermQuery(
                new Term(filter.getKey(), filter.getValue()));
            builder.add(termQuery, BooleanClause.Occur.MUST);
        }
        
        Query combinedQuery = builder.build();
        TopDocs topDocs = searcher.search(combinedQuery, maxResults);
        
        List<SearchResult> results = new ArrayList<>();
        
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            
            SearchResult result = new SearchResult(
                doc.get("id"),
                doc.get("title"),
                doc.get("content"),
                scoreDoc.score
            );
            
            results.add(result);
        }
        
        return results;
    }
    
    public List<SearchResult> searchByField(String field, String value, int maxResults) 
            throws Exception {
        
        Term term = new Term(field, value);
        TermQuery query = new TermQuery(term);
        
        TopDocs topDocs = searcher.search(query, maxResults);
        
        List<SearchResult> results = new ArrayList<>();
        
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            
            SearchResult result = new SearchResult(
                doc.get("id"),
                doc.get("title"),
                doc.get("content"),
                scoreDoc.score
            );
            
            results.add(result);
        }
        
        return results;
    }
    
    public void close() throws Exception {
        indexReader.close();
    }
}

class SearchResult {
    
    private String id;
    private String title;
    private String content;
    private float score;
    
    public SearchResult(String id, String title, String content, float score) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.score = score;
    }
    
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public float getScore() { return score; }
    
    public String getSnippet(int maxLength) {
        if (content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }
}

class CustomAnalyzer extends Analyzer {
    
    private Set<String> stopWords;
    
    public CustomAnalyzer() {
        this.stopWords = new HashSet<>(Arrays.asList(
            "a", "an", "and", "are", "as", "at", "be", "by", "for", "from",
            "has", "he", "in", "is", "it", "its", "of", "on", "that", "the",
            "to", "was", "were", "will", "with"
        ));
    }
    
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        StandardTokenizer tokenizer = new StandardTokenizer();
        
        TokenStream filter = new LowerCaseFilter(tokenizer);
        filter = new StopFilter(filter, stopWords);
        filter = new ASCIIFoldingFilter(filter);
        
        return new TokenStreamComponents(tokenizer, filter);
    }
}
```

---

### 5. Text Mining Pipeline

**Objective:** Build a comprehensive text mining pipeline for extracting patterns, trends, and insights from large text corpora.

**Project Structure:**
```
text-mining/
├── pom.xml
└── src/main/java/com/learning/nlppipeline/mining/
    ├── TextMinerApplication.java
    ├── pipeline/TextMiningPipeline.java
    ├── extractor/PatternExtractor.java
    ├── analyzer/TrendAnalyzer.java
    └── visualizer/ResultsVisualizer.java
```

**Implementation:**

```java
package com.learning.nlppipeline.mining;

import java.util.*;
import java.util.regex.*;

public class TextMiningPipeline {
    
    private TextPreprocessor preprocessor;
    private NGramExtractor unigramExtractor;
    private NGramExtractor bigramExtractor;
    private NGramExtractor trigramExtractor;
    private NERService nerService;
    private PatternExtractor patternExtractor;
    private TrendAnalyzer trendAnalyzer;
    
    public TextMiningPipeline() {
        this.preprocessor = new TextPreprocessor();
        this.unigramExtractor = new NGramExtractor(1);
        this.bigramExtractor = new NGramExtractor(2);
        this.trigramExtractor = new NGramExtractor(3);
        this.nerService = new NERService();
        this.patternExtractor = new PatternExtractor();
        this.trendAnalyzer = new TrendAnalyzer();
    }
    
    public MiningResults mineText(String text) {
        MiningResults results = new MiningResults();
        
        String cleanedText = preprocessor.normalizeText(text);
        
        results.setWordFrequency(calculateWordFrequency(cleanedText));
        results.setBigramFrequency(bigramExtractor.getNgramFrequency(cleanedText));
        results.setTrigramFrequency(trigramExtractor.getNgramFrequency(cleanedText));
        
        results.setNamedEntities(nerService.recognizeEntities(text));
        results.setEntityGroups(nerService.groupEntitiesByType(text));
        
        results.setPatterns(patternExtractor.extractPatterns(cleanedText));
        
        results.setKeywords(extractKeywords(cleanedText));
        
        return results;
    }
    
    private Map<String, Integer> calculateWordFrequency(String text) {
        String[] tokens = text.split("\\s+");
        Map<String, Integer> frequency = new HashMap<>();
        
        for (String token : tokens) {
            if (token.length() > 2) {
                frequency.put(token, frequency.getOrDefault(token, 0) + 1);
            }
        }
        
        return frequency;
    }
    
    private List<String> extractKeywords(String text) {
        Map<String, Integer> frequency = calculateWordFrequency(text);
        
        return frequency.entrySet().stream()
            .filter(e -> e.getValue() >= 3)
            .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
            .limit(20)
            .map(Map.Entry::getKey)
            .toList();
    }
    
    public MiningResults mineCorpus(List<String> documents) {
        MiningResults aggregatedResults = new MiningResults();
        
        Map<String, Integer> totalWordFreq = new HashMap<>();
        Map<String, Integer> totalBigramFreq = new HashMap<>();
        List<NamedEntity> allEntities = new ArrayList<>();
        
        for (String doc : documents) {
            MiningResults singleResults = mineText(doc);
            
            for (Map.Entry<String, Integer> entry : singleResults.getWordFrequency().entrySet()) {
                totalWordFreq.put(entry.getKey(), 
                    totalWordFreq.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
            
            for (Map.Entry<String, Integer> entry : singleResults.getBigramFrequency().entrySet()) {
                totalBigramFreq.put(entry.getKey(), 
                    totalBigramFreq.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
            
            allEntities.addAll(singleResults.getNamedEntities());
        }
        
        aggregatedResults.setWordFrequency(totalWordFreq);
        aggregatedResults.setBigramFrequency(totalBigramFreq);
        aggregatedResults.setNamedEntities(allEntities);
        aggregatedResults.setEntityGroups(nerService.groupEntitiesByType(String.join(" ", documents)));
        
        return aggregatedResults;
    }
    
    public Map<String, Object> performTopicModeling(List<String> documents, int numTopics) {
        Map<String, Object> topicModel = new HashMap<>();
        
        Map<String, Integer> globalFreq = new HashMap<>();
        
        for (String doc : documents) {
            String cleaned = preprocessor.normalizeText(doc);
            Map<String, Integer> docFreq = calculateWordFrequency(cleaned);
            
            for (Map.Entry<String, Integer> entry : docFreq.entrySet()) {
                globalFreq.put(entry.getKey(), 
                    globalFreq.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }
        
        List<String> topWords = globalFreq.entrySet().stream()
            .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
            .limit(50)
            .map(Map.Entry::getKey)
            .toList();
        
        List<Map<String, Double>> topics = new ArrayList<>();
        
        for (int i = 0; i < numTopics; i++) {
            Map<String, Double> topic = new HashMap<>();
            int startIdx = (i * topWords.size() / numTopics);
            int endIdx = Math.min(startIdx + (topWords.size() / numTopics), topWords.size());
            
            for (int j = startIdx; j < endIdx; j++) {
                topic.put(topWords.get(j), Math.random() * 0.5 + 0.5);
            }
            
            topics.add(topic);
        }
        
        topicModel.put("topics", topics);
        topicModel.put("vocabulary", topWords);
        
        return topicModel;
    }
}

class PatternExtractor {
    
    public Map<String, List<String>> extractPatterns(String text) {
        Map<String, List<String>> patterns = new HashMap<>();
        
        patterns.put("urls", extractURLs(text));
        patterns.put("emails", extractEmails(text));
        patterns.put("phoneNumbers", extractPhoneNumbers(text));
        patterns.put("hashtags", extractHashtags(text));
        
        return patterns;
    }
    
    private List<String> extractURLs(String text) {
        List<String> urls = new ArrayList<>();
        Pattern pattern = Pattern.compile("https?://[^\\s]+");
        Matcher matcher = pattern.matcher(text);
        
        while (matcher.find()) {
            urls.add(matcher.group());
        }
        
        return urls;
    }
    
    private List<String> extractEmails(String text) {
        List<String> emails = new ArrayList<>();
        Pattern pattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        Matcher matcher = pattern.matcher(text);
        
        while (matcher.find()) {
            emails.add(matcher.group());
        }
        
        return emails;
    }
    
    private List<String> extractPhoneNumbers(String text) {
        List<String> phones = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\b\\d{3}[-.]?\\d{3}[-.]?\\d{4}\\b");
        Matcher matcher = pattern.matcher(text);
        
        while (matcher.find()) {
            phones.add(matcher.group());
        }
        
        return phones;
    }
    
    private List<String> extractHashtags(String text) {
        List<String> hashtags = new ArrayList<>();
        Pattern pattern = Pattern.compile("#[a-zA-Z0-9_]+");
        Matcher matcher = pattern.matcher(text);
        
        while (matcher.find()) {
            hashtags.add(matcher.group());
        }
        
        return hashtags;
    }
}

class TrendAnalyzer {
    
    public Map<String, Integer> analyzeTrends(List<String> timeSeriesData) {
        Map<String, Integer> trends = new HashMap<>();
        
        for (String data : timeSeriesData) {
            String[] parts = data.split(":");
            
            if (parts.length == 2) {
                String period = parts[0].trim();
                int count = Integer.parseInt(parts[1].trim());
                
                trends.put(period, count);
            }
        }
        
        return trends;
    }
    
    public double calculateGrowthRate(Map<String, Integer> trends) {
        if (trends.size() < 2) {
            return 0.0;
        }
        
        List<Integer> values = new ArrayList<>(trends.values());
        
        int first = values.get(0);
        int last = values.get(values.size() - 1);
        
        if (first == 0) {
            return 0.0;
        }
        
        return ((double) (last - first) / first) * 100;
    }
    
    public List<String> detectAnomalies(List<String> data, double threshold) {
        List<String> anomalies = new ArrayList<>();
        
        List<Integer> values = new ArrayList<>();
        
        for (String item : data) {
            try {
                values.add(Integer.parseInt(item.trim()));
            } catch (NumberFormatException e) {
                continue;
            }
        }
        
        if (values.isEmpty()) {
            return anomalies;
        }
        
        double mean = values.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        double stdDev = calculateStdDev(values, mean);
        
        for (int i = 0; i < values.size(); i++) {
            double zScore = Math.abs((values.get(i) - mean) / stdDev);
            
            if (zScore > threshold) {
                anomalies.add("Index " + i + ": " + values.get(i) + " (z-score: " + zScore + ")");
            }
        }
        
        return anomalies;
    }
    
    private double calculateStdDev(List<Integer> values, double mean) {
        double sum = 0.0;
        
        for (int value : values) {
            sum += Math.pow(value - mean, 2);
        }
        
        return Math.sqrt(sum / values.size());
    }
}

class MiningResults {
    
    private Map<String, Integer> wordFrequency;
    private Map<String, Integer> bigramFrequency;
    private Map<String, Integer> trigramFrequency;
    private List<NamedEntity> namedEntities;
    private Map<String, List<String>> entityGroups;
    private Map<String, List<String>> patterns;
    private List<String> keywords;
    
    public Map<String, Integer> getWordFrequency() { return wordFrequency; }
    public void setWordFrequency(Map<String, Integer> wordFrequency) { this.wordFrequency = wordFrequency; }
    public Map<String, Integer> getBigramFrequency() { return bigramFrequency; }
    public void setBigramFrequency(Map<String, Integer> bigramFrequency) { this.bigramFrequency = bigramFrequency; }
    public Map<String, Integer> getTrigramFrequency() { return trigramFrequency; }
    public void setTrigramFrequency(Map<String, Integer> trigramFrequency) { this.trigramFrequency = trigramFrequency; }
    public List<NamedEntity> getNamedEntities() { return namedEntities; }
    public void setNamedEntities(List<NamedEntity> namedEntities) { this.namedEntities = namedEntities; }
    public Map<String, List<String>> getEntityGroups() { return entityGroups; }
    public void setEntityGroups(Map<String, List<String>> entityGroups) { this.entityGroups = entityGroups; }
    public Map<String, List<String>> getPatterns() { return patterns; }
    public void setPatterns(Map<String, List<String>> patterns) { this.patterns = patterns; }
    public List<String> getKeywords() { return keywords; }
    public void setKeywords(List<String> keywords) { this.keywords = keywords; }
}
```

---

## Build Instructions

For each project, create a subdirectory and build:

```bash
cd <project-directory>
mvn clean compile
mvn exec:java -Dexec.mainClass="com.learning.nlppipeline.<ProjectMainClass>"
```

## Learning Path

1. Start with Mini-Projects 1-5 for foundational NLP concepts
2. Progress to Mini-Projects 6-10 for advanced processing
3. Apply knowledge in Real-World Projects 1-3
4. Master complex pipelines with Real-World Projects 4-5