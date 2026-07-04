# Scalability - MENTAL_MODELS

## Mental Model 1: The Restaurant
- **Vertical scaling**: One chef working faster
- **Horizontal scaling**: Add more chefs in parallel
- **Load balancing**: Hostess distributing customers to available chefs
- **Database sharding**: Each chef has their own ingredient station
- **Caching**: Pre-prepared ingredients that speed up any chef

## Mental Model 2: The Highway
- **Bandwidth**: Number of lanes (throughput)
- **Latency**: Speed limit (response time)
- **Traffic jam**: Congestion at peak load
- **Load balancer**: Toll booth distributing cars
- **CDN**: Local exits so you don't drive to the city center

## Mental Model 3: The Water Pipe
- **Pipe diameter**: System throughput
- **Water pressure**: Request rate
- **Leaks**: Resource leaks under load
- **Pump**: Load balancer pushing water through
- **Reservoir**: Buffer/queue absorbing bursts

## Scaling Dimensions (The Cube Model)

```
              ┌──────────────┐
              │  X-Axis      │
              │  Horizontal  │
              │  Cloning     │
              └──────────────┘
                      │
┌──────────────┐     ┌──────────────┐
│  Z-Axis      │─────│  Y-Axis      │
│  Partition   │     │  Functional  │
│  (Sharding)  │     │  (Services)  │
└──────────────┘     └──────────────┘
```

- **X-Axis**: Run multiple copies behind load balancer (stateless)
- **Y-Axis**: Split by function (microservices)
- **Z-Axis**: Split by data (shard by customer ID, region)

## The Scale-Up vs Scale-Out Decision

```
Can the system be stateless? ──► Yes → Scale-out (preferred)
        │ No
        ▼
Is state easily partitioned? ──► Yes → Shard (z-axis)
        │ No
        ▼
Can we use a distributed cache/DB? ──► Yes → Scale with managed service
        │ No
        ▼
Vertical scaling (last resort)
```
