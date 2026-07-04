# Scalability - WHY IT EXISTS

## Problem Statement
Applications that work for 100 users often fail at 10,000. Performance degrades, timeouts occur, and systems crash under load without intentional scalability design.

## Origin
Scalability became a critical concern with the rise of web applications in the late 1990s. Early systems scaled vertically (bigger machines) but hit physical limits, driving the need for horizontal scaling.

## Core Drivers
- **User growth**: More users → more concurrent requests
- **Data growth**: More transactions → larger databases
- **Geographic distribution**: Global users need local performance
- **Cost efficiency**: Scale only what's needed, when needed

## Why Not Just Buy Bigger Hardware?
Vertical scaling has hard limits:
- CPU clock speeds plateaued around 5 GHz
- Memory channels per socket are limited
- Single machine cost grows super-linearly
- No fault tolerance at the machine level

## Java Ecosystem
Java's thread-per-request model (traditional) and reactive model (WebFlux) both address scalability differently. Virtual threads (Project Loom) offer a new path to handling high concurrency with simple blocking code.
