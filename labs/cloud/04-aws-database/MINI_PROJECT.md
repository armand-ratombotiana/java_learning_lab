# AWS Database - Mini Project

## Project: Serverless Todo API with DynamoDB

### Objective
Build serverless REST API using Lambda and DynamoDB.

### Architecture
```
API Gateway → Lambda → DynamoDB
```

### Steps

**Step 1: Create DynamoDB Table**
- Table name: `todos`
- Partition key: `id` (String)

**Step 2: Create Lambda Function**
```python
import json
import boto3
from uuid import uuid4

dynamodb = boto3.resource('dynamodb')
table = dynamodb.Table('todos')

def create_todo(event):
    data = json.loads(event['body'])
    todo_id = str(uuid4())
    
    table.put_item(Item={
        'id': todo_id,
        'title': data['title'],
        'completed': False,
        'created_at': '2024-01-01T00:00:00Z'
    })
    
    return {'statusCode': 201, 'body': json.dumps({'id': todo_id})}

def get_todos(event):
    response = table.scan()
    return {'statusCode': 200, 'body': json.dumps(response['Items'])}
```

**Step 3: Configure API Gateway**
- Create REST API
- Add /todos GET and POST methods
- Integrate with Lambda
- Deploy to stage

**Step 4: Test API**
- POST /todos - Create todo
- GET /todos - List todos

### Deliverables
1. Working REST API
2. DynamoDB CRUD operations
3. Test results

### Estimated Time
60 minutes