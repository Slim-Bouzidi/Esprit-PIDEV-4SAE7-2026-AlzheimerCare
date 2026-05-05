# Script PowerShell pour importer le realm Keycloak

Write-Host "🔐 Import du Realm Alzheimer dans Keycloak" -ForegroundColor Cyan
Write-Host ""

# Copier le fichier JSON dans le conteneur Keycloak
Write-Host "📋 Copie du fichier realm dans le conteneur..." -ForegroundColor Yellow
docker cp alzheimer-realm-export.json keycloak:/tmp/alzheimer-realm-export.json

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Erreur lors de la copie du fichier" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Fichier copié avec succès" -ForegroundColor Green
Write-Host ""

# Importer le realm
Write-Host "📥 Import du realm..." -ForegroundColor Yellow
docker exec keycloak /opt/keycloak/bin/kc.sh import --file /tmp/alzheimer-realm-export.json

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Erreur lors de l'import du realm" -ForegroundColor Red
    Write-Host ""
    Write-Host "ℹ️  Le realm existe peut-être déjà. Essayez de le supprimer d'abord depuis l'interface web." -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "✅ Realm importé avec succès!" -ForegroundColor Green
Write-Host ""
Write-Host "🌐 Accédez à Keycloak: http://localhost:8081" -ForegroundColor Cyan
Write-Host "👤 Admin: admin / admin" -ForegroundColor Cyan
Write-Host ""
Write-Host "📋 Utilisateurs créés:" -ForegroundColor Yellow
Write-Host "  - admin@alzheimer.fr / admin123 (ADMIN)" -ForegroundColor White
Write-Host "  - soignant@alzheimer.fr / soignant123 (SOIGNANT)" -ForegroundColor White
Write-Host "  - doctor@alzheimer.fr / doctor123 (MEDECIN)" -ForegroundColor White
Write-Host "  - aidant@alzheimer.fr / aidant123 (AIDANT)" -ForegroundColor White
Write-Host "  - patient@alzheimer.fr / patient123 (PATIENT)" -ForegroundColor White
Write-Host ""
