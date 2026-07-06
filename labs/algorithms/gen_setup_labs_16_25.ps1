$base = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\algorithms"

$labs = @(
    @{num="16"; name="16-string-matching"; topic="String Matching Algorithms"},
    @{num="17"; name="17-network-flow"; topic="Network Flow Algorithms"},
    @{num="18"; name="18-computational-geometry"; topic="Computational Geometry"},
    @{num="19"; name="19-number-theory"; topic="Number Theory & Modular Arithmetic"},
    @{num="20"; name="20-randomized-algorithms"; topic="Randomized Algorithms"},
    @{num="21"; name="21-caching-algorithms"; topic="Caching Algorithms"},
    @{num="22"; name="22-game-theory"; topic="Game Theory & Minimax"},
    @{num="23"; name="23-load-balancing"; topic="Load Balancing Algorithms"},
    @{num="24"; name="24-scheduling-algorithms"; topic="Scheduling Algorithms"},
    @{num="25"; name="25-optimization-algorithms"; topic="Optimization Algorithms (Metaheuristics)"}
)

$subdirs = @("MINI_PROJECT", "REAL_WORLD_PROJECT", "CHALLENGE", "TESTS", "BENCHMARK", "DIAGRAMS", "SOLUTION")
$mdFiles = @("README.md","THEORY.md","WHY_IT_EXISTS.md","WHY_IT_MATTERS.md","HISTORY.md","MENTAL_MODELS.md","HOW_IT_WORKS.md","INTERNALS.md","MATH_FOUNDATION.md","VISUAL_GUIDE.md","CODE_DEEP_DIVE.md","STEP_BY_STEP.md","COMMON_MISTAKES.md","DEBUGGING.md","REFACTORING.md","PERFORMANCE.md","SECURITY.md","ARCHITECTURE.md","EXERCISES.md","QUIZ.md","FLASHCARDS.md","INTERVIEW.md","REFLECTION.md","REFERENCES.md")

# Create directories
foreach ($lab in $labs) {
    $labDir = Join-Path $base $lab.name
    Write-Host "Creating $($lab.name)..."
    New-Item -ItemType Directory -Path $labDir -Force | Out-Null
    New-Item -ItemType Directory -Path (Join-Path $labDir "src\main\java\com\algo\lab$($lab.num)") -Force | Out-Null
    New-Item -ItemType Directory -Path (Join-Path $labDir "src\test\java\com\algo\lab$($lab.num)") -Force | Out-Null
    foreach ($sd in $subdirs) {
        New-Item -ItemType Directory -Path (Join-Path $labDir $sd) -Force | Out-Null
    }
    foreach ($mf in $mdFiles) {
        $mfPath = Join-Path $labDir $mf
        if (-not (Test-Path $mfPath)) {
            $heading = $mf -replace '\.md$',''
            $heading = $heading -replace '-',' '
            Set-Content -Path $mfPath -Value "# $($lab.topic) — $heading"
        }
    }
}

Write-Host "All directories created."
