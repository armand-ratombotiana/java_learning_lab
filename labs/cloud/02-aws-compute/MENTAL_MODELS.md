# Mental Models for AWS Compute

## 1. The Compute Spectrum
```
Full Control ──────────────────────────── Zero Ops
    │              │            │              │
   EC2           ECS/EKS      Fargate       Lambda
(VM + OS)     (Container)  (Serverless     (Function)
                              Container)
    │              │            │              │
 You manage     You manage   You manage    You manage
 OS, runtime   containers   containers    code only
```

## 2. The Function as a Microservice
Each Lambda function = one endpoint = one responsibility. Like a Unix tool:
- Does one thing well
- Reads from stdin (event), writes to stdout (response)
- Composable with pipes (event-driven chains)

## 3. The Autoscaling Corollary
EC2 Auto Scaling = thermostat:
- Set desired temperature (desired capacity)
- When too hot (CPU high), add AC units (scale out)
- When cold (CPU low), remove AC units (scale in)

Lambda scaling = water pipes:
- More demand = more water flow = more concurrent executions
- Burst: 500-3000 concurrent executions per minute
- No provisioning needed

## 4. The Container Sandwich
```
┌─────────────────────────────────────────┐
│            Docker Container              │
│  ┌─────────────────────────────────────┐│
│  │         Java Application (JAR)       ││
│  │  ┌───────────────────────────────┐  ││
│  │  │    Spring Boot / Quarkus      │  ││
│  │  └───────────────────────────────┘  ││
│  └─────────────────────────────────────┘│
│  ┌─────────────────────────────────────┐│
│  │      JVM (Java 17/21)               ││
│  └─────────────────────────────────────┘│
│  ┌─────────────────────────────────────┐│
│  │     Amazon Linux / Alpine           ││
│  └─────────────────────────────────────┘│
└─────────────────────────────────────────┘
```
