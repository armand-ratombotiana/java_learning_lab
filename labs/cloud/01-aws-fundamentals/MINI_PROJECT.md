# AWS Fundamentals - Mini Project

## Project: Static Website Hosting with AWS

### Objective
Deploy a static website using S3 with proper security configurations.

### Requirements
1. Create an S3 bucket for website hosting
2. Design and create HTML/CSS content
3. Configure bucket for public read access
4. Set up custom error handling
5. Enable proper caching headers

### Steps

**Step 1: Create S3 Bucket**
- Use AWS Console or CLI
- Bucket name: `my-static-website-YOURNAME`
- Disable "Block all public access" (required for website hosting)

**Step 2: Configure Static Website Hosting**
- Enable static website hosting
- Set index.html as index document
- Set error.html as error document

**Step 3: Create Website Content**
```html
<!-- index.html -->
<!DOCTYPE html>
<html>
<head>
    <title>My Static Website</title>
    <style>
        body { font-family: Arial; padding: 50px; text-align: center; }
        h1 { color: #232f3e; }
    </style>
</head>
<body>
    <h1>Welcome to My AWS Powered Website!</h1>
    <p>This site is hosted on Amazon S3</p>
</body>
</html>
```

**Step 4: Add Bucket Policy for Public Access**
```json
{
    "Version": "2012-10-17",
    "Statement": [{
        "Sid": "PublicReadGetObject",
        "Effect": "Allow",
        "Principal": "*",
        "Action": "s3:GetObject",
        "Resource": "arn:aws:s3:::my-static-website-YOURNAME/*"
    }]
}
```

**Step 5: Verify Website**
- Access website via S3 website endpoint
- Test error page handling

### Deliverables
1. Working static website accessible via internet
2. Screenshot of website
3. Bucket policy document
4. CloudFront distribution (optional enhancement)

### Estimated Time
45-60 minutes