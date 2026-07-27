# Mock Interview: Reinforcement Learning

## Question 1: RL Fundamentals
**Q**: Explain the RL framework: agent, environment, state, action, reward, policy.

**A**: 
- **Agent**: Decision-maker (neural network)
- **Environment**: World the agent interacts with (simulator, game, real world)
- **State (s)**: Current situation/environment observation
- **Action (a)**: What the agent does
- **Reward (r)**: Feedback signal (scalar) - what agent tries to maximize
- **Policy (pi)**: Agent's strategy: maps state to action(s)

Goal: Learn policy that maximizes cumulative (discounted) reward.

```
Episode: s0 -> a0 -> r1, s1 -> a1 -> r2, s2 -> ... -> sT
Return: G_t = r_{t+1} + gamma * r_{t+2} + gamma^2 * r_{t+3} + ...
```

## Question 2: Value vs Policy Methods
**Q**: Compare value-based (DQN), policy-based (REINFORCE, PPO), and actor-critic methods.

**A**:
| Method | Learns | Pros | Cons |
|--------|--------|------|------|
| Value (DQN) | Q(s,a) function | Stable, sample efficient | Discrete actions only |
| Policy (REINFORCE) | pi(a|s) directly | Continuous actions, stochastic | High variance |
| Actor-Critic (A2C, PPO) | Both q and pi | Low variance, continuous | More complex |

**PPO (Proximal Policy Optimization)**: 
- Clips policy updates to prevent destructive large updates
- Surrogate objective: L = min(ratio * A, clip(ratio, 1-eps, 1+eps) * A)
- Stable, widely used. Default choice for many RL problems.

## Question 3: Exploration vs Exploitation
**Q**: How do RL algorithms balance exploration vs exploitation?

**A**:
- **Epsilon-greedy**: Random action with prob epsilon, decaying over time
- **UCB (Upper Confidence Bound)**: Choose actions with highest uncertainty
- **Thompson sampling**: Sample from posterior distribution
- **Entropy bonus**: Add entropy to objective to encourage exploration (SAC)
- **Noise injection**: Add noise to policy or action (parameter noise, action noise)
- **Intrinsic motivation**: Curiosity-based exploration (ICM, RND)

## Question 4: RL for LLM Alignment
**Q**: How is RL used to align language models? Explain PPO in LLM training.

**A**: RLHF (Reinforcement Learning from Human Feedback) pipeline:

1. **SFT**: Supervised fine-tuning on human demonstrations
2. **Reward model training**: Train model to predict human preference
3. **PPO fine-tuning**: Optimize LLM to maximize reward while staying close to SFT model

```python
def ppo_step(policy, ref_policy, reward_model, prompts, kl_coef=0.04):
    responses = policy.generate(prompts)
    rewards = reward_model(prompts, responses)

    # PPO objective with KL penalty
    log_probs = policy.log_prob(prompts, responses)
    ref_log_probs = ref_policy.log_prob(prompts, responses)
    kl_div = log_probs - ref_log_probs

    advantage = rewards - kl_coef * kl_div  # KL-penalized reward

    # Clipped surrogate objective
    ratio = exp(log_probs - ref_log_probs)
    clipped = clamp(ratio, 1-eps, 1+eps)
    loss = -min(ratio * advantage, clipped * advantage)
    return loss.mean()
```

**KL penalty**: Prevents the model from diverging too far from the initial SFT model, which would cause mode collapse or reward hacking.

## Question 5: Multi-Armed Bandits
**Q**: Explain the multi-armed bandit problem. How does it relate to RL and A/B testing?

**A**: K-armed bandit: K slot machines, each with unknown reward distribution. At each step, choose one to maximize cumulative reward.

**Difference from full RL**: 
- No state transitions (single state)
- No long-term planning needed
- No delayed rewards

**Algorithms**:
- **Epsilon-greedy**: Simple, epsilon exploration
- **UCB1**: Upper confidence bound, optimistic in face of uncertainty
- **Thompson sampling**: Bayesian, sample from posterior

**In A/B testing**: Traditional A/B has fixed allocation. Bandits dynamically allocate more traffic to winning variant (minimize regret).

Applications: ad selection, news recommendation, clinical trials, hyperparameter tuning.
