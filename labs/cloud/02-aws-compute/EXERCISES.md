# AWS Compute - Exercises

## Exercise 1: Lambda Basic Function
1. Create a Lambda function using Python runtime
2. Configure basic test event
3. Test function execution
4. View CloudWatch logs

## Exercise 2: Lambda with API Gateway
1. Create Lambda function for HTTP endpoint
2. Create API Gateway REST API
3. Configure integration
4. Test via browser and curl

## Exercise 3: Lambda with S3 Trigger
1. Create Lambda function
2. Configure S3 trigger for PUT events
3. Upload file to S3 bucket
4. Verify function execution and logs

## Exercise 4: Lambda Environment Variables
1. Set environment variables in Lambda
2. Access them in function code
3. Use Secrets Manager for sensitive data

## Exercise 5: Lambda Layers
1. Create a Lambda layer with dependencies
2. Attach layer to function
3. Test layer integration

## Exercise 6: ECS Cluster Setup
1. Create ECS cluster with Fargate
2. View cluster details
3. Understand task execution

## Exercise 7: ECS Task Definition
1. Create task definition for nginx
2. Configure port mappings
3. Set memory and CPU limits

## Exercise 8: ECS Service Deployment
1. Create service with 2 tasks
2. Configure network mode
3. Verify task running

## Exercise 9: ECS with ALB
1. Create Application Load Balancer
2. Configure target group
3. Link to ECS service
4. Test load balancing

## Exercise 10: EKS Cluster Creation
1. Create EKS cluster in Console
2. Configure VPC and subnets
3. Wait for cluster creation
4. Configure kubectl

## Exercise 11: EKS Node Group
1. Create node group
2. Add worker nodes
3. Verify node status

## Exercise 12: Kubernetes Deployment
1. Create Deployment manifest
2. Apply to EKS cluster
3. Verify pods running

## Exercise 13: Kubernetes Service
1. Create Service manifest
2. Expose application
3. Test connectivity

## Exercise 14: Lambda Cold Start Optimization
1. Create function with large dependencies
2. Measure cold start time
3. Implement provisioned concurrency
4. Compare performance

## Exercise 15: Lambda Retry Configuration
1. Configure dead letter queue
2. Test with failing function
3. Verify retry behavior

## Exercise 16: ECS Auto Scaling
1. Configure service auto scaling
2. Set scaling policies
3. Test scaling behavior

## Exercise 17: Lambda VPC Configuration
1. Configure Lambda in VPC
2. Access private resources
3. Understand ENI considerations

## Exercise 18: ECS Task Scheduling
1. Use Fargate scheduled tasks
2. Configure cron expression
3. Verify execution

## Exercise 19: Lambda Versioning and Aliases
1. Publish version
2. Create alias
3. Configure weighted routing

## Exercise 20: EKS Horizontal Pod Autoscaler
1. Deploy metrics server
2. Create HPA manifest
3. Configure scaling rules
4. Test scaling

## Exercise 21: Lambda Docker Image
1. Create Lambda function using container image
2. Push to ECR
3. Deploy and test

## Exercise 22: ECS Service Discovery
1. Enable service discovery
2. Configure DNS
3. Test service-to-service communication

## Exercise 23: Lambda Custom Runtime
1. Create custom runtime
2. Test function execution
3. Understand runtime lifecycle

## Exercise 24: EKS Ingress Controller
1. Install ALB Ingress Controller
2. Create Ingress resource
3. Configure routing rules

## Exercise 25: Lambda Concurrency Limits
1. Configure reserved concurrency
2. Test scaling limits
3. Understand throttling behavior