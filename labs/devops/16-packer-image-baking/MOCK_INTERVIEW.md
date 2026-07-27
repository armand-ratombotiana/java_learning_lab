# Packer & Image Baking MOCK_INTERVIEW.md

## Scenario 1: AMI Building with Packer
You need to create a custom Amazon Machine Image (AMI) with your application pre-installed.

**Questions**:
1. How do you define a Packer template?
2. What's the difference between a builder and a provisioner?
3. How do you use Packer with Ansible as a provisioner?
4. How do you version Packer images?

**Expected approach**: Packer HCL2 template with `source` (builder: amazon-ebs) and `build` block (provisioners: ansible, shell, file). Builder creates the infrastructure, provisioner configures it. Version via tags (AMI tags) and image name (e.g., `myapp-{{timestamp}}`). Use `post-processor` for manifest.

## Scenario 2: Immutable Infrastructure
Your team currently patches servers in place (SSH + Ansible). You want to move to immutable images.

**Questions**:
1. What's the benefit of immutable infrastructure?
2. How do you build images with Packer?
3. How do you deploy new images?
4. How do you handle configuration that changes frequently?

**Expected approach**: Immutable: never patch, always rebuild. CI/CD builds new image, Terraform deploys with new AMI. Image: base OS + app + dependencies baked in. Frequently changing config via external sources (Consul KV, Vault, environment variables via user data/user_data).

## Scenario 3: Multi-Cloud Image Building
Your application runs on AWS, GCP, and Azure. Each needs a custom image.

**Questions**:
1. How do you build images for multiple clouds with Packer?
2. How do you share provisioner code across clouds?
3. How do you handle cloud-specific configurations?
4. How do you test images?

**Expected approach**: Multi-builder: `amazon-ebs`, `googlecompute`, `azure-arm` in same template. Shared provisioners (ansible) with cloud-specific variables. Cloud-specific: different source AMI per region/platform. Testing: `packer validate`, bake and launch instance, run smoke tests.

## Scenario 4: Image Pipeline CI/CD
You need a CI/CD pipeline for building, testing, and deploying Packer images.

**Questions**:
1. Design the image pipeline.
2. When do you trigger image rebuilds?
3. How do you test images before deployment?
4. How do you roll back a bad image?

**Expected approach**: Trigger: base OS updates, app version changes, scheduled (weekly). Pipeline: validate → build → test (smoke, security scan) → publish → deploy (Terraform update AMI ID). Testing: launch instance from AMI, run tests, terminate. Rollback: Terraform points to previous AMI.

## Scenario 5: Security Hardening with Packer
Your images need to pass CIS benchmarks.

**Questions**:
1. How do you harden images with Packer?
2. How do you integrate security scanning?
3. How do you manage secrets during image build?
4. How do you handle compliance validation?

**Expected approach**: Hardening provisioners: `ansible` with CIS playbooks, `shell` scripts for hardening. Scanning: Trivy, Anchore, or Snyk in the pipeline. Secrets: Packer environment variables, Vault integration, avoid baking secrets into images. Compliance: InSpec, OpenSCAP as provisioner or test.

## Key Packer Interview Questions
1. What's the difference between Packer and Terraform?
2. How does Packer work with different builders?
3. Explain Packer's artifact management.
4. What's a Packer post-processor?
5. How do you handle Packer caching?
6. Explain Packer's HCL2 vs JSON syntax.
7. How do you debug Packer builds?
8. What's the Packer manifest post-processor?
9. How do you handle Packer build parallelism?
10. What's the difference between golden images and ephemeral containers?

## Whiteboard Challenge
Design an image baking pipeline for an organization with 10+ microservices running across AWS, GCP, and Azure. Include CI/CD, security scanning, compliance, and rollback.

## Follow-up
1. How would you handle image version rollback?
2. How would you implement blue-green image deployment?
3. How would you handle container images vs VM images?