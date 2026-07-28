# Lab 07: Interview Questions

## Q1: What are the three stages of RLHF?
**A:** 1) Supervised fine-tuning (SFT) on high-quality demonstrations, 2) Reward model training on preference data, 3) PPO optimization using the reward model.

## Q2: Why does PPO use a clipped surrogate objective?
**A:** Clipping prevents the policy from changing too rapidly, which causes training instability. The clip limits the probability ratio to [1-ε, 1+ε].

## Q3: What is the purpose of the KL divergence penalty in RLHF?
**A:** KL penalty prevents the policy from deviating too far from the SFT model, maintaining fluency and preventing reward hacking.

## Q4: How does DPO simplify RLHF?
**A:** DPO directly optimizes the policy on preference data without a separate reward model. It derives a closed-form mapping between reward and policy, making training simpler and more stable.

## Q5: What is reward hacking and how do you detect it?
**A:** The policy exploits spurious patterns in the reward model to get high scores without actually improving quality. Detected by monitoring reward score vs human evaluation correlation.
