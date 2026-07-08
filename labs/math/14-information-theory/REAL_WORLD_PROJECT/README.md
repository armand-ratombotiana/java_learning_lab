# Real-World Project: Communication System Simulator

## Objective
Simulate a complete digital communication system from source coding through noisy channel to decoding.

## Architecture
1. **Source**: Random binary message generator
2. **Source Coding**: Huffman compression
3. **Channel Coding**: Hamming(7,4) or repetition code for error correction
4. **Modulation**: BPSK mapping (0 → +1, 1 → -1)
5. **Channel**: AWGN noise addition with configurable SNR
6. **Demodulation**: Hard decision or soft decision
7. **Channel Decoding**: Syndrome decoding for Hamming code
8. **Source Decoding**: Huffman decompression
9. **Analysis**: BER vs SNR, compare to Shannon limit

## Components
- Source (message generation, entropy calculation)
- SourceCoder (Huffman encode/decode)
- ChannelCoder (Hamming encode/decode)
- Modulator (BPSK)
- Channel (AWGN with configurable SNR)
- Demodulator
- ErrorAnalyzer (BER, mutual information)

## Evaluation Criteria
- Correct end-to-end transmission
- BER vs SNR curves (compare to uncoded)
- Performance near Shannon capacity
- Visualizations of signal constellations
- Analysis of coding gain
