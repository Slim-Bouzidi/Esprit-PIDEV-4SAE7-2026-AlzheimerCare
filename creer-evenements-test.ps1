# Script pour creer des evenements de test manuellement

Write-Host "Creation d'evenements de test..." -ForegroundColor Cyan
Write-Host ""

$dateAujourdhui = Get-Date -Format "yyyy-MM-dd"

# Evenements pour le patient 1 (aziz aziz)
$evenements = @(
    @{
        patient = @{ id = 1 }
        patientNom = "aziz aziz"
        type = "repas"
        heure = "08:00"
        titre = "Petit-dejeuner"
        detail = "Surveiller appetit"
        dateEvenement = $dateAujourdhui
        statut = "en_attente"
    },
    @{
        patient = @{ id = 1 }
        patientNom = "aziz aziz"
        type = "medicament"
        heure = "09:00"
        titre = "Prise de medicaments"
        detail = "Medicaments du matin"
        dateEvenement = $dateAujourdhui
        statut = "en_attente"
    },
    @{
        patient = @{ id = 1 }
        patientNom = "aziz aziz"
        type = "repas"
        heure = "12:30"
        titre = "Dejeuner"
        detail = "Surveiller appetit"
        dateEvenement = $dateAujourdhui
        statut = "en_attente"
    },
    @{
        patient = @{ id = 1 }
        patientNom = "aziz aziz"
        type = "medicament"
        heure = "14:00"
        titre = "Prise de medicaments"
        detail = "Medicaments apres-midi"
        dateEvenement = $dateAujourdhui
        statut = "en_attente"
    },
    @{
        patient = @{ id = 1 }
        patientNom = "aziz aziz"
        type = "repas"
        heure = "19:00"
        titre = "Diner"
        detail = "Surveiller appetit"
        dateEvenement = $dateAujourdhui
        statut = "en_attente"
    },
    @{
        patient = @{ id = 1 }
        patientNom = "aziz aziz"
        type = "medicament"
        heure = "20:00"
        titre = "Prise de medicaments"
        detail = "Medicaments du soir"
        dateEvenement = $dateAujourdhui
        statut = "en_attente"
    }
)

$created = 0
$errors = 0

foreach ($event in $evenements) {
    try {
        $json = $event | ConvertTo-Json -Depth 10
        $response = Invoke-RestMethod -Uri "http://localhost:8098/api/agenda" `
            -Method Post `
            -ContentType "application/json; charset=utf-8" `
            -Body $json
        
        Write-Host "Cree: $($event.heure) - $($event.titre)" -ForegroundColor Green
        $created++
    } catch {
        Write-Host "Erreur: $($event.heure) - $($event.titre)" -ForegroundColor Red
        $errors++
    }
}

Write-Host ""
Write-Host "Resume:" -ForegroundColor Yellow
Write-Host "  Evenements crees: $created" -ForegroundColor Green
Write-Host "  Erreurs: $errors" -ForegroundColor Red
Write-Host ""
Write-Host "Acces agenda: http://localhost:4200/soignant-agenda" -ForegroundColor Cyan
Write-Host "Rafraichissez la page!" -ForegroundColor Green
