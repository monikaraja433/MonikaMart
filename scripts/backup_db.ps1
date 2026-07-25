# MonikaMart Database Backup Script
# Week 8 Requirement: Automated backup of H2 .mv.db database files

param (
    [string]$SourceDir = "data",
    [string]$BackupDir = "backups"
)

$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
if (-not (Test-Path -Path $BackupDir)) {
    New-Item -ItemType Directory -Path $BackupDir -Force | Out-Null
}

$dbFiles = Get-ChildItem -Path $SourceDir -Filter "*.db" -File
if ($dbFiles.Count -eq 0) {
    Write-Host "No active database files found in '$SourceDir'. Database may still be in memory." -ForegroundColor Yellow
    exit 0
}

foreach ($file in $dbFiles) {
    $backupFileName = "$($file.BaseName)_$timestamp$($file.Extension)"
    $destination = Join-Path -Path $BackupDir -ChildPath $backupFileName
    Copy-Item -Path $file.FullName -Destination $destination -Force
    Write-Host "[BACKUP] Successfully archived $($file.Name) to $destination" -ForegroundColor Green
}

Write-Host "Database snapshot backup completed successfully at $timestamp." -ForegroundColor Cyan
