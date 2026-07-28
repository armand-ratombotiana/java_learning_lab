# Lab 07: RLHF & Preference Optimization Guide

## Step 1: Preference Data
Format paired comparisons (chosen vs rejected responses).

## Step 2: Reward Model
Train a model to predict which response is better.

## Step 3: PPO Simulation
Implement clipped surrogate objective for policy updates.

## Step 4: KL Divergence
Add KL penalty to prevent policy from drifting too far.

## Step 5: DPO Concept
Implement Direct Preference Optimization loss.

## Compile & Run
```bash
cd lab07/src
javac com/genai/lab07/Main.java
java com.genai.lab07.Main
```
