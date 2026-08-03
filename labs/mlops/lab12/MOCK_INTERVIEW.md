# Lab 12: Mock Interview — Infrastructure as Code for ML

**Role**: ML Platform Engineer / DevOps Engineer
**Duration**: 60 minutes
**Focus**: Terraform HCL generation for ML infrastructure (S3, ECS/Fargate, IAM, VPC), state management, environment parity, Pulumi vs Terraform

---

**Interviewer**: "What is this lab actually generating, and why generate it from Java instead of writing the HCL by hand?"

**Candidate**: "The `TerraformConfigGenerator` builds the serving platform as HCL: a VPC module, a security group exposing port 8080, an IAM execution role for ECS, three S3 buckets — models, data, experiments — each versioned with a 90-day lifecycle, and a Fargate service running the model server with awslogs. Generating it from Java buys two things: *parameterization* — name, environment, cpu/memory flow in as arguments, so dev and prod come from one code path; and *invariants* — every bucket gets versioning, every resource gets a `ManagedBy = "terraform"` tag, because the generator enforces the rules once instead of relying on developer memory across five files."

**Interviewer**: "Why S3 versioning with noncurrent-version expiration for model artifacts?"

**Candidate**: "Model artifacts are governance objects — Lab 11's audit trail records which version served when, and versioning is what makes that verifiable: when a promotion overwrites an object, the previous version survives, so rollback is a restore, not a mystery. The lifecycle rule `noncurrent_version_expiration { noncurrent_days = 90 }` bounds the cost — old versions are kept long enough to roll back and audit, then garbage-collected automatically. And the generator enforces the policy: you cannot create a model bucket without versioning, because `generateS3Bucket` always emits the versioning block. That's IaC as enforcement, the same idea as the validation gate in Lab 09."

**Interviewer**: "Walk through the IAM design. Is `Resource = [\"*\"]` a problem?"

**Candidate**: "The trust policy is correct: `sts:AssumeRole` for `ecs-tasks.amazonaws.com` — only ECS can assume it. The attached policy grants S3 Get/Put and CloudWatch logs — the minimum the task needs. The blemish is `Resource = [\"*\"]`: Get/Put on *any* bucket rather than just the `mlops-*` ones. In a review I'd tighten it to the three generated bucket ARNs plus the log group. It's a deliberate simplification — and the generator is the right place to fix it, because one change propagates to every environment."

**Interviewer**: "The provider block configures a remote backend. Why does that matter for a team?"

**Candidate**: "State is Terraform's record of what it created. Local state shared across engineers is a merge-bomb — two people planning against stale state will fight over resources. The lab pins state to S3 with a DynamoDB lock table: the lock serializes plan/apply so runs can't stomp each other. For ML specifically, state also captures non-code resources — ECR images, model buckets — so `terraform plan` doubles as drift detection: someone deletes a bucket manually, the next plan shows the difference. Plan-as-drift-detector is the operational core of IaC."

**Interviewer**: "Why Fargate for the model server instead of EC2 instances?"

**Candidate**: "Fargate removes the infrastructure surface: no AMI management, no instance patching, no capacity pools — the task definition is the unit of deployment and `desired_count` handles scaling. The lab sets 3 replicas in prod vs 1 in dev from the environment parameter — parity of *definition*, not of scale: dev and prod run the same image, differing only in declared parameters. For latency-critical or GPU serving you'd want ECS EC2 or Kubernetes; for a containerized HTTP model server like Lab 05's at moderate load, Fargate's simplicity wins."

**Interviewer**: "The generated service references `aws_security_group.fraud-detector_sg.id`, but the generator only emits `model-server_sg`. What happens?"

**Candidate**: "The reference points at a resource that was never declared — `terraform plan` fails with a 'Reference to undeclared resource' error before creating anything. It's a real bug in the lab: `generateEcsFargateService` hardcodes the SG resource name in the service block while the SG generator is only invoked for `model-server`. The fix: the service generator should accept the security-group resource name as a parameter, so the reference and the resource come from the same definition. The lesson: config generation has the same correctness requirements as code — a dangling reference is a compile error in any other language, and `terraform validate` in CI is the lint step that catches it."

