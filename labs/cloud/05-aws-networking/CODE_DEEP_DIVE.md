# AWS Networking - Code Deep Dive

## Route 53 Operations

```python
import boto3

route53 = boto3.client('route53')

# Create hosted zone
response = route53.create_hosted_zone(
    Name='example.com.',
    CallerReference='unique-reference'
)

hosted_zone_id = response['HostedZone']['Id']

# Create record
route53.change_resource_record_sets(
    HostedZoneId=hosted_zone_id,
    ChangeBatch={
        'Changes': [{
            'Action': 'CREATE',
            'ResourceRecordSet': {
                'Name': 'www.example.com.',
                'Type': 'A',
                'TTL': 300,
                'ResourceRecords': [{'Value': '1.2.3.4'}]
            }
        }]
    }
)

# Create weighted record
route53.change_resource_record_sets(
    HostedZoneId=hosted_zone_id,
    ChangeBatch={
        'Changes': [{
            'Action': 'CREATE',
            'ResourceRecordSet': {
                'Name': 'weighted.example.com.',
                'Type': 'A',
                'SetIdentifier': 'primary',
                'Weight': 80,
                'TTL': 300,
                'ResourceRecords': [{'Value': '1.2.3.4'}]
            }
        }]
    }
)

# Create failover record
route53.change_resource_record_sets(
    HostedZoneId=hosted_zone_id,
    ChangeBatch={
        'Changes': [{
            'Action': 'CREATE',
            'ResourceRecordSet': {
                'Name': 'failover.example.com.',
                'Type': 'A',
                'Failover': 'PRIMARY',
                'SetIdentifier': 'primary',
                'TTL': 30,
                'ResourceRecords': [{'Value': '1.2.3.4'}]
            },
            {
                'Action': 'CREATE',
                'ResourceRecordSet': {
                    'Name': 'failover.example.com.',
                    'Type': 'A',
                    'Failover': 'SECONDARY',
                    'SetIdentifier': 'secondary',
                    'TTL': 30,
                    'ResourceRecords': [{'Value': '5.6.7.8'}]
                }
            }]
    }
)

# Create health check
response = route53.create_health_check(
    CallerReference='health-check-1',
    HealthCheckConfig={
        'Type': 'HTTPS',
        'FullyQualifiedDomainName': 'example.com',
        'Port': 443,
        'ResourcePath': '/health',
        'RequestInterval': 30,
        'FailureThreshold': 3
    }
)

# Alias record to ALB
route53.change_resource_record_sets(
    HostedZoneId=hosted_zone_id,
    ChangeBatch={
        'Changes': [{
            'Action': 'CREATE',
            'ResourceRecordSet': {
                'Name': 'app.example.com.',
                'Type': 'A',
                'AliasTarget': {
                    'HostedZoneId': 'Z2FDTNDATAQYW2',
                    'DNSName': 'my-alb-123456.us-east-1.elb.amazonaws.com.',
                    'EvaluateTargetHealth': True
                }
            }
        }]
    }
)
```

## CloudFront Operations

```python
import boto3

cloudfront = boto3.client('cloudfront')

# Create distribution
response = cloudfront.create_distribution(
    Origin={
        'DomainName': 'mybucket.s3.amazonaws.com',
        'Id': 'S3-my-bucket',
        'S3OriginConfig': {'OriginAccessIdentity': ''}
    },
    CallerReference='distribution-1',
    Comment='My CloudFront distribution',
    Enabled=True,
    DefaultRootObject='index.html',
    DefaultCacheBehavior={
        'TargetOriginId': 'S3-my-bucket',
        'ViewerProtocolPolicy': 'redirect-to-https',
        'AllowedMethods': {'Items': ['GET', 'HEAD'], 'CachedMethods': ['GET', 'HEAD']},
        'Compress': True,
        'ForwardedValues': {
            'QueryString': False,
            'Cookies': {'Forward': 'none'}
        }
    },
    PriceClass='PriceClass_100'
)

# Create signed URL
signed_url = cloudfront.sign_cookie(
    url='https://d1234567890.cloudfront.net/private/content.mp4',
    policy=None,
    key_pair_id='K2CJMH2H1L2EXAMPLE',
    private_key='-----BEGIN RSA PRIVATE KEY-----\n...'
)

# Create invalidation
cloudfront.create_invalidation(
    DistributionId='E1234567890ABC',
    InvalidationBatch={
        'CallerReference': 'invalidation-1',
        'Paths': {'Items': ['/images/*.jpg', '/index.html'], 'Quantity': 2}
    }
)

# Update distribution
cloudfront.update_distribution(
    Id='E1234567890ABC',
    DistributionConfig={
        'Enabled': True,
        'Comment': 'Updated distribution'
    },
    IfMatch='etag-value'
)
```

## ALB Operations

```python
import boto3

elbv2 = boto3.client('elbv2')

# Create ALB
response = elbv2.create_load_balancer(
    Name='my-alb',
    Scheme='internet-facing',
    SecurityGroups=['sg-12345678'],
    Subnets=['subnet-12345', 'subnet-67890'],
    Type='application'
)

alb_arn = response['LoadBalancers'][0]['LoadBalancerArn']

# Create target group
response = elbv2.create_target_group(
    Name='my-targets',
    Protocol='HTTP',
    Port=80,
    VpcId='vpc-12345678',
    HealthCheckPath='/health',
    HealthCheckIntervalSeconds=30,
    HealthyThresholdCount=2,
    UnhealthyThresholdCount=2
)

target_group_arn = response['TargetGroups'][0]['TargetGroupArn']

# Register targets
elbv2.register_targets(
    TargetGroupArn=target_group_arn,
    Targets=[
        {'Id': 'i-1234567890abcdef0', 'Port': 80},
        {'Id': 'i-abcdef1234567890', 'Port': 80}
    ]
)

# Create listener with rules
response = elbv2.create_listener(
    LoadBalancerArn=alb_arn,
    Protocol='HTTPS',
    Port=443,
    Certificates=[{'CertificateArn': 'arn:aws:acm:...'}],
    DefaultActions=[{
        'Type': 'forward',
        'TargetGroupArn': target_group_arn
    }]
)

# Add path-based rule
elbv2.create_rule(
    ListenerArn=response['Listeners'][0]['ListenerArn'],
    Conditions=[{'Field': 'path-pattern', 'Values': ['/api/*']}],
    Priority=1,
    Actions=[{
        'Type': 'forward',
        'TargetGroupArn': 'arn:aws:elasticloadbalancing:...'
    }]
)

# Enable sticky sessions
elbv2.modify_target_group_attributes(
    TargetGroupArn=target_group_arn,
    Attributes=[{'Key': 'stickiness.enabled', 'Value': 'true'}]
)
```