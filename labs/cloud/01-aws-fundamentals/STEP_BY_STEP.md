# Step-by-Step Guide — Launch a Java App on AWS

## Step 1: Create an AWS Account
1. Go to https://aws.amazon.com and click "Create an AWS Account"
2. Provide email, password, root user name
3. Add payment method and phone verification
4. Select support plan (Basic — free)
5. Sign in to AWS Management Console

## Step 2: Install and Configure AWS CLI
```powershell
# Download and install AWS CLI v2
# Verify installation
aws --version

# Configure credentials
aws configure
# AWS Access Key ID: AKIA...
# AWS Secret Access Key: wJalrX...
# Default region: us-east-1
# Default output format: json
```

## Step 3: Create IAM Admin User
```powershell
aws iam create-user --user-name admin-java
aws iam attach-user-policy --user-name admin-java `
    --policy-arn arn:aws:iam::aws:policy/AdministratorAccess
aws iam create-access-key --user-name admin-java
# Save AccessKeyId and SecretAccessKey
```

## Step 4: Launch EC2 Instance
```powershell
# Create key pair
aws ec2 create-key-pair --key-name java-lab-key --query KeyMaterial `
    --output text > java-lab-key.pem

# Create security group
aws ec2 create-security-group --group-name java-lab-sg `
    --description "Java lab security group"

# Authorize SSH and HTTP
aws ec2 authorize-security-group-ingress --group-name java-lab-sg `
    --protocol tcp --port 22 --cidr 0.0.0.0/0
aws ec2 authorize-security-group-ingress --group-name java-lab-sg `
    --protocol tcp --port 8080 --cidr 0.0.0.0/0

# Launch instance
aws ec2 run-instances --image-id ami-0c55b159cbfafe1f0 `
    --instance-type t3.micro --key-name java-lab-key `
    --security-groups java-lab-sg --user-data file://userdata.sh
```

## Step 5: Deploy a Java Application
```bash
# SSH into EC2
ssh -i java-lab-key.pem ec2-user@<public-ip>

# Install Java 11
sudo yum install -y java-11-amazon-corretto-devel

# Create a simple Spring Boot app
cat > HelloController.java << 'EOF'
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class HelloController {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", exchange -> {
            String response = "Hello from AWS EC2!";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        });
        server.setExecutor(null);
        server.start();
        System.out.println("Server running on port 8080");
    }
}
EOF

# Compile and run
javac HelloController.java
java HelloController &

# Visit http://<public-ip>:8080
```

## Step 6: Upload File to S3
```powershell
# Create bucket
aws s3 mb s3://java-lab-uploads-$(Get-Random)

# Upload file
aws s3 cp HelloController.java s3://java-lab-uploads-xxxxx/

# List objects
aws s3 ls s3://java-lab-uploads-xxxxx/
```

## Step 7: Clean Up
```powershell
# Terminate EC2
aws ec2 terminate-instances --instance-ids i-xxxxxxxx

# Empty and delete S3 bucket
aws s3 rb s3://java-lab-uploads-xxxxx --force

# Delete key pair
aws ec2 delete-key-pair --key-name java-lab-key
```
