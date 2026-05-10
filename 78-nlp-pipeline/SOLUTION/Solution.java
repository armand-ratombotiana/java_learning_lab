package com.learning.nlppipeline;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.util.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.ByteBuffersDirectory;

import java.util.*;
import java.util.stream.Collectors;

public class Solution {

    public static class StanfordNLPExample {

        public StanfordCoreNLP createPipeline() {
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, depparse");
            props.setProperty("ner.useSUTime", "false");
            return new StanfordCoreNLP(props);
        }

        public CoreDocument processText(StanfordCoreNLP pipeline, String text) {
            CoreDocument document = new CoreDocument(text);
            pipeline.annotate(document);
            return document;
        }

        public List<CoreLabel> tokenize(StanfordCoreNLP pipeline, String text) {
            CoreDocument document = processText(pipeline, text);
            return document.tokens();
        }

        public List<CoreSentence> splitSentences(StanfordCoreNLP pipeline, String text) {
            CoreDocument document = processText(pipeline, text);
            return document.sentences();
        }

        public List<String> getPOS tags(CoreDocument document) {
            return document.tokens().stream()
                .map(token -> token.tag())
                .collect(Collectors.toList());
        }

        public List<String> getLemmas(CoreDocument document) {
            return document.tokens().stream()
                .map(token -> token.lemma())
                .collect(Collectors.toList());
        }

        public List<CoreEntityMention> getNamedEntities(CoreDocument document) {
            return document.entityMentions();
        }

        public List<String> getEntityTypes(CoreDocument document) {
            return document.entityMentions().stream()
                .map(entity -> entity.entityType())
                .collect(Collectors.toList());
        }

        public List<CoreMap> getPhrases(CoreDocument document, String type) {
            return document.sentences().stream()
                .flatMap(sentence -> sentence.get(CoreAnnotations.TreeAnnotation.class).preOrder())
                .filter(tree -> tree.label().toString().startsWith(type))
                .map(tree -> tree.yield())
                .collect(Collectors.toList());
        }

        public List<SemanticGraph> getDependencyGraphs(CoreDocument document) {
            return document.sentences().stream()
                .map(sentence -> sentence.dependencyParse())
                .collect(Collectors.toList());
        }
    }

    public static class TokenizationExample {

        public List<String> tokenizeWithLucene(String text) throws Exception {
            Analyzer analyzer = new StandardAnalyzer();
            List<String> tokens = new ArrayList<>();

            org.apache.lucene.analysis.TokenStream stream = analyzer.tokenStream("field", text);
            CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
            stream.reset();

            while (stream.incrementToken()) {
                tokens.add(term.toString());
            }
            stream.end();
            stream.close();

            return tokens;
        }

        public List<String> lowercaseTokens(List<String> tokens) {
            return tokens.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        }

        public List<String> removeStopWords(List<String> tokens) {
            Set<String> stopWords = new HashSet<>(Arrays.asList(
                "the", "a", "an", "is", "are", "was", "were", "be", "been", "being",
                "have", "has", "had", "do", "does", "did", "will", "would", "could"
            ));
            return tokens.stream()
                .filter(token -> !stopWords.contains(token.toLowerCase()))
                .collect(Collectors.toList());
        }

        public List<String> stemTokens(List<String> tokens) {
            return tokens.stream()
                .map(token -> stemWord(token))
                .collect(Collectors.toList());
        }

        private String stemWord(String word) {
            if (word.length() > 4) {
                if (word.endsWith("ing")) return word.substring(0, word.length() - 3);
                if (word.endsWith("ed")) return word.substring(0, word.length() - 2);
                if (word.endsWith("s") && !word.endsWith("ss")) return word.substring(0, word.length() - 1);
            }
            return word;
        }
    }

    public static class LuceneSearchExample {

        public Directory createIndex(List<String> documents) throws Exception {
            Directory directory = new ByteBuffersDirectory();
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            IndexWriter writer = new IndexWriter(directory, config);

            for (int i = 0; i < documents.size(); i++) {
                Document doc = new Document();
                doc.add(new TextField("id", String.valueOf(i), Field.Store.YES));
                doc.add(new TextField("content", documents.get(i), Field.Store.YES));
                writer.addDocument(doc);
            }

            writer.close();
            return directory;
        }

        public List<String> search(Directory directory, String queryString, int maxResults) throws Exception {
            IndexReader reader = DirectoryReader.open(directory);
            IndexSearcher searcher = new IndexSearcher(reader);

            QueryParser parser = new QueryParser("content", new StandardAnalyzer());
            Query query = parser.parse(queryString);

            ScoreDoc[] hits = searcher.search(query, maxResults).scoreDocs;

            List<String> results = new ArrayList<>();
            for (ScoreDoc hit : hits) {
                Document doc = searcher.doc(hit.doc);
                results.add(doc.get("content"));
            }

            reader.close();
            return results;
        }

