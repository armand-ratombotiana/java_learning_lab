# Repo-wide smoke runner: compiles and runs every listed main class with 20s timeout.
# Usage: pwsh tools/smoke-run.ps1 [-List tools/smoke-run-list.txt] [-Skip "com.mlops.lab05.ModelServingLab"]
param(
    [string]$List = (Join-Path $PSScriptRoot "smoke-run-list.txt"),
    [string[]]$Skip = @()
)

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$outBase = Join-Path $env:TEMP "opencode\run_smoke"
$clsDir = Join-Path $outBase "cls"
if (Test-Path $outBase) { Remove-Item $outBase -Recurse -Force }
New-Item -ItemType Directory -Path $clsDir -Force | Out-Null

$report = New-Object System.Collections.Generic.List[string]
$okCount = 0; $failCount = 0; $timeoutCount = 0; $skipCount = 0

$runs = Get-Content $List | Where-Object { $_.Trim() -ne "" }
foreach ($line in $runs) {
    $fullName = $line.Trim()
    if ($Skip -contains $fullName) { $skipCount++; continue }

    $r = $fullName -split "\."
    $className = $r[-1]
    $pkgPath = ($r[0..($r.Length - 2)] -join "\")

    $files = Get-ChildItem -Path $repoRoot -Recurse -Filter "$className.java" -File `
        | Where-Object { $_.FullName -match "\\src\\" }
    if ($files.Count -eq 0) {
        $report.Add("$fullName | NO_SOURCE")
        $failCount++
        continue
    }
    $d = $files[0].Directory
    while ($d.Name -ne "src" -and $d.Parent) { $d = $d.Parent }
    $srcDir = $d.FullName
    $srcFiles = Get-ChildItem -LiteralPath $srcDir -Recurse -Filter "*.java" -File | Where-Object { $_.FullName -notmatch "\\test\\" }
    $clsDirPer = Join-Path $outBase ("c" + $okCount + $failCount)
    & javac -proc:none -encoding UTF-8 --enable-preview --release 23 -d $clsDirPer $srcFiles.FullName 2>&1 | Out-Null
    if ($LASTEXITCODE -ne 0) {
        $report.Add("$fullName | COMPILE_FAIL")
        $failCount++
        continue
    }

    $ps = New-Object System.Diagnostics.ProcessStartInfo
    $ps.FileName = "java"
    $ps.UseShellExecute = $false
    $ps.CreateNoWindow = $true
    $ps.RedirectStandardOutput = $true
    $ps.RedirectStandardError = $true
    $ps.Arguments = "--enable-preview -cp `"$clsDirPer`" $fullName"
    $proc = New-Object System.Diagnostics.Process
    $proc.StartInfo = $ps
    if (-not $proc.Start()) { $report.Add("$fullName | START_FAILED"); $failCount++; continue }
    $stdout = $proc.StandardOutput.ReadToEndAsync()
    $stderr = $proc.StandardError.ReadToEndAsync()
    $proc.WaitForExit(20000) | Out-Null
    if (-not $proc.HasExited) {
        try { $proc.Kill() } catch {}
        $report.Add("$fullName | TIMEOUT")
        $timeoutCount++
        continue
    }
    $exit = $proc.ExitCode
    if ($exit -ne 0) {
        $err = $stderr.Result
        $report.Add("$fullName | EXIT=$exit | " + $err.Substring(0, [Math]::Min(300, $err.Length)).Replace("`r"," ").Replace("`n"," "))
        $failCount++
    } else { $okCount++ }
}
"OK=$okCount FAIL=$failCount TIMEOUT=$timeoutCount SKIPPED=$skipCount"
$report | Set-Content (Join-Path $outBase "smoke-report.txt")