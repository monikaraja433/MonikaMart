$baseUrl = "http://localhost:8080"
$ErrorActionPreference = "Stop"

Write-Host "=== TEST 1: Health Check ==="
$health = Invoke-RestMethod -Uri "$baseUrl/api/v1/health" -Method Get
Write-Host "Health check response: $($health | ConvertTo-Json -Compress)"
if ($health.status -ne "UP" -or $health.db -ne "UP") {
    throw "Health check failed!"
}

Write-Host "`n=== TEST 2: Product Catalog ==="
$catalog = Invoke-WebRequest -Uri "$baseUrl/products" -UseBasicParsing
Write-Host "Catalog Status: $($catalog.StatusCode)"
if ($catalog.StatusCode -ne 200) {
    throw "Catalog failed!"
}

Write-Host "`n=== TEST 3: Admin Login (monikaraja433@gmail.com) ==="
$adminSession = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$loginBody = @{
    email = "monikaraja433@gmail.com"
    password = $env:ADMIN_PASSWORD
}
$adminLoginResp = Invoke-WebRequest -Uri "$baseUrl/login" -Method Post -Body $loginBody -WebSession $adminSession -MaximumRedirection 0 -ErrorAction SilentlyContinue
Write-Host "Admin Login HTTP Status: $($adminLoginResp.StatusCode)"
Write-Host "Admin Redirect: $($adminLoginResp.Headers.Location)"

# Follow redirect to /admin/dashboard
$adminDash = Invoke-WebRequest -Uri "$baseUrl/admin/dashboard" -WebSession $adminSession -UseBasicParsing
Write-Host "Admin Dashboard Status: $($adminDash.StatusCode)"
if ($adminDash.Content -match "Live Activity & Notifications") {
    Write-Host "SUCCESS: 'Live Activity & Notifications' section is present on Admin Dashboard!"
} else {
    throw "Missing 'Live Activity & Notifications' on Admin Dashboard!"
}

Write-Host "`n=== TEST 4: Security: Non-Admin Email CANNOT Login as Admin ==="
$fakeAdminSession = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$fakeLoginBody = @{
    email = "admin@monikamart.com"
    password = "Admin@123"
}
$fakeLoginResp = Invoke-WebRequest -Uri "$baseUrl/login" -Method Post -Body $fakeLoginBody -WebSession $fakeAdminSession -UseBasicParsing
if ($fakeLoginResp.Content -match "Invalid email or password" -or $fakeLoginResp.Content -match "Unauthorized") {
    Write-Host "SUCCESS: Old/arbitrary admin email successfully rejected!"
} else {
    throw "Security vulnerability: Old admin email was not rejected!"
}

Write-Host "`n=== TEST 5: User Registration ==="
$newUserEmail = "buyer_test_" + (Get-Random -Minimum 1000 -Maximum 9999) + "@monikamart.com"
$regSession = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$regBody = @{
    name = "Test Buyer Automated"
    email = $newUserEmail
    password = "Password@123"
    confirmPassword = "Password@123"
    role = "BUYER"
    phone = "+91 9988776655"
    address = "123 Test Street, Chennai"
}
$regResp = Invoke-WebRequest -Uri "$baseUrl/register" -Method Post -Body $regBody -WebSession $regSession -MaximumRedirection 0 -ErrorAction SilentlyContinue
Write-Host "Registration Redirect: $($regResp.Headers.Location)"

# Follow redirect and check for success message
$productsAfterReg = Invoke-WebRequest -Uri "$baseUrl/products?registered=true" -WebSession $regSession -UseBasicParsing
if ($productsAfterReg.Content -match "Account registered successfully" -or $productsAfterReg.Content -match "registered successfully") {
    Write-Host "SUCCESS: Registration success message displayed!"
} else {
    throw "Missing registration success message!"
}

Write-Host "`n=== TEST 6: Add to Cart & Success Message ==="
$addCartBody = @{
    productId = "1"
    quantity = "1"
}
$addCartResp = Invoke-WebRequest -Uri "$baseUrl/cart/add" -Method Post -Body $addCartBody -WebSession $regSession -MaximumRedirection 0 -ErrorAction SilentlyContinue
Write-Host "Add to Cart Redirect: $($addCartResp.Headers.Location)"

$cartPage = Invoke-WebRequest -Uri "$baseUrl/cart?added=true" -WebSession $regSession -UseBasicParsing
if ($cartPage.Content -match "Product added to cart successfully!") {
    Write-Host "SUCCESS: 'Product added to cart successfully!' message verified!"
} else {
    throw "Missing exact Add to Cart success message!"
}

Write-Host "`n=== TEST 7: Place Order & Success Message ==="
$orderBody = @{
    shippingAddress = "123 Test Street, Chennai, Tamil Nadu - 600025"
    paymentMethod = "MOCK_PAYMENT"
}
$orderResp = Invoke-WebRequest -Uri "$baseUrl/checkout/place" -Method Post -Body $orderBody -WebSession $regSession -MaximumRedirection 0 -ErrorAction SilentlyContinue
Write-Host "Place Order Redirect: $($orderResp.Headers.Location)"

$orderLocation = $orderResp.Headers.Location
$orderDetail = Invoke-WebRequest -Uri "$baseUrl$orderLocation" -WebSession $regSession -UseBasicParsing
if ($orderDetail.Content -match "Order placed successfully!") {
    Write-Host "SUCCESS: 'Order placed successfully!' message verified!"
} else {
    throw "Missing exact Place Order success message!"
}

Write-Host "`n=== TEST 8: Verify Admin Notifications Logged All Activities ==="
$adminDashUpdated = Invoke-WebRequest -Uri "$baseUrl/admin/dashboard" -WebSession $adminSession -UseBasicParsing
if ($adminDashUpdated.Content -match "ORDER_PLACED" -or $adminDashUpdated.Content -match "Order placed") {
    Write-Host "SUCCESS: New order activity appears in Admin Notifications!"
} else {
    throw "Order placement activity not found in Admin Notifications!"
}
if ($adminDashUpdated.Content -match "REGISTRATION" -or $adminDashUpdated.Content -match "registered") {
    Write-Host "SUCCESS: New user registration appears in Admin Notifications!"
} else {
    throw "Registration activity not found in Admin Notifications!"
}

Write-Host "`n========================================================"
Write-Host " ALL TEST FLOWS COMPLETED AND VERIFIED 100% SUCCESSFULLY! "
Write-Host "========================================================"
