# Lab 04: RAG System Design Guide

## Step 1: Document Chunking
Implement fixed-size, overlap, and semantic chunking strategies.

## Step 2: Embedding Model (Simulated)
Create dense vector representations for text chunks.

## Step 3: Vector Index & Retrieval
Build an in-memory vector store with cosine similarity search.

## Step 4: Augmentation
Inject retrieved chunks into a prompt template.

## Step 5: Generation Pipeline
Combine retrieval + augmentation + generation into a RAG pipeline.

## Compile & Run
```bash
cd lab04/src
javac com/genai/lab04/Main.java
java com.genai.lab04.Main
```
