# RabbitMQ Solution

Reference implementation for RabbitMQ queue, exchange, binding, and DLQ.

## Connection
- `ConnectionFactory` for connection configuration
- `RabbitConnection` for connection management
- Host, port, username, password, virtual host

## Exchange
- Types: DIRECT, FANOUT, TOPIC, HEADERS
- Durable exchanges
- `exchangeDeclare`, `queueBind`

## Queue
- Durable, exclusive, auto-delete options
- Bindings to exchanges with routing keys

## Dead Letter Queue (DLQ)
- Automatic message routing on failure
- DLQ queue and exchange setup

## Producer & Consumer
- `Producer` for sending messages
- `Consumer` with callback handling

## Retry Policy
- Configurable retry attempts
- Exponential backoff with max interval

## Test Coverage: 25+ tests