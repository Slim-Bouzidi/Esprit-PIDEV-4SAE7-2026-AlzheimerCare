# Script pour supprimer tous les evenements d'agenda

Write-Host "Suppression de tous les evenements d'agenda..." -ForegroundColor Yellow
Write-Host ""

try {
    # Recuperer tous les evenements
    $evenements = Invoke-RestMethod -Uri "http://localhost:8098/api/agenda" -Method Get
    
    Write-Host "Nombre d'evenements a supprimer: $($evenements.Count)" -ForegroundColor Cyan
    Write-Host ""
    
    if ($evenements.Count -eq 0) {
        Write-Host "Aucun evenement a supprimer" -ForegroundColor Green
        exit 0
    }
    
    # Demander confirmation
    $confirmation = Read-Host "Voulez-vous vraiment supprimer tous les $($evenements.Count) evenements? (oui/non)"
    
    if ($confirmation -ne "oui") {
        Write-Host "Suppression annulee" -ForegroundColor Yellow
        exit 0
    }
    
    Write-Host ""
    Write-Host "Suppression en cours..." -ForegroundColor Yellow
    
    $deleted = 0
    $errors = 0
    
    foreach ($event in $evenements) {
        try {
            Invoke-RestMethod -Uri "http://localhost:8098/api/agenda/$($event.id)" -Method Delete
            $deleted++
            
            if ($deleted % 10 -eq 0) {
                Write-Host "  $deleted evenements supprimes..." -ForegroundColor Gray
            }
        } catch {
            $errors++
        }
    }
    
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "Suppression terminee!" -ForegroundColor Green
    Write-Host "  Evenements supprimes: $deleted" -ForegroundColor Green
    Write-Host "  Erreurs: $errors" -ForegroundColor Red
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Rafraichissez la page de l'agenda pour voir les changements" -ForegroundColor Yellow
    
} catch {
    Write-Host "Erreur lors de la recuperation des evenements:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Write-Host ""
    Write-Host "Verifiez que le backend est lance (port 8098)" -ForegroundColor Yellow
}
