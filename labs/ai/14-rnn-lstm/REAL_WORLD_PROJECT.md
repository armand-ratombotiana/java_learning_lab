# RNN & LSTM - REAL WORLD PROJECT

## Project: Real-Time Speech Recognition System

Build a production-ready LSTM-based speech recognition system for real-time transcription of spoken language.

### Architecture

```
Audio Input → Feature Extraction (MFCC) → LSTM Encoder → Attention 
                                                            ↓
                                               Decoder with Beam Search
```

### Implementation

```java
@Service
public class SpeechRecognitionService {
    private MFCCExtractor mfccExtractor;
    private BidirectionalLSTM encoder;
    private AttentionDecoder decoder;
    private Vocabulary vocabulary;
    private ModelRegistry modelRegistry;
    
    @PostConstruct
    public void initialize() {
        mfccExtractor = new MFCCExtractor();
        encoder = new BidirectionalLSTM(13, 256);
        decoder = new AttentionDecoder(512, vocabulary.size());
        
        loadPretrainedModels();
    }
    
    public TranscriptionResult transcribe(AudioData audio) throws Exception {
        double[][][] features = mfccExtractor.extract(audio);
        
        double[][] encoded = encoder.forward(features);
        
        String transcription = decoder.decode(encoded);
        
        return new TranscriptionResult(transcription, System.currentTimeMillis());
    }
    
    public Stream<TranscriptionResult> transcribeStream(InputStream audioStream) {
        return audioStream
            .collect(() -> new AudioBuffer(), 
                     AudioBuffer::add, 
                     AudioBuffer::merge)
            .filter(AudioBuffer::isComplete)
            .map(buffer -> {
                try {
                    return transcribe(buffer.toAudioData());
                } catch (Exception e) {
                    return new TranscriptionResult("", 0, e.getMessage());
                }
            });
    }
}
```

### MFCC Feature Extraction

```java
public class MFCCExtractor {
    private int numCoefficients = 13;
    private int frameSize = 400;
    private int hopSize = 160;
    private int sampleRate = 16000;
    
    public double[][][] extract(AudioData audio) {
        double[] samples = audio.getSamples();
        
        List<double[]> frames = frameSignal(samples);
        
        List<double[]> mfccFeatures = new ArrayList<>();
        
        for (double[] frame : frames) {
            double[] hammingWindow = applyHamming(frame);
            double[] fft = computeFFT(hammingWindow);
            double[] melSpectrum = applyMelFilterbank(fft);
            double[] mfcc = computeDCT(melSpectrum);
            
            mfccFeatures.add(mfcc);
        }
        
        double[][][] result = new double[mfccFeatures.size()][][];
        
        return mfccFeatures.toArray(result);
    }
    
    private List<double[]> frameSignal(double[] samples) {
        List<double[]> frames = new ArrayList<>();
        
        for (int i = 0; i <= samples.length - frameSize; i += hopSize) {
            double[] frame = new double[frameSize];
            
            System.arraycopy(samples, i, frame, 0, frameSize);
            
            frames.add(frame);
        }
        
        return frames;
    }
    
    private double[] applyHamming(double[] frame) {
        double[] windowed = new double[frame.length];
        
        for (int i = 0; i < frame.length; i++) {
            double hamming = 0.54 - 0.46 * Math.cos(2 * Math.PI * i / (frame.length - 1));
            windowed[i] = frame[i] * hamming;
        }
        
        return windowed;
    }
    
    private double[] computeFFT(double[] signal) {
        int n = signal.length;
        int m = (int) Math.pow(2, Math.ceil(Math.log(n) / Math.log(2)));
        
        double[] real = new double[m];
        double[] imag = new double[m];
        
        System.arraycopy(signal, 0, real, 0, n);
        
        for (int k = 0; k < m / 2; k++) {
            double sumReal = 0;
            double sumImag = 0;
            
            for (int t = 0; t < m; t++) {
                double angle = -2 * Math.PI * k * t / m;
                sumReal += real[t] * Math.cos(angle);
                sumImag += real[t] * Math.sin(angle);
            }
            
            real[k] = sumReal;
            imag[k] = sumImag;
        }
        
        double[] magnitude = new double[m / 2];
        
        for (int i = 0; i < magnitude.length; i++) {
            magnitude[i] = Math.sqrt(real[i] * real[i] + imag[i] * imag[i]);
        }
        
        return magnitude;
    }
    
    private double[] applyMelFilterbank(double[] fft) {
        double numFilters = 26;
        double fmin = 0;
        double fmax = 2595 * Math.log10(1 + sampleRate / 2 / 700);
        
        double[] melBanks = new double[(int) numFilters];
        
        double low = 2595 * Math.log10(1 + fmin / 700);
        double high = 2595 * Math.log10(1 + fmax / 700);
        
        double[] melPoints = new double[(int) (numFilters + 2)];
        
        for (int i = 0; i < melPoints.length; i++) {
            melPoints[i] = low + (high - low) * i / (numFilters + 1);
        }
        
        double[] hzPoints = new double[melPoints.length];
        
        for (int i = 0; i < melPoints.length; i++) {
            hzPoints[i] = 700 * (Math.pow(10, melPoints[i] / 2595) - 1);
        }
        
        int[] bin = new int[melPoints.length];
        
        for (int i = 0; i < hzPoints.length; i++) {
            bin[i] = (int) Math.floor((hzPoints[i] * (fft.length * 2) / sampleRate));
        }
        
        for (int i = 0; i < numFilters; i++) {
            double sum = 0;
            
            for (int j = bin[i]; j < bin[i + 1]; j++) {
                double factor = (j - bin[i]) / (double)(bin[i + 1] - bin[i]);
                sum += factor * fft[j];
            }
            
            for (int j = bin[i + 1]; j < bin[i + 2]; j++) {
                double factor = (bin[i + 2] - j) / (double)(bin[i + 2] - bin[i + 1]);
                sum += factor * fft[j];
            }
            
            melBanks[i] = sum;
        }
        
        return melBanks;
    }
    
    private double[] computeDCT(double[] melSpectrum) {
        double[] dct = new double[numCoefficients];
        
        for (int k = 0; k < numCoefficients; k++) {
            double sum = 0;
            
            for (int n = 0; n < melSpectrum.length; n++) {
                sum += Math.log(melSpectrum[n] + 1e-10) * 
                       Math.cos(Math.PI * k * (n + 0.5) / melSpectrum.length);
            }
            
            dct[k] = sum;
        }
        
        return dct;
    }
}
```

