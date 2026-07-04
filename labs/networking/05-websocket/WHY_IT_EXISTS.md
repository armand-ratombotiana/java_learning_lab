# WebSocket - Why It Exists

## The Problem WebSocket Solved

Traditional HTTP is request-response based - the server cannot push data without the client requesting it. Solutions like polling and long-polling are inefficient.

## HTTP Polling vs WebSocket
| Aspect | HTTP Polling | WebSocket |
|--------|-------------|-----------|
| Latency | Poll interval (1-30s) | Real-time (<100ms) |
| Overhead | HTTP headers per request | Minimal frame overhead |
| Bandwidth | Significant (frequent requests) | Efficient (push-based) |
| Server load | Higher (handles many poll requests) | Lower (event-driven) |

## Use Cases
- Real-time chat applications
- Live sports scores and tickers
- Collaborative editing (Google Docs)
- Financial trading platforms
- Multiplayer games
- Real-time IoT updates
