# HTTP Protocol - Architecture

## Layered Architecture

```
+-----------------------------+
|      Client Application     |
|  +-----------------------+  |
|  |  HTTP Client Library  |  |
|  +----------+------------+  |
+-------------+---------------+
              | HTTP/HTTPS
              v
+-----------------------------+
|  Load Balancer / Proxy     |
|  (Nginx, HAProxy)          |
|  - SSL termination          |
|  - Load balancing           |
|  - Rate limiting            |
+----------+------------------+
           | HTTP
           v
+-----------------------------+
|  API Gateway                |
|  - Routing                  |
|  - Authentication           |
|  - Rate limiting            |
+----------+------------------+
           | HTTP
           v
+-----------------------------+
|  Microservice               |
|  +----------+ +----------+ |
|  |Controller| | Service  | |
|  +----------+ +----+-----+ |
|                   |       |
|  +----------------+-----+ |
|  |    Database / Cache   | |
|  +-----------------------+ |
+-----------------------------+
```

## Connection Flow in Microservices

```
Service A --HTTP--> API Gateway --HTTP--> Service B
     |                                       |
     v                                       v
Service Registry                  Service Registry
  (Eureka)                          (Eureka)
```

## Caching Architecture

```
Client --> CDN (Cloudflare, CloudFront)
             |--- Cache Hit --> Return cached
             |--- Cache Miss --> Origin Server
                                 |-- Redis
                                 |-- Database
```
