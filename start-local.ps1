Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$logsDirectory = Join-Path $projectRoot 'logs'

if (-not (Test-Path $logsDirectory)) {
    New-Item -ItemType Directory -Path $logsDirectory | Out-Null
}

function Get-PortListener {
    param(
        [int]$Port
    )

    Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue |
        Select-Object -First 1
}

function Get-ProcessCommandLine {
    param(
        [int]$ProcessId
    )

    $process = Get-CimInstance Win32_Process -Filter "ProcessId = $ProcessId" -ErrorAction SilentlyContinue
    if ($process) {
        return $process.CommandLine
    }

    return $null
}

function Start-BackgroundProcess {
    param(
        [string]$WorkingDirectory,
        [string]$Command,
        [string]$LogFile
    )

    $escapedWorkingDirectory = $WorkingDirectory.Replace("'", "''")
    $escapedLogFile = $LogFile.Replace("'", "''")
    $script = "Set-Location '$escapedWorkingDirectory'; $Command *> '$escapedLogFile'"

    Start-Process powershell -ArgumentList '-NoProfile', '-Command', $script -PassThru | Out-Null
}

function Wait-ForPort {
    param(
        [int]$Port,
        [int]$TimeoutSeconds = 20
    )

    for ($attempt = 0; $attempt -lt $TimeoutSeconds; $attempt++) {
        Start-Sleep -Seconds 1
        if (Get-PortListener -Port $Port) {
            return $true
        }
    }

    return $false
}

$backendListener = Get-PortListener -Port 8080
if ($backendListener) {
    $backendCommandLine = Get-ProcessCommandLine -ProcessId $backendListener.OwningProcess
    if ($backendCommandLine -and $backendCommandLine.Contains('com.salihguneyin.stockwise.StockwiseApplication')) {
        Write-Output 'Backend is already running on http://localhost:8080'
    }
    else {
        Write-Output ("Port 8080 is already in use by PID {0}. Stop that process or free the port before starting the backend." -f $backendListener.OwningProcess)
    }
}
else {
    Start-BackgroundProcess `
        -WorkingDirectory (Join-Path $projectRoot 'backend') `
        -Command '.\mvnw.cmd clean spring-boot:run' `
        -LogFile (Join-Path $logsDirectory 'backend.log')

    Write-Output 'Starting backend on http://localhost:8080'
    [void](Wait-ForPort -Port 8080)
}

$frontendListener = Get-PortListener -Port 5173
if ($frontendListener) {
    $frontendCommandLine = Get-ProcessCommandLine -ProcessId $frontendListener.OwningProcess
    if ($frontendCommandLine -and $frontendCommandLine.ToLowerInvariant().Contains('stockwise')) {
        Write-Output 'Frontend is already running on http://127.0.0.1:5173'
    }
    else {
        Write-Output ("Port 5173 is already in use by PID {0}. Stop that process or change the frontend port before starting Vite." -f $frontendListener.OwningProcess)
    }
}
else {
    Start-BackgroundProcess `
        -WorkingDirectory (Join-Path $projectRoot 'frontend') `
        -Command 'npm run dev -- --host 127.0.0.1' `
        -LogFile (Join-Path $logsDirectory 'frontend.log')

    Write-Output 'Starting frontend on http://127.0.0.1:5173'
}

Write-Output 'If the page is not ready immediately, wait a few more seconds and refresh http://127.0.0.1:5173'

