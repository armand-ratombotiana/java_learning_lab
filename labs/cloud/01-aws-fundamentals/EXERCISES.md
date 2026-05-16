# AWS Fundamentals - Exercises

## Exercise 1: EC2 Instance Management
1. Launch a t3.micro EC2 instance using AWS Console
2. Connect to the instance via SSH
3. Install nginx web server
4. Verify the server is running

## Exercise 2: S3 Bucket Operations
1. Create an S3 bucket with a unique name
2. Enable versioning on the bucket
3. Upload a text file to the bucket
4. Create a lifecycle policy to move objects to Glacier after 30 days

## Exercise 3: IAM User and Policy Creation
1. Create a new IAM user with programmatic access
2. Attach a policy allowing only read access to S3
3. Generate access keys for the user
4. Test the access using AWS CLI

## Exercise 4: VPC Setup
1. Create a VPC with CIDR 10.0.0.0/16
2. Create a public subnet (10.0.1.0/24)
3. Create a private subnet (10.0.2.0/24)
4. Configure routing for internet access from public subnet

## Exercise 5: Security Group Configuration
1. Create a security group for a web server
2. Allow inbound HTTP (port 80) and HTTPS (port 443)
3. Allow inbound SSH (port 22) from your IP only
4. Allow all outbound traffic

## Exercise 6: EC2 with User Data
1. Launch an EC2 instance with user data that:
   - Updates packages
   - Installs Apache
   - Creates a simple HTML page
2. Verify the web server is accessible

## Exercise 7: S3 Static Website Hosting
1. Create an S3 bucket for static website hosting
2. Upload index.html and error.html
3. Configure bucket policy for public read
4. Access the website via the S3 endpoint

## Exercise 8: IAM Role for EC2
1. Create an IAM role with S3 read permissions
2. Attach the role to an EC2 instance
3. Verify the instance can access S3 without access keys

## Exercise 9: EC2 Key Pairs
1. Create a new key pair
2. Launch an instance using the key
3. Verify you can connect using the private key

## Exercise 10: CloudWatch Basics
1. Launch an EC2 instance with CloudWatch agent
2. View basic metrics in CloudWatch console
3. Create a simple alarm for CPU utilization

## Exercise 11: EBS Volume Management
1. Create an EBS volume
2. Attach it to an EC2 instance
3. Format and mount the volume
4. Create a snapshot of the volume

## Exercise 12: S3 Cross-Region Replication
1. Create two buckets in different regions
2. Enable versioning on both buckets
3. Configure replication rules
4. Verify objects are replicated

## Exercise 13: EC2 Auto Scaling Group
1. Create a launch template
2. Create an Auto Scaling group
3. Configure scaling policies
4. Test the scaling behavior

## Exercise 14: VPC Peering
1. Create two VPCs in the same region
2. Set up VPC peering connection
3. Configure routing for communication
4. Test connectivity between instances

## Exercise 15: IAM Policy Analysis
1. Analyze an IAM policy document
2. Identify allowed and denied actions
3. Create a more restrictive version

## Exercise 16: EC2 Instance Metadata
1. Retrieve instance metadata using curl
2. Access IAM security credentials
3. Understand the metadata endpoints

## Exercise 17: S3 Pre-signed URLs
1. Generate a pre-signed URL for an S3 object
2. Set an expiration time
3. Test accessing the object via the URL

## Exercise 18: VPC Flow Logs
1. Enable VPC Flow Logs
2. Send logs to CloudWatch
3. Analyze the flow log data

## Exercise 19: EC2 Instance Types Comparison
1. Compare different instance types
2. Understand vCPU, memory, network performance
3. Choose appropriate instance for a workload

## Exercise 20: AWS CLI Scripting
1. Write a bash script to:
   - List all EC2 instances
   - Start stopped instances
   - Stop running instances

## Exercise 21: Cost Optimization
1. Review AWS Cost Explorer
2. Identify underutilized EC2 instances
3. Create a plan to optimize costs

## Exercise 22: S3 Encryption
1. Upload an object with server-side encryption
2. Use KMS keys for encryption
3. Verify encryption is applied

## Exercise 23: IAM MFA Setup
1. Enable MFA for an IAM user
2. Configure virtual MFA device
3. Test login with MFA

## Exercise 24: EC2 Placement Groups
1. Create a cluster placement group
2. Launch instances in the group
3. Test network performance

## Exercise 25: S3 Bucket Policies
1. Write a bucket policy for:
   - Deny unencrypted uploads
   - Require specific IP access