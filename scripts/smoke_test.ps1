# MonikaMart PowerShell Automated Smoke Test
param (
    [string]$BaseUrl = "http://localhost:8080/monikamart"
)

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host " MonikaMart Automated Production Smoke Test" -ForegroundColor Cyan
Write-Host " Target URL: $BaseUrl" -ForegroundColor Cyan
Write-Host "=========================================================="

function Test-Endpoint {
    param (
        [string]$Name,
        [string]$Uri,
        [string]$Method = "GET",
        [string]$Body = $null,
        [int]$ExpectedStatus = 200
    )

    try {
        $params = @{
            Uri = $Uri
            Method = $Method
            UseBasicParsing = $true
            ErrorAction = "Stop"
        }
        if ($Body) {
            $params["Body"] = $Body
            $params["ContentType"] = "application/json"
        }

        $response = Invoke-WebRequest @params
        if ($response.StatusCode -eq $ExpectedStatus) {
            Write-Host "[PASS] $Name (HTTP $($response.StatusCode))" -ForegroundColor Green
            return $true
        } else {
            Write-Host "[FAIL] $Name (Expected $ExpectedStatus, got $($response.StatusCode))" -ForegroundColor Red
            return $false
        }
    } catch {
        Write-Host "[FAIL] $Name - Exception: $($_.Exception.Message)" -ForegroundColor Red
        return $false
    }
}

$allPassed = $true

# 1. Health API Check (Week 8 deliverable)
$passed = Test-Endpoint -Name "GET /api/v1/health" -Uri "$BaseUrl/api/v1/health"
if (-not $passed) { $allPassed = $false }

# 2. Product Catalog
$passed = Test-Endpoint -Name "GET /products (Catalog)" -Uri "$BaseUrl/products"
if (-not $passed) { $allPassed = $false }

# 3. Login Page
$passed = Test-Endpoint -Name "GET /login" -Uri "$BaseUrl/login"
if (-not $passed) { $allPassed = $false }

# 4. AI Chatbot API
$chatBody = '{"message":"What is your delivery timeframe?"}'
$passed = Test-Endpoint -Name "POST /api/chat" -Uri "$BaseUrl/api/chat" -Method "POST" -Body $chatBody
if (-not $passed) { $allPassed = $false }

Write-Host "----------------------------------------------------------"
if ($allPassed) {
    Write-Host " All smoke test verifications PASSED successfully." -ForegroundColor Green
} else {
    Write-Host " Smoke test reported failures. Review server logs." -ForegroundColor Red
}
