# Step-by-Step — Multi-Tier VPC with Public and Private Subnets

## Step 1: Create VPC
```powershell
aws ec2 create-vpc --cidr-block 10.0.0.0/16 --instance-tenancy default
# Save VpcId: vpc-xxx

# Enable DNS hostnames
aws ec2 modify-vpc-attribute --vpc-id vpc-xxx --enable-dns-hostnames
```

## Step 2: Create Subnets
```powershell
# Public subnet (AZ-a)
aws ec2 create-subnet --vpc-id vpc-xxx --cidr-block 10.0.1.0/24 `
    --availability-zone us-east-1a
# Save SubnetId: subnet-public-a

# Private subnets
aws ec2 create-subnet --vpc-id vpc-xxx --cidr-block 10.0.2.0/24 `
    --availability-zone us-east-1a
# Save: subnet-private-app-a

aws ec2 create-subnet --vpc-id vpc-xxx --cidr-block 10.0.3.0/24 `
    --availability-zone us-east-1b
# Save: subnet-private-app-b

aws ec2 create-subnet --vpc-id vpc-xxx --cidr-block 10.0.4.0/24 `
    --availability-zone us-east-1a
# Save: subnet-private-db-a

aws ec2 create-subnet --vpc-id vpc-xxx --cidr-block 10.0.5.0/24 `
    --availability-zone us-east-1b
# Save: subnet-private-db-b
```

## Step 3: Create Internet Gateway
```powershell
aws ec2 create-internet-gateway
# Save InternetGatewayId: igw-xxx

aws ec2 attach-internet-gateway --vpc-id vpc-xxx --internet-gateway-id igw-xxx
```

## Step 4: Create NAT Gateway
```powershell
# Create Elastic IP for NAT
aws ec2 allocate-address --domain vpc
# Save AllocationId: eipalloc-xxx

# Create NAT in public subnet
aws ec2 create-nat-gateway --subnet-id subnet-public-a `
    --allocation-id eipalloc-xxx
# Save NatGatewayId: nat-xxx
```

## Step 5: Configure Route Tables
```powershell
# Public route table (internet access)
aws ec2 create-route-table --vpc-id vpc-xxx
# Save: rtb-public

aws ec2 create-route --route-table-id rtb-public `
    --destination-cidr-block 0.0.0.0/0 --gateway-id igw-xxx

aws ec2 associate-route-table --route-table-id rtb-public `
    --subnet-id subnet-public-a

# Private route table (NAT for internet)
aws ec2 create-route-table --vpc-id vpc-xxx
# Save: rtb-private

aws ec2 create-route --route-table-id rtb-private `
    --destination-cidr-block 0.0.0.0/0 --nat-gateway-id nat-xxx

aws ec2 associate-route-table --route-table-id rtb-private `
    --subnet-id subnet-private-app-a

aws ec2 associate-route-table --route-table-id rtb-private `
    --subnet-id subnet-private-app-b

# DB route table (no internet)
aws ec2 create-route-table --vpc-id vpc-xxx
# Save: rtb-db

aws ec2 associate-route-table --route-table-id rtb-db `
    --subnet-id subnet-private-db-a

aws ec2 associate-route-table --route-table-id rtb-db `
    --subnet-id subnet-private-db-b
```

## Step 6: Create Security Groups
```powershell
# ALB SG: allow HTTP/HTTPS from internet
aws ec2 create-security-group --group-name alb-sg `
    --description "ALB Security Group" --vpc-id vpc-xxx
aws ec2 authorize-security-group-ingress --group-name alb-sg `
    --protocol tcp --port 80 --cidr 0.0.0.0/0

# App SG: allow traffic from ALB only
aws ec2 create-security-group --group-name app-sg `
    --description "App Security Group" --vpc-id vpc-xxx
aws ec2 authorize-security-group-ingress --group-name app-sg `
    --protocol tcp --port 8080 --source-group alb-sg

# DB SG: allow MySQL from app only
aws ec2 create-security-group --group-name db-sg `
    --description "DB Security Group" --vpc-id vpc-xxx
aws ec2 authorize-security-group-ingress --group-name db-sg `
    --protocol tcp --port 3306 --source-group app-sg
```

## Step 7: Test Connectivity
```powershell
# Launch test instance in public subnet
aws ec2 run-instances --image-id ami-xxx --instance-type t3.micro `
    --key-name my-key --security-group-ids alb-sg `
    --subnet-id subnet-public-a

# Launch test instance in private subnet
aws ec2 run-instances --image-id ami-xxx --instance-type t3.micro `
    --key-name my-key --security-group-ids app-sg `
    --subnet-id subnet-private-app-a

# SSH to public instance → ping private instance internal IP
```

## Step 8: Clean Up
```powershell
aws ec2 terminate-instances --instance-ids i-xxx i-yyy
aws ec2 delete-nat-gateway --nat-gateway-id nat-xxx
aws ec2 release-address --allocation-id eipalloc-xxx
aws ec2 delete-internet-gateway --internet-gateway-id igw-xxx
aws ec2 delete-vpc --vpc-id vpc-xxx
```
