$ErrorActionPreference = "Stop"

$baseDir = "c:\Users\jratombo-adm\Desktop\java_learning_lab"
$inventoryFile = Join-Path $baseDir "MODULE_INVENTORY.md"

$modules = Get-ChildItem -Path $baseDir -Directory | Where-Object { $_.Name -match "^\d{2}-.*$" } | Sort-Object Name

$inventoryData = @()

foreach ($mod in $modules) {
    $modDir = $mod.FullName
    
    $hasPom = Test-Path (Join-Path $modDir "pom.xml")
    $hasReadme = Test-Path (Join-Path $modDir "README.md")
    $hasTheory = Test-Path (Join-Path $modDir "THEORY.md")
    
    $testDir = Join-Path $modDir "src\test\java"
    $hasTestDir = Test-Path $testDir
    $testFiles = if ($hasTestDir) { (Get-ChildItem -Path $testDir -Recurse -File).Count } else { 0 }
    
    $mainDir = Join-Path $modDir "src\main\java"
    $hasMainDir = Test-Path $mainDir
    $mainFiles = if ($hasMainDir) { (Get-ChildItem -Path $mainDir -Recurse -File).Count } else { 0 }
    
    $hasLabJavaOnly = ($mainFiles -eq 1 -and (Get-ChildItem -Path $mainDir -Recurse -File).Name -eq "Lab.java")
    
    $status = "Stub"
    $quality = "Low"
    if ($hasPom -and $hasReadme -and $mainFiles -gt 0 -and $testFiles -gt 0 -and !$hasLabJavaOnly) {
        $status = "Complete"
        $quality = "High"
    } elseif ($hasPom -and ($mainFiles -gt 0 -or $hasLabJavaOnly)) {
        $status = "Partial"
        $quality = "Medium"
        if ($hasLabJavaOnly) { $quality = "Low" }
    }
    
    $inventoryData += [PSCustomObject]@{
        Module = $mod.Name
        Status = $status
        Quality = $quality
        Tests = if ($testFiles -gt 0) { "$testFiles files" } else { "None" }
        Documentation = if ($hasReadme -and $hasTheory) { "Good" } elseif ($hasReadme) { "Basic" } else { "None" }
    }
}

$inventoryContent = @()
$inventoryContent += "# Module Completion Status"
$inventoryContent += ""
$inventoryContent += "| Module | Status | Quality | Tests | Documentation |"
$inventoryContent += "|--------|--------|---------|-------|---------------|"

foreach ($item in $inventoryData) {
    $inventoryContent += "| $($item.Module) | $($item.Status) | $($item.Quality) | $($item.Tests) | $($item.Documentation) |"
}

$inventoryContent += ""
$inventoryContent += "Generated on $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')"

Set-Content -Path $inventoryFile -Value ($inventoryContent -join "`r`n") -Encoding UTF8

Write-Host "Inventory generated at $inventoryFile"
