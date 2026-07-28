# Lab 09: Real-Time Analytics

## Overview

Build real-time analytics infrastructure — streaming aggregations, windowed computations, materialized views, and near-real-time dashboards.

## Learning Objectives

- Implement tumbling, hopping, and sliding window aggregations
- Build a materialized view that updates in real-time
- Design a real-time dashboard data pipeline
- Handle late data and out-of-order events

## Key Concepts

- **Window Types**: Tumbling (fixed), Hopping (overlapping), Sliding (per-event)
- **Watermark**: Tracking event-time progress for late data handling
- **Materialized Views**: Pre-computed aggregations served via KV store
- **Stream-Table Join**: Enriching streams with slowly changing dimensions
- **Exactly-Once**: Ensuring accurate counts in real-time dashboards
