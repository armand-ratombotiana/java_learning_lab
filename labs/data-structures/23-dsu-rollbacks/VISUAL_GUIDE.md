# Visual Guide to Disjoint Set Union with Rollbacks

## Diagrams and Visualizations

This guide provides visual explanations. For actual diagrams, see the DIAGRAMS directory.

## Conceptual Visualization

### Structure Overview

Imagine a multi-level organization where the top level contains summary or index information, middle levels provide routing and organization, and the bottom level stores actual elements.

### Element Placement

Elements flow through a series of filters, are sorted into bins based on properties, and follow paths through a decision tree.

## Operation Visualizations

### Insertion Flow

An element enters at the top level, navigates through intermediate levels, settles into its final position, and the structure may reorganize.

### Lookup Path

A query enters at the top, follows a guided path through the hierarchy, and reaches the target element or determines absence.

## Common Patterns

### The Recursive Pattern

The structure exhibits self-similarity at different scales where small sub-structures mirror the larger structure.

### The Balance Pattern

Balance is maintained through splitting full nodes, merging sparse nodes, and redistributing among siblings.

## Memory Layout Visualization

Elements are arranged to maximize spatial locality, temporal locality, and sequential access patterns.
