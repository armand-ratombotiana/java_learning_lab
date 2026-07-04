# Observability - MENTAL_MODELS

## Mental Model 1: The Car Dashboard
- **Speedometer = Metrics**: How fast are we going? (RPS, latency)
- **Check Engine Light = Alerts**: Something is wrong (error rate spikes)
- **GPS History = Traces**: Where have we been? (request path through services)
- **Engine Log = Logs**: Detailed history of each component
- **Fuel Gauge = Capacity**: How much headroom do we have?

## Mental Model 2: The Hospital
- **Vital signs = Metrics**: Heart rate, blood pressure (CPU, memory, latency)
- **Medical history = Logs**: Detailed patient records (structured log entries)
- **Diagnostic scan = Tracing**: Follow the issue through body systems
- **Monitor alarm = Alert**: Nurse call button (threshold breached)
- **Daily rounds = Dashboards**: Check all patients (Grafana overview)

## Mental Model 3: The Flight Black Box
- **Flight Data Recorder = Metrics**: Altitude, speed, heading (performance data)
- **Cockpit Voice Recorder = Logs**: Every conversation in cockpit (detailed events)
- **Flight path = Trace**: Complete journey from departure to arrival

## The Three Pillars

```
OBSERVABILITY = LOGS + METRICS + TRACES
                    │
     ┌──────────────┼──────────────┐
     ▼              ▼              ▼
┌─────────┐  ┌──────────┐  ┌──────────┐
│  LOGS   │  │ METRICS  │  │ TRACES   │
├─────────┤  ├──────────┤  ├──────────┤
│ What    │  │ What     │  │ Why      │
│ happened│  │ is       │  │ did it   │
│         │  │ happening│  │ happen?  │
│         │  │ now?     │  │          │
├─────────┤  ├──────────┤  ├──────────┤
│ "Error  │  │ 98% CPU  │  │ Span A → │
│  saving │  │ 500 req/s│  │ Span B → │
│  order" │  │ P95=2s  │  │ Span C   │
└─────────┘  └──────────┘  └──────────┘
```

## RED Method (Services)
Rate of requests, Errors of requests, Duration of requests.

## USE Method (Resources)
Utilization, Saturation, Errors.

## Golden Signals (Google SRE)
Latency, Traffic, Errors, Saturation.
