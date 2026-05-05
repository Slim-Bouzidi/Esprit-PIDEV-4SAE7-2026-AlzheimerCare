# Script pour generer les evenements d'agenda automatiquement

Write-Host "Generation des evenements d'agenda..." -ForegroundColor Cyan
Write-Host ""

# Calculer les dates (aujourd'hui + 7 jours)
$dateDebut = Get-Date -Format "yyyy-MM-dd"
$dateFin = (Get-Date).AddDays(7).ToString("yyyy-MM-dd")

Write-Host "Periode: $dateDebut au $dateFin" -ForegroundColor Yellow
Write-Host ""

try {
    $url = "http://localhost:8098/api/agenda/generer?debut=$dateDebut&fin=$dateFin"
    $response = Invoke-RestMethod -Uri $url `
        -Method Post `
        -ContentType "application/json"
    
    Write-Host "Succes! Evenements generes: $($response.Count)" -ForegroundColor Green
    Write-Host ""
    
    if ($response.Count -gt 0) {
        Write-Host "Types d'evenements crees:" -ForegroundColor Yellow
        Write-Host "  - Medicaments (depuis les traitements actifs)" -ForegroundColor White
        Write-Host "  - Rendez-vous (depuis les RDV planifies)" -ForegroundColor White
        Write-Host "  - Repas (petit-dejeuner, dejeuner, diner)" -ForegroundColor White
        Write-Host ""
        Write-Host "Acces a l'agenda: http://localhost:4200/soignant-agenda" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "Rafraichissez la page pour voir les evenements!" -ForegroundColor Green
    } else {
        Write-Host "Aucun nouvel evenement cree (peut-etre deja existants)" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "Verifiez qu'il y a des patients actifs dans la base" -ForegroundColor Yellow
    }
    
} catch {
    Write-Host "Erreur lors de la generation:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Write-Host ""
    Write-Host "Verifiez que:" -ForegroundColor Yellow
    Write-Host "  1. Le backend est lance (port 8098)" -ForegroundColor White
    Write-Host "  2. Des patients existent dans la base de donnees" -ForegroundColor White
    Write-Host "  3. La connexion MySQL fonctionne" -ForegroundColor White
}
