# SridhivyaMart / MonikaMart Local Launcher
Set-Location $PSScriptRoot
Write-Host "Starting MonikaMart E-Commerce Platform on Port 8080..." -ForegroundColor Cyan
mvn compile exec:java
