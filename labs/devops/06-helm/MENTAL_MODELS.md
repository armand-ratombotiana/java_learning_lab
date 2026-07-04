# Mental Models for Helm

## 1. Package Manager Analogy
- **Helm** = apt/yum/homebrew for Kubernetes
- **Chart** = .deb/.rpm package
- **Repository** = Package repository (PPA, Homebrew tap)
- **Release** = Installed package
- **Values** = Configuration options

## 2. Template Engine
Think of Helm's template engine like a mail merge: you have a letter template (template YAML) with placeholders, and a data file (values.yaml). The engine fills in the placeholders and produces personalized letters (deployable YAML).

## 3. Three-Way Merge
Unlike a simple replace, Helm's three-way strategy is like a smart diff/patch tool that considers what was last deployed, what should be deployed, and what's currently running — correctly handling manual kubectl edits.

## 4. Chart as Recipe
A chart is a recipe with ingredients (templates), quantities (default values), and variations (values overrides). You can adjust for different tastes (environments) without changing the recipe.
