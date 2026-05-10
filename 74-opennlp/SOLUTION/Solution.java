package com.learning.opennlp;

import opennlp.tools.cmdline.tokenizer.TokenizerTool;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameModel;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.chunker.Chunker;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.util.Span;
import opennlp.tools.util.Sequence;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class Solution {

    public static class TokenizationExample {

        public String[] tokenizeSimple(String text) {
            Tokenizer tokenizer = SimpleTokenizer.INSTANCE;
            return tokenizer.tokenize(text);
        }

        public String[] tokenizeWithModel(InputStream modelStream) throws Exception {
            TokenizerModel model = new TokenizerModel(modelStream);
            Tokenizer tokenizer = new opennlp.tools.tokenize.TokenizerME(model);
            return tokenizer.tokenize("This is a sample sentence for tokenization.");
        }

        public Span[] findTokenSpans(String text) {
            Tokenizer tokenizer = SimpleTokenizer.INSTANCE;
            return tokenizer.tokenizePos(text);
        }

        public double[] getTokenProbs(String[] tokens, InputStream modelStream) throws Exception {
            TokenizerModel model = new TokenizerModel(modelStream);
            opennlp.tools.tokenize.TokenizerME tokenizer = new opennlp.tools.tokenize.TokenizerME(model);
            return tokenizer.getTokenProbs(tokens);
        }
    }

    public static class SentenceDetectionExample {

        public String[] detectSentences(String text) {
            SentenceDetector detector = new SentenceDetectorME(
                new SentenceModel(new File("models/en-sent.bin"))
            );
            return detector.sentDetect(text);
        }

        public Span[] detectSentenceSpans(String text) throws Exception {
            try (InputStream modelStream = new FileInputStream("models/en-sent.bin")) {
                SentenceModel model = new SentenceModel(modelStream);
                SentenceDetectorME detector = new SentenceDetectorME(model);
                return detector.sentPosDetect(text);
            }
        }

        public double[] getSentenceProbs(String[] sentences) throws Exception {
            try (InputStream modelStream = new FileInputStream("models/en-sent.bin")) {
                SentenceModel model = new SentenceModel(modelStream);
                SentenceDetectorME detector = new SentenceDetectorME(model);
                return detector.getSentenceProbs();
            }
        }

        public String[] segment(String text) {
            return text.split("[.!?]+\\s*");
        }
    }

    public static class POSExample {

        public String[] tagWords(String[] tokens) throws Exception {
            try (InputStream modelStream = new FileInputStream("models/en-pos-maxent.bin")) {
                POSModel model = new POSModel(modelStream);
                POSTaggerME tagger = new POSTaggerME(model);
                return tagger.tag(tokens);
            }
        }

        public String[] tagWithProbabilities(String[] tokens) throws Exception {
            try (InputStream modelStream = new FileInputStream("models/en-pos-maxent.bin")) {
                POSModel model = new POSModel(modelStream);
                POSTaggerME tagger = new POSTaggerME(model);
                String[] tags = tagger.tag(tokens);
                double[] probs = tagger.getProbs();
                return tags;
            }
        }

        public Span[] getPosSpans(String[] tokens, String[] tags) {
            Span[] spans = new Span[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                spans[i] = new Span(i, i + 1, tags[i]);
            }
            return spans;
        }

        public String getTag(String token) throws Exception {
            try (InputStream modelStream = new FileInputStream("models/en-pos-maxent.bin")) {
                POSModel model = new POSModel(modelStream);
                POSTaggerME tagger = new POSTaggerME(model);
                String[] tags = tagger.tag(new String[]{token});
                return tags[0];
            }
        }
    }

    public static class NERExample {

        public Span[] findEntities(String[] tokens) throws Exception {
            try (InputStream modelStream = new FileInputStream("models/en-ner-person.bin")) {
                TokenNameFinderModel model = new TokenNameFinderModel(modelStream);
                NameFinderME finder = new NameFinderME(model);
                return finder.find(tokens);
            }
        }

        public Span[] findMultipleEntityTypes(String[] tokens) throws Exception {
            Span[] personSpans;
            Span[] locationSpans;
            Span[] organizationSpans;

            try (InputStream personModel = new FileInputStream("models/en-ner-person.bin")) {
                TokenNameFinderModel model = new TokenNameFinderModel(personModel);
                NameFinderME finder = new NameFinderME(model);
                personSpans = finder.find(tokens);
            }

            return personSpans;
        }

        public String[] getEntityTypes(Span[] spans) {
            String[] types = new String[spans.length];
            for (int i = 0; i < spans.length; i++) {
                types[i] = spans[i].getType();
            }
            return types;
        }

        public double[] getEntityProbs(Span[] entities) {
            double[] probs = new double[entities.length];
            for (int i = 0; i < entities.length; i++) {
                probs[i] = entities[i].getConfidence();
            }
            return probs;
        }
    }

    public static class ChunkerExample {

        public String[] chunkSentence(String[] tokens, String[] tags) throws Exception {
            try (InputStream modelStream = new FileInputStream("models/en-chunker.bin")) {
                ChunkerModel model = new ChunkerModel(modelStream);
                ChunkerME chunker = new ChunkerME(model);
                return chunker.chunk(tokens, tags);
            }
        }

        public Span[] getChunkSpans(String[] tokens, String[] tags) throws Exception {
            try (InputStream modelStream = new FileInputStream("models/en-chunker.bin")) {
                ChunkerModel model = new ChunkerModel(modelStream);
                ChunkerME chunker = new ChunkerME(model);
                return chunker.chunkAsSpans(tokens, tags);
            }
        }

        public Sequence topKSequences(String[] tokens, String[] tags, int k) throws Exception {
            try (InputStream modelStream = new FileInputStream("models/en-chunker.bin")) {
                ChunkerModel model = new ChunkerModel(modelStream);
                ChunkerME chunker = new ChunkerME(model);
                return chunker.topKSequences(tokens, tags);
            }
        }
    }

    public static class LemmatizerExample {

        public String[] lemmatize(String[] tokens, String[] posTags) throws Exception {
            try (InputStream modelStream = new FileInputStream("models/en-lemmatizer.bin")) {
                LemmatizerModel model = new LemmatizerModel(modelStream);
                DictionaryLemmatizer lemmatizer = new DictionaryLemmatizer(model);
                return lemmatizer.lemmatize(tokens, posTags);
            }
        }

        public String lemmatizeToken(String token, String posTag) throws Exception {
            try (InputStream modelStream = new FileInputStream("models/en-lemmatizer.bin")) {
                LemmatizerModel model = new LemmatizerModel(modelStream);
                DictionaryLemmatizer lemmatizer = new DictionaryLemmatizer(model);
                String[] result = lemmatizer.lemmatize(new String[]{token}, new String[]{posTag});
                return result[0];
            }
        }
    }

    public static class ParserExample {

        public opennlp.tools.parser.Parse parse(String sentence) throws Exception {
            try (InputStream modelStream = new FileInputStream("models/en-parser.bin")) {
                ParserModel model = new ParserModel(modelStream);
                Parser parser = ParserFactory.create(model);
                return parser.parse(sentence);
            }
        }

        public opennlp.tools.parser.Parse[] parseMultiple(String sentence, int numParses) throws Exception {
            try (InputStream modelStream = new FileInputStream("models/en-parser.bin")) {
                ParserModel model = new ParserModel(modelStream);
                Parser parser = ParserFactory.create(model);
                return parser.parse(sentence, numParses);
            }
        }
    }

    public static class PipelineExample {

        public String processText(String text) throws Exception {
            StringBuilder result = new StringBuilder();

            String[] sentences = new SentenceDetectionExample().detectSentences(text);
            result.append("Sentences: ").append(sentences.length).append("\n");

            for (String sentence : sentences) {
                String[] tokens = new TokenizationExample().tokenizeSimple(sentence);
                String[] posTags = new POSExample().tagWords(tokens);
                Span[] entities = new NERExample().findEntities(tokens);

                result.append("\nSentence: ").append(sentence).append("\n");
                result.append("Tokens: ").append(Arrays.toString(tokens)).append("\n");
                result.append("POS Tags: ").append(Arrays.toString(posTags)).append("\n");
                result.append("Entities: ").append(Arrays.toString(entities)).append("\n");
            }

            return result.toString();
        }

        public NLPResult analyzeText(String text) throws Exception {
            NLPResult result = new NLPResult();

            SentenceDetectionExample sentenceDetector = new SentenceDetectionExample();
            result.sentences = sentenceDetector.detectSentences(text);

            TokenizationExample tokenizer = new TokenizationExample();
            result.tokens = tokenizer.tokenizeSimple(text);

            POSExample posTagger = new POSExample();
            result.posTags = posTagger.tagWords(result.tokens);

            NERExample nerFinder = new NERExample();
            result.entities = nerFinder.findEntities(result.tokens);

            return result;
        }
    }

    public static class NLPResult {
        public String[] sentences;
        public String[] tokens;
        public String[] posTags;
        public Span[] entities;
    }

    public static void main(String[] args) {
        System.out.println("OpenNLP Solutions");
        System.out.println("=================");

        TokenizationExample tokenExample = new TokenizationExample();
        String[] tokens = tokenExample.tokenizeSimple("This is a test sentence.");
        System.out.println("Tokenization: " + Arrays.toString(tokens));

        SentenceDetectionExample sentExample = new SentenceDetectionExample();
        String[] sentences = sentExample.segment("Hello world. This is a test. Another sentence.");
        System.out.println("Sentence detection: " + sentences.length + " sentences");

        POSExample posExample = new POSExample();
        System.out.println("POS tagging available");

        NERExample nerExample = new NERExample();
        System.out.println("NER available");

        ChunkerExample chunkerExample = new ChunkerExample();
        System.out.println("Chunker available");

        PipelineExample pipelineExample = new PipelineExample();
        System.out.println("NLP pipeline ready");
    }
}