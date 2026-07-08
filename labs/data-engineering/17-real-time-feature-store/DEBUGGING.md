# Debugging Real-Time Feature Store

## Missing Features at Serving
Check entity key exists in online store; verify materialization ran; check key format

## Slow Serving
Optimize online store connection; use batch retrieval (multiple entities per request); cache frequently accessed features

## Inconsistent Results
Compare online and offline values for same entity; check materialization freshness; verify transformation logic

## Point-in-Time Errors
Verify label and feature timestamps; check for null timestamps; validate join condition
