# Reflection — AWS Fundamentals

## Self-Assessment

| Skill | I Know This | I Can Explain It | I Can Teach It |
|-------|:-----------:|:-----------------:|:--------------:|
| EC2 launch and configuration | ☐ | ☐ | ☐ |
| S3 bucket creation and policies | ☐ | ☐ | ☐ |
| IAM user/group/role management | ☐ | ☐ | ☐ |
| VPC design with subnets, route tables | ☐ | ☐ | ☐ |
| Security group vs NACL | ☐ | ☐ | ☐ |
| AWS global infrastructure | ☐ | ☐ | ☐ |
| Shared responsibility model | ☐ | ☐ | ☐ |
| EC2 pricing models | ☐ | ☐ | ☐ |
| S3 durability and consistency | ☐ | ☐ | ☐ |
| IAM policy evaluation | ☐ | ☐ | ☐ |

## Journal Prompts

1. How is AWS different from traditional on-premise hosting for a Java application?

2. What surprised you most about AWS's architecture (regions, AZs, Nitro)?

3. Which AWS fundamental service do you think you'll use most in your projects?

4. How would you explain the shared responsibility model to a non-technical stakeholder?

5. What was the most difficult concept in this module? What made it hard?

6. How does understanding AWS change how you think about deploying a Java application?

7. If you were building a startup, how would AWS fundamentals affect your architecture decisions?

## Next Steps

1. **Weak areas**: Revisit THEORY.md and COMMON_MISTAKES.md
2. **Practice**: Complete QUIZ.md and EXERCISES.md
3. **Apply**: Build MINI_PROJECT.md (deploy Java app on EC2)
4. **Extend**: Try REAL_WORLD_PROJECT.md for full production setup
5. **Prepare**: Use INTERVIEW.md for certification/SWE interview prep

## Key Takeaways
- AWS provides utility computing — pay only for what you use
- Four pillars: EC2 (compute), S3 (storage), IAM (security), VPC (network)
- Design for failure: use multiple AZs, Auto Scaling, health checks
- Security is layered: IAM policies + SGs + NACLs + encryption
- Everything is API-driven — Java SDK wraps all AWS services
