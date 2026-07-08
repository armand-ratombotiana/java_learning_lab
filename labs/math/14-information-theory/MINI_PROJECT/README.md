# Mini-Project: File Compression Tool

## Objective
Build a file compression tool using Huffman coding.

## Requirements
1. Read any binary file as byte stream
2. Compute frequency table of symbols (bytes)
3. Build Huffman tree from frequencies
4. Generate prefix codes (bit strings)
5. Encode the file: replace each byte with its code
6. Pack bits into bytes for output
7. Store header (frequency table) for decoding
8. Decode compressed file back to original
9. Calculate compression ratio

## Extensions
- Implement adaptive Huffman coding (one-pass)
- Add LZ77 preprocessing before Huffman (deflate algorithm)
- Implement Shannon-Fano coding and compare ratios
- Add a GUI for file selection

## Evaluation Criteria
- Lossless compression/decompression
- Correct compression ratio calculation
- Handles binary and text files
- Performance analysis (speed, ratio)