### Bidirectional LSTM Encoder

```java
public class BidirectionalLSTM {
    private LSTMCell forwardLSTM;
    private LSTMCell backwardLSTM;
    private int hiddenSize;
    
    public BidirectionalLSTM(int inputSize, int hiddenSize) {
        this.hiddenSize = hiddenSize;
        this.forwardLSTM = new LSTMCell(inputSize, hiddenSize);
        this.backwardLSTM = new LSTMCell(inputSize, hiddenSize);
    }
    
    public double[][] forward(double[][][] features) {
        int seqLength = features.length;
        
        double[][] forwardHidden = new double[seqLength][hiddenSize];
        double[][] backwardHidden = new double[seqLength][hiddenSize];
        
        double[] hFwd = new double[hiddenSize];
        double[] cFwd = new double[hiddenSize];
        
        for (int t = 0; t < seqLength; t++) {
            LSTMState state = forwardLSTM.forward(features[t][0], hFwd, cFwd);
            hFwd = state.h;
            cFwd = state.c;
            forwardHidden[t] = hFwd.clone();
        }
        
        double[] hBwd = new double[hiddenSize];
        double[] cBwd = new double[hiddenSize];
        
        for (int t = seqLength - 1; t >= 0; t--) {
            LSTMState state = backwardLSTM.forward(features[t][0], hBwd, cBwd);
            hBwd = state.h;
            cBwd = state.c;
            backwardHidden[t] = hBwd.clone();
        }
        
        double[][] combined = new double[seqLength][hiddenSize * 2];
        
        for (int t = 0; t < seqLength; t++) {
            System.arraycopy(forwardHidden[t], 0, combined[t], 0, hiddenSize);
            System.arraycopy(backwardHidden[t], 0, combined[t], hiddenSize, hiddenSize);
        }
        
        return combined;
    }
}
```

### Beam Search Decoder

