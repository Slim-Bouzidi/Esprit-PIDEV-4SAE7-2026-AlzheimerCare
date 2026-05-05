# Script pour creer des evenements pour toute la semaine

Write-Host "Creation d'evenements pour toute la semaine..." -ForegroundColor Cyan
Write-Host ""

$created = 0
$errors = 0

# Creer des evenements pour 7 jours
for ($i = 0; $i -lt 7; $i++) {
    $date = (Get-Date).AddDays($i).ToString("yyyy-MM-dd")
    $jourNom = (Get-Date).AddDays($i).ToString("dddd dd/MM")
    
    Write-Host "Creation pour $jourNom..." -ForegroundColor Yellow
    
    # Evenements quotidiens pour le patient 1 (aziz aziz)
    $evenements = @(
        @{
            patient = @{ id = 1 }
            patientNom = "aziz aziz"
            type = "repas"
            heure = "08:00"
            titre = "Petit-dejeuner"
            detail = "Surveiller appetit"
            dateEvenement = $date
            statut = "en_attente"
        },
        @{
            patient = @{ id = 1 }
            patientNom = "aziz aziz"
            type = "medicament"
            heure = "09:00"
            titre = "Prise de medicaments"
            detail = "Medicaments du matin"
            dateEvenement = $date
            statut = "en_attente"
        },
        @{
            patient = @{ id = 1 }
            patientNom = "aziz aziz"
            type = "repas"
            heure = "12:30"
            titre = "Dejeuner"
            detail = "Surveiller appetit"
            dateEvenement = $date
            statut = "en_attente"
        },
        @{
            patient = @{ id = 1 }
            patientNom = "aziz aziz"
            type = "medicament"
            heure = "14:00"
            titre = "Prise de medicaments"
            detail = "Medicaments apres-midi"
            dateEvenement = $date
            statut = "en_attente"
        },
        @{
            patient = @{ id = 1 }
            patientNom = "aziz aziz"
            type = "repas"
            heure = "19:00"
            titre = "Diner"
            detail = "Surveiller appetit"
            dateEvenement = $date
            statut = "en_attente"
        },
        @{
            patient = @{ id = 1 }
            patientNom = "aziz aziz"
            type = "medicament"
            heure = "20:00"
            titre = "Prise de medicaments"
            detail = "Medicaments du soir"
            dateEvenement = $date
            statut = "en_attente"
        }
    )
    
    foreach ($event in $evenements) {
        try {
            $json = $event | ConvertTo-Json -Depth 10
            $response = Invoke-RestMethod -Uri "http://localhost:8098/api/agenda" `
                -Method Post `
                -ContentType "application/json; charset=utf-8" `
                -Body $json
            
            $created++
        } catch {
            $errors++
        }
    }
    
    Write-Host "  $($evenements.Count) evenements crees" -ForegroundColor Green
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Resume Final:" -ForegroundColor Yellow
Write-Host "  Total evenements crees: $created" -ForegroundColor Green
Write-Host "  Erreurs: $errors" -ForegroundColor Red
Write-Host "  Jours couverts: 7 jours" -ForegroundColor White
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Acces agenda: http://localhost:4200/soignant-agenda" -ForegroundColor Cyan
Write-Host "Cliquez sur 'Semaine' pour voir tous les jours!" -ForegroundColor Green
Write-Host ""
Write-Host "Rafraichissez la page!" -ForegroundColor Yellow
