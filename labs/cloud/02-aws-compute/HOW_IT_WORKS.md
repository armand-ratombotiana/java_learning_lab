# How AWS Compute Works

## EC2 Instance Launch Cycle

1. **API call** → `RunInstances` with AMI, instance type, network config
2. **Nitro hypervisor** allocates physical resources (CPU cores, RAM, EBS attach)
3. **ENA** provisions networking (up to 100 Gbps)
4. **AMI root volume** created from snapshot on EBS
5. **Instance boots** — kernel loads, cloud-init runs user data
6. **SSH key injection** — public key written to ~/.ssh/authorized_keys
7. **Status checks** begin — 2/2 passed before ready

```java
// EC2 launch with user data script
String userData = Base64.getEncoder().encodeToString(
    ("#!/bin/bash\nyum install -y java-17-amazon-corretto\n" +
     "java -jar /opt/app.jar").getBytes());

RunInstancesRequest request = RunInstancesRequest.builder()
    .imageId("ami-xxx").instanceType(InstanceType.M5_LARGE)
    .userData(userData).maxCount(1).minCount(1)
    .build();
```

## Lambda Invocation

1. **Trigger**: S3 event, SQS message, API Gateway HTTP, schedule
2. **Cold start** (if needed): Download code, init runtime, run init code → ~200ms-5s
3. **Warm start**: Reuse existing execution environment → ~1-5ms
4. **Handler invoked**: Event deserialized into Java object
5. **Response**: Return serialized response or throw exception
6. **Freeze**: Environment frozen for ~5-15 min (may be reused)

```java
// Lambda handler (Java 17)
public class MyHandler implements RequestHandler<APIGatewayProxyRequestEvent, 
    APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent event, Context context) {
        String body = "Hello from Lambda!";
        return new APIGatewayProxyResponseEvent()
            .withStatusCode(200).withBody(body);
    }
}
```

## ECS Task Lifecycle

1. **Task definition** → JSON defining container image, CPU, memory, port mappings
2. **Service** → Desired count, load balancer, deployment config
3. **Scheduler** → Places tasks on EC2 instances (or Fargate assigns resources)
4. **Agent** → ECS agent on EC2 pulls image, configures networking
5. **Docker** → Container starts, health check runs
6. **ALB** → Registers container as target, routes traffic

## Elastic Beanstalk

1. **Upload** WAR/JAR or Dockerfile
2. **Platform** picks tomcat, Corretto, or Docker
3. **CloudFormation** creates EC2, ALB, ASG, RDS (if configured)
4. **Deployment** copies app to EC2 instances
5. **Health** monitors, replaces failed instances
