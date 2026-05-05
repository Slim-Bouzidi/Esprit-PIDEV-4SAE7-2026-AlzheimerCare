# Script simple pour creer le realm via API

Write-Host "Creation du Realm Alzheimer..." -ForegroundColor Cyan

# Obtenir le token
$tokenResponse = Invoke-RestMethod -Uri "http://localhost:8081/realms/master/protocol/openid-connect/token" `
    -Method Post `
    -ContentType "application/x-www-form-urlencoded" `
    -Body @{
        username = "admin"
        password = "admin"
        grant_type = "password"
        client_id = "admin-cli"
    }

$token = $tokenResponse.access_token

# Creer le realm
$realmData = @{
    id = "alzheimer-realm"
    realm = "alzheimer-realm"
    displayName = "Alzheimer System"
    enabled = $true
    sslRequired = "none"
    registrationAllowed = $false
    loginWithEmailAllowed = $true
    duplicateEmailsAllowed = $false
    resetPasswordAllowed = $true
    editUsernameAllowed = $false
} | ConvertTo-Json

try {
    Invoke-RestMethod -Uri "http://localhost:8081/admin/realms" `
        -Method Post `
        -Headers @{
            "Authorization" = "Bearer $token"
            "Content-Type" = "application/json"
        } `
        -Body $realmData
    
    Write-Host "Realm cree avec succes!" -ForegroundColor Green
} catch {
    Write-Host "Erreur ou realm existe deja" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Rafraichissez la page Keycloak: http://localhost:8081/admin" -ForegroundColor Cyan
