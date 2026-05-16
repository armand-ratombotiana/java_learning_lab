# AWS Fundamentals - Code Deep Dive

## AWS CLI and SDK Examples

### 1. EC2 Operations

```python
import boto3

# Create EC2 client
ec2 = boto3.client('ec2', region_name='us-east-1')

# Launch an instance
response = ec2.run_instances(
    ImageId='ami-0c55b159cbfafe1f0',
    InstanceType='t3.micro',
    KeyName='my-key-pair',
    MaxCount=1,
    MinCount=1,
    SecurityGroupIds=['sg-12345678'],
    SubnetId='subnet-12345678'
)

# Get instance ID
instance_id = response['Instances'][0]['InstanceId']
print(f"Instance launched: {instance_id}")

# Describe instances
response = ec2.describe_instances(Filters=[{'Name': 'instance-state-name', 'Values': ['running']}])
for reservation in response['Reservations']:
    for instance in reservation['Instances']:
        print(f"Instance ID: {instance['InstanceId']}, Type: {instance['InstanceType']}")

# Stop and terminate instances
ec2.stop_instances(InstanceIds=[instance_id])
ec2.terminate_instances(InstanceIds=[instance_id])
```

### 2. S3 Operations

```python
import boto3

# Create S3 client
s3 = boto3.client('s3')

# Upload a file
s3.upload_file('local-file.txt', 'my-bucket', 'remote-file.txt')

# Download a file
s3.download_file('my-bucket', 'remote-file.txt', 'local-file.txt')

# List buckets
response = s3.list_buckets()
for bucket in response['Buckets']:
    print(f"Bucket: {bucket['Name']}")

# Create bucket with versioning
s3.create_bucket(Bucket='my-new-bucket')
s3.put_bucket_versioning(
    Bucket='my-new-bucket',
    VersioningConfiguration={'Status': 'Enabled'}
)

# Set lifecycle policy
s3.put_bucket_lifecycle_configuration(
    Bucket='my-bucket',
    LifecycleConfiguration={
        'Rules': [{
            'ID': 'Move to Glacier after 90 days',
            'Status': 'Enabled',
            'Transitions': [{'Days': 90, 'StorageClass': 'GLACIER'}],
            'Expiration': {'Days': 365}
        }]
    }
)
```

### 3. IAM Operations

```python
import boto3

# Create IAM client
iam = boto3.client('iam')

# Create user
response = iam.create_user(UserName='new-user')
print(f"User created: {response['User']['UserName']}")

# Create access key
response = iam.create_access_key(UserName='new-user')
print(f"Access Key ID: {response['AccessKey']['AccessKeyId']}")

# Attach policy to user
iam.attach_user_policy(
    UserName='new-user',
    PolicyArn='arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess'
)

# Create role
response = iam.create_role(
    RoleName='my-ec2-role',
    AssumeRolePolicyDocument=json.dumps({
        'Version': '2012-10-17',
        'Statement': [{
            'Effect': 'Allow',
            'Principal': {'Service': 'ec2.amazonaws.com'},
            'Action': 'sts:AssumeRole'
        }]
    })
)

# List users
response = iam.list_users()
for user in response['Users']:
    print(f"User: {user['UserName']}, Created: {user['CreateDate']}")
```

### 4. VPC Operations

```python
import boto3

# Create EC2 client for VPC operations
ec2 = boto3.client('ec2', region_name='us-east-1')

# Create VPC
response = ec2.create_vpc(CidrBlock='10.0.0.0/16')
vpc_id = response['Vpc']['VpcId']
print(f"VPC created: {vpc_id}")

# Create subnets
public_subnet = ec2.create_subnet(VpcId=vpc_id, CidrBlock='10.0.1.0/24', AvailabilityZone='us-east-1a')
private_subnet = ec2.create_subnet(VpcId=vpc_id, CidrBlock='10.0.2.0/24', AvailabilityZone='us-east-1b')

# Create Internet Gateway
igw = ec2.create_internet_gateway()
igw_id = igw['InternetGateway']['InternetGatewayId']
ec2.attach_internet_gateway(InternetGatewayId=igw_id, VpcId=vpc_id)

# Create route table and routes
route_table = ec2.create_route_table(VpcId=vpc_id)
ec2.create_route(RouteTableId=route_table['RouteTable']['RouteTableId'], DestinationCidrBlock='0.0.0.0/0', GatewayId=igw_id)

# Associate route table with public subnet
ec2.associate_route_table(RouteTableId=route_table['RouteTable']['RouteTableId'], SubnetId=public_subnet['Subnet']['SubnetId'])

# Create security group
security_group = ec2.create_security_group(
    GroupName='my-security-group',
    Description='My security group',
    VpcId=vpc_id
)

# Add rules
ec2.authorize_security_group_ingress(
    GroupId=security_group['GroupId'],
    IpProtocol='tcp',
    FromPort=80,
    ToPort=80,
    CidrIp='0.0.0.0/0'
)
```

### Terraform Equivalent

```hcl
# main.tf
provider "aws" {
  region = "us-east-1"
}

# EC2 Instance
resource "aws_instance" "web" {
  ami           = "ami-0c55b159cbfafe1f0"
  instance_type = "t3.micro"
  subnet_id     = aws_subnet.public.id
  tags = {
    Name = "web-server"
  }
}

# S3 Bucket
resource "aws_s3_bucket" "data" {
  bucket = "my-data-bucket"
}

# VPC
resource "aws_vpc" "main" {
  cidr_block = "10.0.0.0/16"
}

resource "aws_subnet" "public" {
  vpc_id     = aws_vpc.main.id
  cidr_block = "10.0.1.0/24"
}

# IAM Role
resource "aws_iam_role" "ec2_role" {
  name = "ec2_role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Principal = {
        Service = "ec2.amazonaws.com"
      }
    }]
  })
}
```