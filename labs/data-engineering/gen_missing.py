#!/usr/bin/env python3
"""Generate missing Data Engineering Academy markdown files."""
import os, pathlib

BASE = r"C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\data-engineering"

labs = [
    "01-data-pipelines", "02-etl-processes", "03-data-warehousing",
    "04-apache-spark", "05-apache-flink", "06-kafka-streaming",
    "07-data-lake", "08-workflow-orchestration", "09-data-quality",
    "10-change-data-capture", "11-feature-stores", "12-data-governance"
]

files_24 = [
    "README.md","THEORY.md","WHY_IT_EXISTS.md","WHY_IT_MATTERS.md",
    "HISTORY.md","MENTAL_MODELS.md","HOW_IT_WORKS.md","INTERNALS.md",
    "MATH_FOUNDATION.md","VISUAL_GUIDE.md","CODE_DEEP_DIVE.md",
    "STEP_BY_STEP.md","COMMON_MISTAKES.md","DEBUGGING.md","REFACTORING.md",
    "PERFORMANCE.md","SECURITY.md","ARCHITECTURE.md","EXERCISES.md",
    "QUIZ.md","FLASHCARDS.md","INTERVIEW.md","REFLECTION.md","REFERENCES.md"
]

# Check what's missing
for lab in labs:
    lab_dir = os.path.join(BASE, lab)
    existing = {f.name for f in pathlib.Path(lab_dir).glob("*.md")}
    missing = [f for f in files_24 if f not in existing]
    if missing:
        print(f"{lab}: missing {len(missing)}: {', '.join(missing[:5])}...")
    else:
        print(f"{lab}: ALL 24 FILES PRESENT")

print("\nDone checking")
