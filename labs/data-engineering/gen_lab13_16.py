#!/usr/bin/env python3
"""Generate labs 13-16: Delta Lake, Iceberg, Data Quality, Data Catalog."""
import os, pathlib, sys

BASE = r"C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\data-engineering"
DOCS = ["README.md","THEORY.md","WHY_IT_EXISTS.md","WHY_IT_MATTERS.md",
    "HISTORY.md","MENTAL_MODELS.md","HOW_IT_WORKS.md","INTERNALS.md",
    "MATH_FOUNDATION.md","VISUAL_GUIDE.md","CODE_DEEP_DIVE.md",
    "STEP_BY_STEP.md","COMMON_MISTAKES.md","DEBUGGING.md","REFACTORING.md",
    "PERFORMANCE.md","SECURITY.md","ARCHITECTURE.md","EXERCISES.md",
    "QUIZ.md","FLASHCARDS.md","INTERVIEW.md","REFLECTION.md","REFERENCES.md"]
SUBDIRS = ["BENCHMARK","CHALLENGE","DIAGRAMS","MINI_PROJECT","REAL_WORLD_PROJECT","SOLUTION","TESTS"]

def ensure(p): pathlib.Path(p).mkdir(parents=True, exist_ok=True)
def write(p, c):
    with open(p, "w", encoding="utf-8") as f: f.write(c.strip() + "\n")
    print(f"  {os.path.basename(p)}")

from gen_content_13_16 import LABS, make_docs

for dname, data in sorted(LABS.items()):
    lab = os.path.join(BASE, dname)
    ensure(lab)
    docs = make_docs(data)
    for doc in DOCS:
        key = doc.replace(".md","")
        c = docs.get(key,"")
        if c: write(os.path.join(lab, doc), c)
    for sd in SUBDIRS:
        ensure(os.path.join(lab, sd))
        gk = os.path.join(lab, sd, ".gitkeep")
        if not os.path.exists(gk): open(gk,"w").close()
    pkg = data.get("package","com.dataeng.xx")
    pkg_path = pkg.replace(".","/")
    src = os.path.join(lab, "src", "main", "java", pkg_path)
    ensure(src)
    for fname, content in data.get("JavaSources",{}).items():
        write(os.path.join(src, fname), f"package {pkg};\n\n{content.strip()}")
    tst = os.path.join(lab, "src", "test", "java", pkg_path)
    ensure(tst)
    for fname, content in data.get("Tests",{}).items():
        write(os.path.join(tst, fname), f"package {pkg};\n\nimport static org.junit.jupiter.api.Assertions.*;\nimport org.junit.jupiter.api.Test;\nimport org.junit.jupiter.api.BeforeEach;\n\n{content.strip()}")

print("Labs 13-16 done!")
