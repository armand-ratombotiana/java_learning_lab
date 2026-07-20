# Reactive Programming Deep - Micro-Labs

## Overview
This module contains 5 deep-dive micro-labs covering Reactive Streams, Project Reactor, WebFlux, RSocket, and reactive testing.

## Lab Index
| # | Lab | Description |
|---|-----|-------------|
| 01 | [Reactive Streams](./01-reactive-streams/) | Publisher/Subscriber/Subscription/Processor, backpressure, request(n), onSubscribe/onNext/onError/onComplete contracts |
| 02 | [Project Reactor](./02-project-reactor/) | Flux/Mono, map/flatMap/concatMap, publishOn/subscribeOn, Schedulers, backpressure strategies, hot vs cold publishers |
| 03 | [WebFlux Deep Dive](./03-webflux-deep/) | @RestController vs RouterFunction, WebClient, SSE, EventLoop, connection pooling, Reactor Netty internals |
| 04 | [RSocket](./04-rsocket/) | RSocket interaction models (request/response, fire-and-forget, streaming, channel), setup payload, routing, resumability |
| 05 | [Reactive Testing](./05-reactive-testing/) | StepVerifier, virtual time (StepVerifier.withVirtualTime), TestPublisher, OperatorDebug, Reactor DebugAgent |

## How to Use
Each micro-lab is self-contained with 24 markdown files, Java source code, JUnit 5 tests, and 7 subdirectories for hands-on work. Start from lab 01 and progress sequentially.
