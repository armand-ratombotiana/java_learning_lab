# AWS Database - Code Deep Dive

## RDS Operations

```python
import boto3
import pymysql

# Create RDS instance
rds = boto3.client('rds')

response = rds.create_db_instance(
    DBInstanceIdentifier='my-database',
    Engine='mysql',
    DBInstanceClass='db.t3.micro',
    MasterUsername='admin',
    MasterUserPassword='password123',
    AllocatedStorage=20,
    StorageType='gp3',
    MultiAZ=False,
    PubliclyAccessible=True,
    VpcSecurityGroupIds=['sg-12345678']
)

# Connect to RDS
connection = pymysql.connect(
    host='my-database.123456789.us-east-1.rds.amazonaws.com',
    user='admin',
    password='password123',
    database='mydb'
)

# Execute query
with connection.cursor() as cursor:
    cursor.execute("CREATE TABLE users (id INT, name VARCHAR(255))")
    cursor.execute("INSERT INTO users VALUES (1, 'John')")
    connection.commit()

# Create read replica
rds.create_db_instance_read_replica(
    DBInstanceIdentifier='my-database-replica',
    SourceDBInstanceIdentifier='my-database',
    DBInstanceClass='db.t3.micro'
)

# Create snapshot
rds.create_db_snapshot(
    DBSnapshotIdentifier='my-snapshot',
    DBInstanceIdentifier='my-database'
)

# Restore from snapshot
rds.restore_db_instance_from_db_snapshot(
    DBInstanceIdentifier='restored-database',
    DBSnapshotIdentifier='my-snapshot'
)

# Modify instance
rds.modify_db_instance(
    DBInstanceIdentifier='my-database',
    ApplyImmediately=True,
    BackupRetentionPeriod=7
)
```

## DynamoDB Operations

```python
import boto3

dynamodb = boto3.resource('dynamodb')

# Create table
table = dynamodb.create_table(
    TableName='users',
    KeySchema=[
        {'AttributeName': 'user_id', 'KeyType': 'HASH'},
        {'AttributeName': 'created_at', 'KeyType': 'RANGE'}
    ],
    AttributeDefinitions=[
        {'AttributeName': 'user_id', 'AttributeType': 'N'},
        {'AttributeName': 'created_at', 'AttributeType': 'S'}
    ],
    ProvisionedThroughput={
        'ReadCapacityUnits': 5,
        'WriteCapacityUnits': 5
    }
)

# Wait for table creation
table.wait_until_exists()

# Put item
table.put_item(Item={'user_id': 1, 'name': 'John', 'email': 'john@example.com'})

# Get item
response = table.get_item(Key={'user_id': 1})
item = response.get('Item')

# Query
response = table.query(
    KeyConditionExpression='user_id = :uid',
    ExpressionAttributeValues={':uid': 1}
)

# Scan
response = table.scan(FilterExpression='age > :age', ExpressionAttributeValues={':age': 18})

# Update item
table.update_item(
    Key={'user_id': 1},
    UpdateExpression='SET email = :email',
    ExpressionAttributeValues={':email': 'new@example.com'}
)

# Delete item
table.delete_item(Key={'user_id': 1})

# Create GSI
table.update(
    AttributeDefinitions=[{'AttributeName': 'email', 'AttributeType': 'S'}],
    GlobalSecondaryIndexUpdates=[{
        'Create': {
            'IndexName': 'email-index',
            'KeySchema': [{'AttributeName': 'email', 'KeyType': 'HASH'}],
            'Projection': {'ProjectionType': 'ALL'}
        }
    }]
)
```

## ElastiCache (Redis) Operations

```python
import redis

# Connect to ElastiCache
client = redis.Redis(
    host='my-cluster.123456.cache.amazonaws.com',
    port=6379,
    decode_responses=True
)

# Set value
client.set('user:1', 'John')

# Get value
name = client.get('user:1')

# Set with expiry
client.setex('session:123', 3600, 'data')

# Hash operations
client.hset('user:1', mapping={'name': 'John', 'email': 'john@example.com'})
user = client.hgetall('user:1')

# List operations
client.rpush('queue:orders', 'order1', 'order2')
order = client.lpop('queue:orders')

# Set operations
client.sadd('online_users', 'user1', 'user2')
users = client.smembers('online_users')

# Sorted set (leaderboard)
client.zadd('leaderboard', {'user1': 100, 'user2': 90})
top_users = client.zrevrange('leaderboard', 0, 9, withscores=True)

# Pub/Sub
pubsub = client.pubsub()
pubsub.subscribe('notifications')
for message in pubsub.listen():
    print(message)

# Cluster mode
from rediscluster import RedisCluster
cluster = RedisCluster(startup_nodes=[{'host': 'node1', 'port': 7000}])
```

## DynamoDB with Boto3 (Low-level)

```python
dynamodb_client = boto3.client('dynamodb')

# Put item
dynamodb_client.put_item(
    TableName='users',
    Item={
        'user_id': {'N': '1'},
        'name': {'S': 'John'},
        'age': {'N': '30'}
    },
    ConditionExpression='attribute_not_exists(user_id)'
)

# Batch operations
dynamodb_client.batch_write_item(
    RequestItems={
        'users': [
            {'PutRequest': {'Item': {'user_id': {'N': '1'}, 'name': {'S': 'John'}}}},
            {'DeleteRequest': {'Key': {'user_id': {'N': '2'}}}}
        ]
    }
)

# Transactions
dynamodb_client.transact_write_items(
    TransactItems=[
        {'Put': {'TableName': 'users', 'Item': {'user_id': {'N': '1'}, 'name': {'S': 'John'}}}}
    ]
)
```