        public List<String> searchWithFilter(Directory directory, String queryString,
                                             Map<String, String> filters, int maxResults) throws Exception {
            return search(directory, queryString, maxResults);
        }
    }

    public static class TextProcessingExample {

        public String normalizeText(String text) {
            return text.replaceAll("\\s+", " ")
                .replaceAll("[^a-zA-Z0-9\\s]", "")
                .trim();
        }

        public String removeHTMLTags(String text) {
            return text.replaceAll("<[^>]*>", "");
        }

        public String expandContractions(String text) {
            Map<String, String> contractions = new HashMap<>();
            contractions.put("won't", "will not");
            contractions.put("can't", "cannot");
            contractions.put("n't", " not");
            contractions.put("'re", " are");
            contractions.put("'ve", " have");
            contractions.put("'ll", " will");
            contractions.put("'d", " would");
            contractions.put("'m", " am");

            for (Map.Entry<String, String> entry : contractions.entrySet()) {
                text = text.replace(entry.getKey(), entry.getValue());
            }
            return text;
        }

        public String removeAccents(String text) {
            return java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        }
    }

    public static class FeatureExtractionExample {

        public Map<String, Integer> getWordFrequency(List<String> tokens) {
            Map<String, Integer> frequency = new HashMap<>();
            for (String token : tokens) {
                frequency.put(token, frequency.getOrDefault(token, 0) + 1);
            }
            return frequency;
        }

        public List<String> getTopNWords(Map<String, Integer> frequency, int n) {
            return frequency.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(n)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        }

        public double calculateTF(String term, List<String> tokens) {
            long count = tokens.stream().filter(t -> t.equals(term)).count();
            return (double) count / tokens.size();
        }

        public double calculateIDF(String term, List<List<String>> documents) {
            long docsWithTerm = documents.stream()
                .filter(doc -> doc.contains(term))
                .count();
            return Math.log((double) documents.size() / (docsWithTerm + 1));
        }

        public double calculateTFIDF(String term, List<String> tokens, List<List<String>> documents) {
            double tf = calculateTF(term, tokens);
            double idf = calculateIDF(term, documents);
            return tf * idf;
        }
    }

    public static class PipelineExample {

        public NLPPipeline createPipeline() {
            return new NLPPipeline();
        }

        public ProcessingResult process(NLPPipeline pipeline, String text) {
            return pipeline.process(text);
        }

        public String extractKeywords(NLPPipeline pipeline, String text, int topN) {
            ProcessingResult result = pipeline.process(text);
            Map<String, Integer> freq = result.getWordFrequency();
            return result.getTopWords(topN).stream()
                .collect(Collectors.joining(", "));
        }
    }

    static class NLPPipeline {
        private StanfordCoreNLP stanfordNLP;
        private TokenizationExample tokenizer;

        public NLPPipeline() {
            this.stanfordNLP = new StanfordNLPExample().createPipeline();
            this.tokenizer = new TokenizationExample();
        }

        public ProcessingResult process(String text) {
            ProcessingResult result = new ProcessingResult();
            result.text = text;
            return result;
        }
    }

    static class ProcessingResult {
        public String text;
        private Map<String, Integer> wordFrequency;

        public Map<String, Integer> getWordFrequency() {
            if (wordFrequency == null) {
                wordFrequency = new HashMap<>();
            }
            return wordFrequency;
        }

        public List<String> getTopWords(int n) {
            return getWordFrequency().entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(n)
                .map(Map.Entry::getKey)
                .toList();
        }
    }

    public static void main(String[] args) {
        System.out.println("NLP Pipeline Solutions");
        System.out.println("=======================");

        StanfordNLPExample stanfordNLP = new StanfordNLPExample();
        StanfordCoreNLP pipeline = stanfordNLP.createPipeline();
        System.out.println("Stanford CoreNLP pipeline created");

        CoreDocument doc = stanfordNLP.processText(pipeline, "John works at Google in New York.");
        System.out.println("Processed: " + doc.tokens().size() + " tokens");

        TokenizationExample tokenizer = new TokenizationExample();
        System.out.println("Tokenizer examples available");

        LuceneSearchExample lucene = new LuceneSearchExample();
        System.out.println("Lucene search ready");

        TextProcessingExample textProcessor = new TextProcessingExample();
        String normalized = textProcessor.normalizeText("  Hello   World!  ");
        System.out.println("Normalized: " + normalized);

        FeatureExtractionExample featureExtraction = new FeatureExtractionExample();
        System.out.println("Feature extraction ready");

        PipelineExample pipelineExample = new PipelineExample();
        System.out.println("NLP pipeline utilities available");
    }
}