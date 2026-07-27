# Mock Interview: Fine-Tune a 7B Model for Code Generation

## Scenario
You are interviewing for a ML engineer role at a developer tools company. They want to fine-tune CodeLlama-7B for their internal code generation use case.

## Interviewer Opening Question
"Walk me through the full pipeline to fine-tune a 7B parameter model for code generation — from data preparation to deployment."

## Candidate Response
"I'd use LoRA fine-tuning on a curated dataset of code-text pairs. The pipeline covers: data curation, prompt formatting, training with QLoRA (4-bit quantization), evaluation on HumanEval, and deployment with vLLM for inference."

## Interviewer Probing Questions

**Q: How do you format the training data?**
"I'd use the Alpaca-style chat template with special tokens: <s>[INST] Write a function to... [/INST] def solution():\n... </s>. This matches the base model's chat format."

**Q: What rank for LoRA and which layers?**
"Rank r=16, alpha=32. I target Q, K, V, O projection matrices and the MLP layers. This gives good adaptation without overfitting."

**Q: How do you prevent catastrophic forgetting of general coding ability?**
"Mix in 10% general code data from The Stack during training. Use a learning rate warmup and cosine decay. Monitor validation perplexity on held-out general code."

## Candidate Solution (Python)

```python
import torch
from transformers import (
    AutoModelForCausalLM, AutoTokenizer, TrainingArguments,
    BitsAndBytesConfig
)
from peft import LoraConfig, get_peft_model, prepare_model_for_kbit_training
from datasets import Dataset
from trl import SFTTrainer

class CodeFineTuner:
    def __init__(self, base_model="codellama/CodeLlama-7b-hf"):
        self.base_model = base_model
        self.tokenizer = AutoTokenizer.from_pretrained(base_model)
        self.tokenizer.pad_token = self.tokenizer.eos_token

    def prepare_quantized_model(self):
        bnb_config = BitsAndBytesConfig(
            load_in_4bit=True,
            bnb_4bit_quant_type="nf4",
            bnb_4bit_compute_dtype=torch.bfloat16,
            bnb_4bit_use_double_quant=True,
        )
        model = AutoModelForCausalLM.from_pretrained(
            self.base_model,
            quantization_config=bnb_config,
            device_map="auto",
            torch_dtype=torch.bfloat16,
        )
        model = prepare_model_for_kbit_training(model)
        return model

    def setup_lora(self, model):
        lora_config = LoraConfig(
            r=16,
            lora_alpha=32,
            target_modules=["q_proj", "k_proj", "v_proj", "o_proj",
                            "gate_proj", "up_proj", "down_proj"],
            lora_dropout=0.05,
            bias="none",
            task_type="CAUSAL_LM",
        )
        return get_peft_model(model, lora_config)

    def format_example(self, instruction: str, code: str) -> str:
        return f"<s>[INST] {instruction} [/INST] {code}</s>"

    def create_dataset(self, instructions, codes):
        texts = [self.format_example(i, c) for i, c in zip(instructions, codes)]
        return Dataset.from_dict({"text": texts})

    def train(self, train_dataset, output_dir="./code-model", num_epochs=3):
        model = self.prepare_quantized_model()
        model = self.setup_lora(model)

        training_args = TrainingArguments(
            output_dir=output_dir,
            per_device_train_batch_size=4,
            gradient_accumulation_steps=4,
            learning_rate=2e-4,
            warmup_ratio=0.03,
            lr_scheduler_type="cosine",
            logging_steps=10,
            save_strategy="epoch",
            num_train_epochs=num_epochs,
            bf16=True,
            tf32=True,
            gradient_checkpointing=True,
            optim="adamw_8bit",
        )

        trainer = SFTTrainer(
            model=model,
            args=training_args,
            train_dataset=train_dataset,
            tokenizer=self.tokenizer,
            max_seq_length=2048,
            dataset_text_field="text",
        )
        trainer.train()
        trainer.save_model(output_dir)
        return trainer

    def evaluate(self, model, test_prompts):
        model.eval()
        results = []
        for prompt in test_prompts:
            inputs = self.tokenizer(prompt, return_tensors="pt").to("cuda")
            with torch.no_grad():
                outputs = model.generate(
                    **inputs,
                    max_new_tokens=256,
                    temperature=0.1,
                    do_sample=True,
                )
            generated = self.tokenizer.decode(outputs[0], skip_special_tokens=True)
            results.append(generated)
        return results
```

## Interviewer Feedback
"Comprehensive walkthrough covering quantization, LoRA, data formatting, and training. Your choice of r=16, target modules, and LR schedule shows solid practical experience. Consider adding Paged AdamW for memory stability."

## Key Takeaways
- QLoRA with 4-bit quantization enables 7B fine-tuning on consumer GPUs
- Target all attention projection and MLP layers for full adaptation
- Mix general code data to prevent catastrophic forgetting
- Use SFTTrainer from TRL for simplified supervised fine-tuning
- Evaluate on HumanEval or a held-out test set
