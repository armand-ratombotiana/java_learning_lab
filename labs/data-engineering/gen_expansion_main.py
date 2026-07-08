#!/usr/bin/env python3
"""Generate 12 new Data Engineering Academy labs (09-20)."""
import os, pathlib, sys

BASE = r"C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\data-engineering"
DOC_FILES = [
    "README.md","THEORY.md","WHY_IT_EXISTS.md","WHY_IT_MATTERS.md",
    "HISTORY.md","MENTAL_MODELS.md","HOW_IT_WORKS.md","INTERNALS.md",
    "MATH_FOUNDATION.md","VISUAL_GUIDE.md","CODE_DEEP_DIVE.md",
    "STEP_BY_STEP.md","COMMON_MISTAKES.md","DEBUGGING.md","REFACTORING.md",
    "PERFORMANCE.md","SECURITY.md","ARCHITECTURE.md","EXERCISES.md",
    "QUIZ.md","FLASHCARDS.md","INTERVIEW.md","REFLECTION.md","REFERENCES.md"
]
SUBDIRS = ["BENCHMARK","CHALLENGE","DIAGRAMS","MINI_PROJECT","REAL_WORLD_PROJECT","SOLUTION","TESTS"]

def ensure(path):
    pathlib.Path(path).mkdir(parents=True, exist_ok=True)

def write_file(path, content):
    ensure(os.path.dirname(path))
    with open(path, "w", encoding="utf-8") as f:
        f.write(content.strip() + "\n")
    print(f"  WROTE: {os.path.basename(path)}")

def gitkeep(path):
    f = os.path.join(path, ".gitkeep")
    if not os.path.exists(f):
        with open(f, "w") as fp: pass

# Load lab content modules
sys.path.insert(0, BASE)
from gen_lab_content import LAB_CONTENT

for lab_dir_name, lab_data in sorted(LAB_CONTENT.items()):
    lab_path = os.path.join(BASE, lab_dir_name)
    print(f"\nCreating {lab_dir_name}...")
    ensure(lab_path)

    # Create doc files
    for doc in DOC_FILES:
        key = doc.replace(".md", "")
        content = lab_data.get(key, "")
        if content:
            write_file(os.path.join(lab_path, doc), content)

    # Create subdirs with .gitkeep
    for sd in SUBDIRS:
        sp = os.path.join(lab_path, sd)
        ensure(sp)
        gitkeep(sp)

    # Create Java sources
    package = lab_data.get("package", "com.dataeng.xx")
    pkg_path = package.replace(".", "/")
    src_base = os.path.join(lab_path, "src", "main", "java", pkg_path)
    test_base = os.path.join(lab_path, "src", "test", "java", pkg_path)
    ensure(src_base)

    java_sources = lab_data.get("JavaSources", {})
    for fname, content in java_sources.items():
        pkg_line = f"package {package};"
        full_content = pkg_line + "\n\n" + content.strip()
        write_file(os.path.join(src_base, fname), full_content)

    # Create test files
    ensure(test_base)
    tests = lab_data.get("Tests", {})
    for fname, content in tests.items():
        pkg_line = f"package {package};"
        test_imports = (
            "import static org.junit.jupiter.api.Assertions.*;\n"
            "import org.junit.jupiter.api.Test;\n"
            "import org.junit.jupiter.api.BeforeEach;\n"
        )
        full_content = pkg_line + "\n\n" + test_imports + "\n" + content.strip()
        write_file(os.path.join(test_base, fname), full_content)

print("\nDone! All 12 labs created.")
