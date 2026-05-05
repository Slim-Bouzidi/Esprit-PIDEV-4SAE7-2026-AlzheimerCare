# Script pour créer le realm via l'API Keycloak

Write-Host "🔐 Création du Realm Alzheimer via API Keycloak" -ForegroundColor Cyan
Write-Host ""

# Attendre que Keycloak soit prêt
Write-Host "⏳ Attente de Keycloak..." -ForegroundColor Yellow
Start-Sleep -Seconds 5

# 1. Obtenir le token admin
Write-Host "🔑 Obtention du token admin..." -ForegroundColor Yellow
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
Write-Host "✅ Token obtenu" -ForegroundColor Green
Write-Host ""

# 2. Créer le realm
Write-Host "🏗️  Création du realm alzheimer-realm..." -ForegroundColor Yellow

$realmJson = Get-Content -Path "alzheimer-realm-export.json" -Raw

try {
    Invoke-RestMethod -Uri "http://localhost:8081/admin/realms" `
        -Method Post `
        -Headers @{
            "Authorization" = "Bearer $token"
            "Content-Type" = "application/json"
        } `
        -Body $realmJson
    
    Write-Host "✅ Realm créé avec succès!" -ForegroundColor Green
} catch {
    if ($_.Exception.Response.StatusCode -eq 409) {
        Write-Host "⚠️  Le realm existe déjà" -ForegroundColor Yellow
    } else {
        Write-Host "❌ Erreur: $($_.Exception.Message)" -ForegroundColor Red
        exit 1
    }
}

Write-Host ""
Write-Host "🎉 Configuration terminée!" -ForegroundColor Green
Write-Host ""
Write-Host "🌐 Accédez à Keycloak: http://localhost:8081/admin" -ForegroundColor Cyan
Write-Host "👤 Admin: admin / admin" -ForegroundColor Cyan
Write-Host ""
Write-Host "Selectionnez 'alzheimer-realm' dans le menu deroulant en haut a gauche" -ForegroundColor Yellow
Write-Host ""
