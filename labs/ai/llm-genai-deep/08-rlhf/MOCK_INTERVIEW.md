# Mock Interview: Implement RLHF Training Loop from Scratch

## Scenario
You are interviewing for a research engineer role at an AI safety lab. They want you to demonstrate deep understanding of the RLHF pipeline.

## Interviewer Opening Question
"Implement the RLHF training loop from scratch — including reward model training and PPO optimization."

## Candidate Response
"RLHF has three stages: supervised fine-tuning (SFT), reward model (RM) training on human preferences, and PPO fine-tuning of the policy using the reward model as a signal. I'll implement the RM training and the PPO loop."

## Interviewer Probing Questions

**Q: How do you handle reward normalization?**
"Normalize rewards to have mean 0, std 1 within each batch. This stabilizes PPO training. I also clip rewards to [-5, 5] to prevent extreme values."

**Q: What's the role of the KL penalty?**
"KL divergence between the current policy and the SFT model prevents the policy from diverging too far. It acts as a regularizer. The total reward = r_theta - beta * KL(policy || ref_policy)."

**Q: Why PPO over other RL algorithms?**
"PPO uses a clipped surrogate objective that prevents destructive policy updates. It's sample-efficient and stable enough for language model fine-tuning."

## Candidate Solution (Python)

```python
import torch
import torch.nn as nn
import torch.nn.functional as F
import numpy as np
from transformers import AutoModelForCausalLM, AutoTokenizer
from dataclasses import dataclass
from typing import List

@dataclass
class PreferenceExample:
    prompt: str
    chosen: str
    rejected: str

class RewardModel(nn.Module):
    def __init__(self, base_model: str):
        super().__init__()
        self.backbone = AutoModelForCausalLM.from_pretrained(base_model)
        hidden_size = self.backbone.config.hidden_size
        self.reward_head = nn.Linear(hidden_size, 1)

    def forward(self, input_ids, attention_mask):
        outputs = self.backbone(input_ids=input_ids, attention_mask=attention_mask,
                                output_hidden_states=True)
        # Use last token's hidden state
        last_hidden = outputs.hidden_states[-1][:, -1, :]
        return self.reward_head(last_hidden).squeeze(-1)

class RLTrainer:
    def __init__(self, policy: nn.Module, ref_policy: nn.Module,
                 reward_model: RewardModel, tokenizer, beta=0.04, lr=1e-5):
        self.policy = policy
        self.ref_policy = ref_policy
        self.reward_model = reward_model
        self.tokenizer = tokenizer
        self.beta = beta
        self.optimizer = torch.optim.AdamW(policy.parameters(), lr=lr)
        self.ref_policy.eval()

    def train_reward_model(self, examples: List[PreferenceExample], epochs=1):
        self.reward_model.train()
        optimizer = torch.optim.AdamW(self.reward_model.parameters(), lr=1e-5)
        for epoch in range(epochs):
            for ex in examples:
                chosen_enc = self.tokenizer(ex.prompt + ex.chosen, return_tensors="pt")
                rejected_enc = self.tokenizer(ex.prompt + ex.rejected, return_tensors="pt")
                r_chosen = self.reward_model(**chosen_enc)
                r_rejected = self.reward_model(**rejected_enc)
                loss = -F.logsigmoid(r_chosen - r_rejected).mean()
                optimizer.zero_grad()
                loss.backward()
                optimizer.step()

    def ppo_update(self, query, response, advantages):
        # Tokenize
        full = query + response
        input_ids = self.tokenizer(full, return_tensors="pt").input_ids.to("cuda")
        # Current policy logits
        outputs = self.policy(input_ids, labels=input_ids)
        log_probs = F.log_softmax(outputs.logits[:, :-1, :], dim=-1)
        selected_log_probs = log_probs.gather(-1, input_ids[:, 1:].unsqueeze(-1)).squeeze(-1)
        # Ref policy logits
        with torch.no_grad():
            ref_outputs = self.ref_policy(input_ids, labels=input_ids)
            ref_log_probs = F.log_softmax(ref_outputs.logits[:, :-1, :], dim=-1)
            ref_selected = ref_log_probs.gather(-1, input_ids[:, 1:].unsqueeze(-1)).squeeze(-1)
        # KL penalty
        kl = selected_log_probs - ref_selected
        # PPO clipped objective
        ratio = torch.exp(selected_log_probs - ref_selected.detach())
        clip_ratio = torch.clamp(ratio, 0.8, 1.2)
        pg_loss = -torch.min(ratio * advantages, clip_ratio * advantages).mean()
        kl_loss = (kl ** 2).mean()
        loss = pg_loss + self.beta * kl_loss
        self.optimizer.zero_grad()
        loss.backward()
        torch.nn.utils.clip_grad_norm_(self.policy.parameters(), 1.0)
        self.optimizer.step()
        return loss.item()

    def generate_and_train(self, prompts: List[str], num_steps=1000):
        for step in range(num_steps):
            prompt = np.random.choice(prompts)
            input_ids = self.tokenizer(prompt, return_tensors="pt").input_ids.to("cuda")
            with torch.no_grad():
                gen_ids = self.policy.generate(input_ids, max_new_tokens=64,
                                               do_sample=True, temperature=0.7)
            response = self.tokenizer.decode(gen_ids[0][input_ids.shape[1]:])
            with torch.no_grad():
                reward = self.reward_model(gen_ids, torch.ones_like(gen_ids))
                advantage = (reward - reward.mean()) / (reward.std() + 1e-8)
            loss = self.ppo_update(prompt, response, advantage)
            if step % 100 == 0:
                print(f"Step {step}, loss={loss:.4f}, reward={reward.mean().item():.4f}")
```

## Interviewer Feedback
"Strong implementation covering the full RLHF pipeline. You correctly separate RM training and PPO, and the KL penalty is properly implemented. Consider adding a value head and GAE for advantage estimation."

## Key Takeaways
- RLHF requires three stages: SFT, reward modeling, and PPO
- Reward model is trained on binary preference comparisons
- PPO with KL penalty prevents policy collapse
- Reward normalization and clipping stabilize training
- The ratio clipping in PPO prevents destructive updates
