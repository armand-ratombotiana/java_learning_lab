$base = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\algorithms"

$labs = @(
    @{n="16"; d="16-string-matching"; t="String Matching Algorithms"},
    @{n="17"; d="17-network-flow"; t="Network Flow Algorithms"},
    @{n="18"; d="18-computational-geometry"; t="Computational Geometry"},
    @{n="19"; d="19-number-theory"; t="Number Theory and Modular Arithmetic"},
    @{n="20"; d="20-randomized-algorithms"; t="Randomized Algorithms"},
    @{n="21"; d="21-caching-algorithms"; t="Caching Algorithms"},
    @{n="22"; d="22-game-theory"; t="Game Theory and Minimax"},
    @{n="23"; d="23-load-balancing"; t="Load Balancing Algorithms"},
    @{n="24"; d="24-scheduling-algorithms"; t="Scheduling Algorithms"},
    @{n="25"; d="25-optimization-algorithms"; t="Optimization Algorithms"}
)

$subdirs = "MINI_PROJECT","REAL_WORLD_PROJECT","CHALLENGE","TESTS","BENCHMARK","DIAGRAMS","SOLUTION"

foreach ($lab in $labs) {
    $labDir = Join-Path $base $lab.d
    Write-Host ("Creating " + $lab.d + "...")
    New-Item -ItemType Directory -Path $labDir -Force | Out-Null
    New-Item -ItemType Directory -Path (Join-Path $labDir "src\main\java\com\algo\lab$($lab.n)") -Force | Out-Null
    New-Item -ItemType Directory -Path (Join-Path $labDir "src\test\java\com\algo\lab$($lab.n)") -Force | Out-Null
    foreach ($sd in $subdirs) {
        New-Item -ItemType Directory -Path (Join-Path $labDir $sd) -Force | Out-Null
    }
}

Write-Host "All directories created."