**Interviewer**: "The image line is `ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/mlops-model:latest`. What's wrong with it?"

**Candidate**: "Two things. `ACCOUNT_ID` is a literal placeholder — the config can't apply until it's substituted, which is why real pipelines use a variable or a `data.aws_caller_identity` source. And `:latest` is an anti-pattern for models: model artifacts are immutable, versioned like Lab 03's registry, so the ECR tag should come from the same promotion event the registry records — `mlops-model:2.1.0`. Otherwise rollback means re-tagging production. The right pattern: the pipeline that promotes the model also runs the config with that tag — one source of truth for what is deployed."

**Interviewer**: "Terraform vs Pulumi — the lab's Q. When would you choose each?"

**Candidate**: "Terraform is HCL: declarative, tool-agnostic, the industry default — huge module ecosystem (the VPC module is from `terraform-aws-modules`). Pulumi is general-purpose code — the Java SDK gives type checking, loops, and your existing test infrastructure; this lab's generators are trivially expressible in Pulumi because it's already doing code-generation. My rule: DevOps-experienced teams with a plain AWS/GCP catalog — Terraform; software-engineer teams building a platform and wanting the app's language and tests — Pulumi. Either way the discipline is identical: config is code, reviewed like code."

**Interviewer**: "How does IaC tie into the rest of the MLOps stack from the other labs?"

**Candidate**: "Every artifact becomes a Terraform concern: Lab 03's registry is backed by the versioned model bucket; Lab 05's server is the Fargate task; Lab 07's pipeline runs `terraform apply` as its deploy stage; Lab 08's monitoring scrapes the ECS endpoints; Lab 11's governance wants the environment tags this generator stamps everywhere. The unifying principle: the platform is defined once, in versioned config, and every environment — dev, staging, prod — is a parameterized instantiation of that definition. Infrastructure drift between environments becomes a code-review diff, not a discovery after an incident."

**Interviewer**: "What does `terraform destroy` mean for an ML platform, and when is it actually used?"

**Candidate**: "`destroy` tears down exactly what state knows, in dependency order — a correct delete is as much a property of IaC as a correct create, which is why the lab prints it as part of the workflow. In practice: ephemeral dev environments get destroyed nightly to control cost; staging is recreated per release; production destroy requires explicit approval and is rare. The critical habit is *not* to destroy anything whose state you didn't create — the `ManagedBy = "terraform"` tag is the inventory answer: 'if the tag is missing, Terraform will not touch it.' That tag is cheap to emit and priceless during audits."

**Interviewer**: "The lab's `generateEcsFargateService` takes cpu and memory as ints. What does the generated config show about how Terraform consumes them?"

**Candidate**: "They're rendered as strings — `cpu = \"512\"`, `memory = \"1024\"` — because the AWS provider declares those arguments as strings (Fargate task sizes like 512/1024 and 1024/2048 are the valid pairings). The formatting subtlety in the generator: `%d` formats the int, then the surrounding quotes make it a valid HCL string. It's a small example of the boundary between code and config: the generator owns types and validation (rejecting an invalid pair), the generated file owns the provider's grammar. That separation is why generated IaC is more reliable than hand-edited files — the type errors are caught in Java, not at plan time."

**Interviewer**: "How would you test this generator — what assertions would you CI-gate?"

**Candidate**: "Three tiers. Structure: for each generated bucket, the output contains the versioning block with `status = \"Enabled\"` and the lifecycle rule — no bucket without versioning. Identity: `mlops-fraud-detector-dev-*` naming appears in cluster, task family, and service — the environment parameter threads through everything. Correctness: every `aws_*` resource referenced by another block is declared somewhere in the full config — the check that catches the `fraud-detector_sg` bug — plus a golden-file test: the generated dev config must equal the captured transcript byte-for-byte, so any change to the generator is a reviewed diff to the golden file. Those are exactly the three assertions I'd run before any `terraform plan` leaves the pipeline."
