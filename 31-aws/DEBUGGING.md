# Debugging AWS SDK Issues

## Common Failure Scenarios

### Credentials and Authentication Failures

Requests fail with access denied or signature mismatch errors. Applications cannot access AWS resources. This indicates credential configuration problems or permission issues.

The AWS credentials may be missing or expired. Check credential chain order: environment variables, shared credentials file, instance profile. Verify credentials are valid with `aws sts get-caller-identity`.

IAM role permissions may be insufficient. The role attached to EC2/ECS/Lambda lacks required policies. Check CloudTrail for authorization errors. Add the necessary IAM policy permissions.

### SDK Timeout and Throttling

SDK operations timeout or receive throttling errors. Requests fail with "Rate exceeded" or "ThrottlingException". Response latency increases under load. This indicates SDK configuration or service limits.

Default SDK timeouts may be too short. Increase client configuration timeouts for slow operations. Set appropriate retry policies with exponential backoff.

Service quotas may be hit for the account. Check AWS service quotas and request increases if needed. Implement request queuing to smooth demand spikes.

### Stack Trace Examples

**Invalid credentials:**
```
com.amazonaws.services.s3.model.AmazonS3Exception: The AWS Access Key Id you provided does not exist in our records
    at com.amazonaws.services.s3.internal.S3ErrorResponseHandler.handleError(S3ErrorResponseHandler.java:123)
```

**Access denied:**
```
software.amazon.awssdk.services.s3.model.S3AccessDeniedException: Access Denied
    at software.amazon.awssdk.core.internal.http.pipeline.stages.HandleErrorStage.handleError(HandleErrorStage.java:67)
```

**Throttling exception:**
```
software.amazon.awssdk.services.dynamodb.model.ThrottlingException: Rate exceeded
    at software.amazon.awssdk.services.dynamodb.model.DynamoDbException$Builder.build(DynamoDbException.java:345)
```

## Debugging Techniques

### Enabling SDK Logging

Enable AWS SDK logging to see request/response details. Configure logging at DEBUG or TRACE level. Log the full request including headers and payload.

Review HTTP request and response pairs. Check for retry attempts and their causes. Look for credential signing details and timing.

Use request metrics to track performance. Enable SDK metrics collection to see latency percentiles. Monitor retry rates and error counts.

### Credential Chain Debugging

Use AWS CLI to verify credentials work: `aws sts get-caller-identity`. This confirms the credentials are valid and shows the identity.

Check instance metadata for EC2 instance profile. Use `curl` to query the metadata endpoint. Verify the role name and attached policies.

Review CloudTrail for API call details. Look for access denied errors and the associated IAM policy. Check for service-specific permission requirements.

## Best Practices

Use IAM roles instead of access keys. Attach roles to EC2 instances, ECS tasks, or Lambda functions. Rotate credentials regularly.

Configure appropriate retry behavior. Use exponential backoff with jitter. Set reasonable timeout values per operation.

Use regional clients to reduce latency. Consider using VPC endpoints for private resources. Implement circuit breakers for resilience.