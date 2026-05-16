# Terraform IaC - QUIZ

## Section 1: Basics

**Q1: What is Infrastructure as Code?**
- A) Programming infrastructure
- B) Managing infrastructure through code/configuration files
- C) Writing code for infrastructure
- D) Cloud-based coding

**Q2: What does `terraform init` do?**
- A) Creates resources
- B) Initializes working directory and downloads providers
- C) Validates configuration
- D) Shows plan

**Q3: What is the purpose of `terraform plan`?**
- A) Apply changes
- B) Preview changes before applying
- C) Initialize backend
- D) Import resources

**Q4: What is Terraform state used for?**
- A) Documentation
- B) Tracking real-world infrastructure
- C) Testing
- D) Backup

**Q5: What command applies changes?**
- A) terraform apply
- B) terraform create
- C) terraform execute
- D) terraform run

## Section 2: Configuration

**Q6: What is a resource in Terraform?**
- A) Cloud resource only
- A) Managed infrastructure component
- C) State file
- D) Provider

**Q7: What is a variable used for?**
- A) Storing state
- B) Parameterizing configuration
- C) Managing providers
- D) Creating outputs

**Q8: What is the difference between variables and locals?**
- A) No difference
- B) Locals are computed, variables are input
- C) Variables are computed, locals are input
- D) Locals are deprecated

**Q9: How do you reference another resource's attribute?**
- A) resource.resource_type.resource_name.attribute
- B) aws_resource.attribute
- C) ${aws_resource.attribute}
- D) All of the above

**Q10: What does a data source provide?**
- A) Local data
- B) Read-only information from existing infrastructure
- C) New resources
- D) State information

## Section 3: State Management

**Q11: What backend type is recommended for teams?**
- A) Local
- B) S3
- C) Consul
- D) All

**Q12: What is state locking?**
- A) Preventing state modification
- B) Preventing concurrent operations on state
- C) Encrypting state
- D) Backing up state

**Q13: Why should sensitive data not be in state?**
- A) Performance
- B) Security - state may be exposed
- C) Size
- D) Speed

**Q14: What does `terraform import` do?**
- A) Imports cloud resources into Terraform
- B) Imports Terraform into cloud
- C) Imports state
- D) Imports modules

**Q15: How to safely manage secrets in Terraform?**
- A) Store in state file
- B) Use environment variables or secrets managers
- C) Store in plain text
- D) Commit to git

## Section 4: Modules

**Q16: What is a module?**
- A) Terraform provider
- B) Reusable collection of resources
- C) State file
- D) Backend type

**Q17: What is the difference between module source types?**
- A) No difference
- B) Local uses relative path, remote uses URL
- C) Remote is faster
- D) Local is deprecated

**Q18: When should you use modules?**
- A) Never
- B) For reusable infrastructure patterns
- C) Only for providers
- D) Only in production

**Q19: What are output values used for?**
- A) Debugging
- B) Sharing module results with other configs
- C) Storing state
- D) Testing

**Q20: How do you version modules?**
- A) Not possible
- B) Use version constraints in source
- C) Only use latest
- D) Use branches

## Section 5: Workspaces

**Q21: What are workspaces used for?**
- A) Coding
- B) Managing multiple environments
- C) Testing
- D) Documentation

**Q22: What is the default workspace?**
- A) default
- B) main
- C) primary
- D) production

**Q23: How do you switch workspaces?**
- A) terraform switch
- B) terraform workspace select
- C) terraform use
- D) terraform go

**Q24: Can workspaces share state?**
- A) Yes, always
- B) No, they have separate state
- C) Only with special configuration
- D) Not recommended

**Q25: When use workspaces vs directories?**
- A) Workspaces for all cases
- B) Directories for completely different infra, workspaces for similar envs
- C) Directories for all cases
- D) Neither

## Section 6: Best Practices

**Q26: What is drift?**
- A) Infrastructure changing outside Terraform
- B) State corruption
- C) Provider issues
- D) Backend problems

**Q27: How to detect drift?**
- A) terraform plan
- B) terraform refresh
- C) terraform show
- D) All of the above

**Q28: What is the recommended workflow?**
- A) Apply directly
- B) Plan first, then apply
- C) No planning
- D) Manual only

**Q29: What should be in .gitignore?**
- A) .tf files
- B) .tfstate and .tfvars with secrets
- C) Variables
- D) Nothing

**Q30: How to structure Terraform code?**
- A) All in one file
- B) Separate files for different concerns
- C) No structure
- D) Random organization

---

## Answers

1. B
2. B
3. B
4. B
5. A
6. B
7. B
8. B
9. A
10. B
11. B
12. B
13. B
14. A
15. B
16. B
17. B
18. B
19. B
20. B
21. B
22. A
23. B
24. B
25. B
26. A
27. A
28. B
29. B
30. B