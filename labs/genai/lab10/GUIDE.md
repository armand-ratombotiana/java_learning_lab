# Lab 10: LLM Safety & Alignment Guide

## Step 1: Deny List Guardrail
Define blocked topics/keywords; reject matching requests.

## Step 2: Regex-Based Content Filter
Filter PII, profanity, and sensitive patterns.

## Step 3: Prompt Injection Detector
Classify whether user input attempts to override system prompt.

## Step 4: Output Guardrails
Validate model output before returning to user.

## Step 5: Red-Teaming Simulation
Generate adversarial inputs to test guardrails.

## Compile & Run
```bash
cd lab10/src
javac com/genai/lab10/Main.java
java com.genai.lab10.Main
```
