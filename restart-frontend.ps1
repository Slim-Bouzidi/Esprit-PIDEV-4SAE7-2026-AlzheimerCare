# Script pour redémarrer le frontend Angular proprement

Write-Host "🔄 Redémarrage du frontend Angular..." -ForegroundColor Cyan

# Arrêter tous les processus Node liés à Angular
Write-Host "⏹️  Arrêt des processus Node..." -ForegroundColor Yellow
Get-Process | Where-Object {$_.ProcessName -eq "node"} | ForEach-Object {
    try {
        Stop-Process -Id $_.Id -Force -ErrorAction SilentlyContinue
        Write-Host "   ✓ Processus $($_.Id) arrêté" -ForegroundColor Green
    } catch {
        Write-Host "   ⚠ Impossible d'arrêter le processus $($_.Id)" -ForegroundColor Red
    }
}

Start-Sleep -Seconds 2

# Supprimer le cache Angular
Write-Host "🗑️  Suppression du cache Angular..." -ForegroundColor Yellow
$cachePath = "alzheimer-system-main/frontend/alzheimer-angular/.angular"
if (Test-Path $cachePath) {
    Remove-Item -Recurse -Force $cachePath
    Write-Host "   ✓ Cache supprimé" -ForegroundColor Green
} else {
    Write-Host "   ℹ Pas de cache à supprimer" -ForegroundColor Gray
}

# Aller dans le dossier Angular
Set-Location "alzheimer-system-main/frontend/alzheimer-angular"

Write-Host ""
Write-Host "✅ Prêt à redémarrer!" -ForegroundColor Green
Write-Host ""
Write-Host "Exécutez maintenant:" -ForegroundColor Cyan
Write-Host "   npm start" -ForegroundColor White
Write-Host ""
Write-Host "Puis testez:" -ForegroundColor Cyan
Write-Host "   http://localhost:4200/doctor-memoire-assistee" -ForegroundColor White
Write-Host ""
