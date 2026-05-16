# AWS Compute - Code Deep Dive

## Lambda Functions

```python
import json
import boto3

def lambda_handler(event, context):
    # Access request parameters
    http_method = event['httpMethod']
    path = event['path']
    
    # Parse body if present
    body = event.get('body', '')
    
    # Get identity
    identity = event.get('requestContext', {}).get('identity', {})
    
    # Parse query parameters
    params = event.get('queryStringParameters', {})
    
    # Return response
    return {
        'statusCode': 200,
        'headers': {
            'Content-Type': 'application/json',
            'Access-Control-Allow-Origin': '*'
        },
        'body': json.dumps({
            'message': 'Hello from Lambda!',
            'method': http_method,
            'path': path
        })
    }

# Environment variables usage
def lambda_with_env(event, context):
    table_name = os.environ.get('TABLE_NAME')
    dynamodb = boto3.resource('dynamodb')
    table = dynamodb.Table(table_name)
    
    # Process event
    item_id = event['pathParameters']['id']
    response = table.get_item(Key={'id': item_id})
    
    return {
        'statusCode': 200,
        'body': json.dumps(response.get('Item', {}))
    }

# Layer usage
def lambda_with_layers(event, context):
    import pandas as pd  # From layer
    import numpy as np   # From layer
    
    data = pd.DataFrame(event['data'])
    result = data.describe()
    return {'statusCode': 200, 'body': json.dumps(result.to_dict())}
```

## Lambda with AWS Services

```python
import boto3

# DynamoDB Integration
def lambda_dynamodb(event, context):
    dynamodb = boto3.resource('dynamodb')
    table = dynamodb.Table('my-table')
    
    # Put item
    table.put_item(Item={'id': '1', 'data': 'test'})
    
    # Query
    response = table.query(
        KeyConditionExpression=Key('id').eq('1')
    )
    
    return {'statusCode': 200, 'body': json.dumps(response['Items'])}

# S3 Trigger
def lambda_s3_trigger(event, context):
    s3 = boto3.client('s3')
    
    for record in event['Records']:
        bucket = record['s3']['bucket']['name']
        key = record['s3']['object']['key']
        
        # Get object
        response = s3.get_object(Bucket=bucket, Key=key)
        content = response['Body'].read()
        
        # Process (e.g., resize image, parse CSV)
        print(f"Processed {key} from {bucket}")
    
    return {'statusCode': 200}

# Step Functions Integration
def lambda_start_stepfunctions(event, context):
    stepfunctions = boto3.client('stepfunctions')
    
    response = stepfunctions.start_execution(
        stateMachineArn='arn:aws:states:us-east-1:123456789:stateMachine:MyStateMachine',
        input=json.dumps(event)
    )
    
    return {'executionArn': response['executionArn']}
```

## ECS Task Definitions

```json
{
    "family": "my-web-app",
    "networkMode": "awsvpc",
    "requiresCompatibilities": ["FARGATE"],
    "cpu": "256",
    "memory": "512",
    "executionRoleArn": "arn:aws:iam::123456789:role/ecsTaskExecutionRole",
    "containerDefinitions": [
        {
            "name": "web",
            "image": "nginx:latest",
            "essential": true,
            "portMappings": [
                {
                    "containerPort": 80,
                    "protocol": "tcp"
                }
            ],
            "environment": [
                {"name": "ENV", "value": "production"}
            ],
            "logConfiguration": {
                "logDriver": "awslogs",
                "options": {
                    "awslogs-group": "/ecs/my-web-app",
                    "awslogs-region": "us-east-1",
                    "awslogs-stream-prefix": "ecs"
                }
            }
        }
    ]
}
```

## ECS Service Creation

```python
import boto3

ecs = boto3.client('ecs')

# Create Cluster
response = ecs.create_cluster(
    clusterName='my-cluster',
    capacityProviders=['FARGATE']
)

# Register Task Definition
response = ecs.register_task_definition(
    family='my-web-app',
    networkMode='awsvpc',
    requiresCompatibilities=['FARGATE'],
    cpu='256',
    memory='512',
    executionRoleArn='arn:aws:iam::123456789:role/ecsTaskExecutionRole',
    containerDefinitions=[{
        'name': 'web',
        'image': 'nginx:latest',
        'essential': True,
        'portMappings': [{
            'containerPort': 80,
            'protocol': 'tcp'
        }]
    }]
)

# Create Service
response = ecs.create_service(
    cluster='my-cluster',
    serviceName='my-service',
    taskDefinition='my-web-app:1',
    desiredCount=2,
    launchType='FARGATE',
    networkConfiguration={
        'awsvpcConfiguration': {
            'subnets': ['subnet-123', 'subnet-456'],
            'securityGroups': ['sg-123']
        }
    },
    loadBalancers=[{
        'targetGroupArn': 'arn:aws:elasticloadbalancing:...',
        'containerName': 'web',
        'containerPort': 80
    }]
)
```

## EKS Deployment

```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app
  labels:
    app: my-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: my-app
  template:
    metadata:
      labels:
        app: my-app
    spec:
      containers:
      - name: my-app
        image: my-image:latest
        ports:
        - containerPort: 8080
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
---
apiVersion: v1
kind: Service
metadata:
  name: my-app-service
spec:
  selector:
    app: my-app
  ports:
  - port: 80
    targetPort: 8080
  type: LoadBalancer
```

```python
# AWS SDK for Kubernetes operations
import boto3

# Create EKS Cluster
eks = boto3.client('eks')

response = eks.create_cluster(
    name='my-cluster',
    version='1.27',
    roleArn='arn:aws:iam::123456789:role/eks-role',
    resourcesVpcConfig={
        'subnetIds': ['subnet-1', 'subnet-2'],
        'securityGroupIds': ['sg-1']
    }
)

# Add Node Group
eks.create_nodegroup(
    clusterName='my-cluster',
    nodegroupName='my-nodes',
    scalingConfig={
        'minSize': 2,
        'maxSize': 4,
        'desiredSize': 2
    },
    instanceTypes=['t3.medium'],
    nodeRole='arn:aws:iam::123456789:role/eks-node-role'
)
```