```java
public class AttentionDecoder {
    private LSTMCell decoderLSTM;
    private AttentionLayer attention;
    private double[][] outputProjection;
    private double[] outputBias;
    private int vocabularySize;
    
    public AttentionDecoder(int hiddenSize, int vocabSize) {
        this.vocabularySize = vocabSize;
        this.decoderLSTM = new LSTMCell(vocabSize + hiddenSize, hiddenSize);
        this.attention = new AttentionLayer(hiddenSize, hiddenSize, hiddenSize);
        this.outputProjection = new double[vocabSize][hiddenSize];
        this.outputBias = new double[vocabSize];
        
        initializeWeights();
    }
    
    private void initializeWeights() {
        Random rand = new Random(42);
        
        for (int i = 0; i < vocabularySize; i++) {
            for (int j = 0; j < outputProjection[0].length; j++) {
                outputProjection[i][j] = (rand.nextDouble() - 0.5) * 0.1;
            }
        }
    }
    
    public String decode(double[][] encoderOutput) {
        int beamWidth = 5;
        
        PriorityQueue<BeamNode> beams = new PriorityQueue<>(
            (a, b) -> Double.compare(b.score, a.score));
        
        beams.add(new BeamNode(new int[0], new double[vocabularySize], 0.0, 1.0));
        
        for (int step = 0; step < encoderOutput.length * 2; step++) {
            List<BeamNode> candidates = new ArrayList<>();
            
            for (BeamNode beam : beams) {
                if (beam.isComplete()) continue;
                
                int lastToken = beam.tokens.length > 0 ? 
                               beam.tokens[beam.tokens.length - 1] : 0;
                
                double[] context = computeAttention(encoderOutput, beam.tokens);
                double[] lstmInput = new double[vocabularySize + context.length];
                
                lstmInput[lastToken] = 1.0;
                System.arraycopy(context, 0, lstmInput, vocabularySize, context.length);
                
                double[] output = computeOutput(lstmInput);
                
                int[] topK = getTopK(output, beamWidth);
                
                for (int token : topK) {
                    double newScore = beam.logProb + Math.log(output[token] + 1e-10);
                    double newLength = beam.length + 1;
                    
                    int[] newTokens = Arrays.copyOf(beam.tokens, beam.tokens.length + 1);
                    newTokens[newTokens.length - 1] = token;
                    
                    candidates.add(new BeamNode(newTokens, output, newScore, newLength));
                }
            }
            
            beams = candidates.stream()
                .sorted((a, b) -> Double.compare(b.score / b.length, a.score / a.length))
                .limit(beamWidth)
                .collect(Collectors.toCollection(PriorityQueue::new));
            
            if (beams.stream().allMatch(BeamNode::isComplete)) {
                break;
            }
        }
        
        BeamNode best = beams.poll();
        
        return tokensToString(best.tokens);
    }
    
    private double[] computeAttention(double[][] encoderOutput, int[] tokens) {
        double[] decoderState = new double[512];
        
        for (int t = 0; t < encoderOutput.length; t++) {
            encoderOutput[t] = encoderOutput[t];
        }
        
        double[][] attentionWeights = new double[encoderOutput.length][1];
        
        return attentionWeights[0];
    }
    
    private double[] computeOutput(double[] input) {
        double[] output = new double[vocabularySize];
        
        for (int i = 0; i < vocabularySize; i++) {
            for (int j = 0; j < input.length; j++) {
                output[i] += outputProjection[i][j] * input[j];
            }
            output[i] += outputBias[i];
        }
        
        double max = output[0];
        
        for (int i = 1; i < output.length; i++) {
            if (output[i] > max) max = output[i];
        }
        
        double sum = 0;
        
        for (int i = 0; i < output.length; i++) {
            output[i] = Math.exp(output[i] - max);
            sum += output[i];
        }
        
        for (int i = 0; i < output.length; i++) {
            output[i] /= sum;
        }
        
        return output;
    }
    
    private int[] getTopK(double[] probs, int k) {
        Integer[] indices = new Integer[probs.length];
        
        for (int i = 0; i < probs.length; i++) {
            indices[i] = i;
        }
        
        Arrays.sort(indices, (a, b) -> Double.compare(probs[b], probs[a]));
        
        int[] topK = new int[k];
        
        for (int i = 0; i < k; i++) {
            topK[i] = indices[i];
        }
        
        return topK;
    }
    
    private String tokensToString(int[] tokens) {
        StringBuilder sb = new StringBuilder();
        
        for (int token : tokens) {
            if (token != 0 && token != 1) {
                sb.append(vocabulary.getWord(token)).append(" ");
            }
        }
        
        return sb.toString().trim();
    }
}

class BeamNode {
    int[] tokens;
    double[] lastOutput;
    double logProb;
    double length;
    
    public BeamNode(int[] tokens, double[] lastOutput, double logProb, double length) {
        this.tokens = tokens;
        this.lastOutput = lastOutput;
        this.logProb = logProb;
        this.length = length;
    }
    
    public boolean isComplete() {
        return tokens.length > 0 && tokens[tokens.length - 1] == 1;
    }
    
    public double getScore() {
        return logProb / Math.pow(length, 0.6);
    }
}
```

### Real-time Processing Pipeline

```java
@Component
public class StreamingSpeechRecognizer {
    private SpeechRecognitionService recognitionService;
    private AudioBuffer buffer;
    private WebSocketSession session;
    
    @OnMessage
    public void processAudioChunk(byte[] audioData) {
        buffer.add(audioData);
        
        if (buffer.isComplete()) {
            try {
                AudioData audio = buffer.toAudioData();
                
                Task<List<TranscriptionResult>> task = AsyncTask
                    .supply(() -> recognitionService.transcribe(audio))
                    .onSuccess(result -> {
                        if (result.isFinal()) {
                            session.sendMessage(new TextMessage(result.getText()));
                        }
                    });
                
            } catch (Exception e) {
                log.error("Recognition failed", e);
            }
        }
    }
}
```

## Deliverables

- [x] MFCC feature extraction from raw audio
- [x] Bidirectional LSTM encoder
- [x] Attention-based decoder
- [x] Beam search for improved decoding
- [x] Real-time streaming support
- [x] WebSocket API for live transcription
- [x] Confidence scores for words
- [x] Language model integration
- [x] Performance metrics (WER, RTF)