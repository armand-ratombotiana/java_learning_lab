# Debugging — AWS Compute

## Lambda Debugging

### Function Timeout
```
Symptom: "Task timed out after 30.00 seconds"
Check:
  1. Is VPC configured? (ENI creation adds 10s+)
  2. DB connection timeout? (increase connection timeout)
  3. HTTP call to external API? (increase function timeout)
  4. Tiered: start with 30s, increase if needed (max 900s)
```

### Out of Memory
```
Symptom: "RequestId: ... Error: Runtime exited with error: signal: killed"
Check:
  - Memory setting too low (Java typically needs 512MB+)
  - Heap dump to /tmp, analyze with Eclipse MAT
  - Increase memory (also increases CPU allocation)

Fix: Configure with 1024MB+ for Java functions
```

### Cold Start Too Long
```
Symptom: P50 = 50ms, P99 = 5000ms
Check:
  - X-Ray trace: Init phase duration
  - Jar size: >30MB? Use layers or GraalVM native-image
  - SnapStart enabled? (reduces Init from 3-5s to ~200ms)
  - VPC enabled? (adds 5-10s for ENI creation)
```

## ECS Debugging

### Container Exit Code
```
Symptom: Task stops immediately (last status = STOPPED)
Check: aws ecs describe-tasks --cluster prod --tasks arn:xxx
  - Exit code 137: Out of memory
  - Exit code 1/130: Application error
  - Exit code 0: Intentional stop (completed job)

Fix: aws ecs describe-tasks --tasks $TASK_ARN
  Look for "stoppedReason" field
```

### Cannot Pull Container Image
```
Symptom: "CannotPullContainerError: pull access denied"
Check:
  - ECR repository exists and URI is correct
  - Task execution role has ecr:GetDownloadUrlForLayer
  - Image tag exists (not just :latest when expecting :1.0.0)
  - ECR requires authentication (ECS agent handles this with task role)
```

### Port Mapping Mismatch
```
Symptom: ALB returns 502, container running
Check:
  - Task definition: containerPort matches application port
  - Application listening on 0.0.0.0 (not localhost)
  - Security group allows ALB traffic
  - Health check path returns 200
```

## Elastic Beanstalk Debugging

### Deployment Failure
```
Symptom: Environment goes from green to red after deploy
Check:
  - /var/log/eb-activity.log (CloudWatch log group)
  - /var/log/tomcat8/catalina.out
  - eb events --environment-name prod (deployment status)
  - Environment health = Severe; check "causes" field
```

### Instance Termination
```
Symptom: Instances keep getting terminated
Check: aws elasticbeanstalk describe-environment-health
  --environment-name prod --attribute-names Causes
  - Unhealthy ALB targets? check health check path
  - Insufficient capacity? check ASG settings
  - Immutable deployment failed? check previous deployment logs
```
