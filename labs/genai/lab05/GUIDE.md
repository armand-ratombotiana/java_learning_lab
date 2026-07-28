# Lab 05: LLM Agent Frameworks Guide

## Step 1: Tool Definition
Define a `Tool` interface with name, description, and execute method.

## Step 2: Tool Registry
Create a registry that maps tool names to implementations.

## Step 3: ReAct Agent Loop
Implement Think → Act → Observe cycle with max iterations.

## Step 4: Memory & Context
Accumulate thought traces and observations across steps.

## Step 5: Agent Orchestration
Run the agent with a goal, collect final answer.

## Compile & Run
```bash
cd lab05/src
javac com/genai/lab05/Main.java
java com.genai.lab05.Main
```
