# Script pour creer des evenements clairs et bien organises pour toute la semaine

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  CREATION D'EVENEMENTS AGENDA CLAIRS  " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$created = 0
$errors = 0

# Creer des evenements pour 7 jours
for ($i = 0; $i -lt 7; $i++) {
    $date = (Get-Date).AddDays($i).ToString("yyyy-MM-dd")
    $jourNom = (Get-Date).AddDays($i).ToString("dddd dd/MM/yyyy")
    
    Write-Host "Creation pour: $jourNom" -ForegroundColor Yellow
    
    # Evenements quotidiens CLAIRS et ORGANISES
    $evenements = @(
        # MATIN
        @{
            patient = @{ id = 1 }
            patientNom = "aziz aziz"
            type = "repas"
            heure = "08:00"
            titre = "Petit-dejeuner"
            detail = "Surveiller appetit et hydratation"
            dateEvenement = $date
            statut = "en_attente"
        },
        @{
            patient = @{ id = 1 }
            patientNom = "aziz aziz"
            type = "medicament"
            heure = "09:00"
            titre = "Medicaments matin"
            detail = "Traitement du matin"
            dateEvenement = $date
            statut = "en_attente"
        },
        @{
            patient = @{ id = 1 }
            patientNom = "aziz aziz"
            type = "soin"
            heure = "10:00"
            titre = "Toilette"
            detail = "Hygiene quotidienne"
            dateEvenement = $date
            statut = "en_attente"
        },
        
        # MIDI
        @{
            patient = @{ id = 1 }
            patientNom = "aziz aziz"
            type = "repas"
            heure = "12:30"
            titre = "Dejeuner"
            detail = "Repas principal"
            dateEvenement = $date
            statut = "en_attente"
        },
        @{
            patient = @{ id = 1 }
            patientNom = "aziz aziz"
            type = "medicament"
            heure = "14:00"
            titre = "Medicaments midi"
            detail = "Traitement apres-midi"
            dateEvenement = $date
            statut = "en_attente"
        },
        
        # APRES-MIDI
        @{
            patient = @{ id = 1 }
            patientNom = "aziz aziz"
            type = "soin"
            heure = "15:00"
            titre = "Activite"
            detail = "Stimulation cognitive"
            dateEvenement = $date
            statut = "en_attente"
        },
        
        # SOIR
        @{
            patient = @{ id = 1 }
            patientNom = "aziz aziz"
            type = "repas"
            heure = "19:00"
            titre = "Diner"
            detail = "Repas du soir"
            dateEvenement = $date
            statut = "en_attente"
        },
        @{
            patient = @{ id = 1 }
            patientNom = "aziz aziz"
            type = "medicament"
            heure = "20:00"
            titre = "Medicaments soir"
            detail = "Traitement du soir"
            dateEvenement = $date
            statut = "en_attente"
        },
        @{
            patient = @{ id = 1 }
            patientNom = "aziz aziz"
            type = "soin"
            heure = "21:00"
            titre = "Coucher"
            detail = "Preparation pour la nuit"
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
            Write-Host "  $($event.heure) - $($event.titre)" -ForegroundColor Green
        } catch {
            $errors++
            Write-Host "  ERREUR: $($event.heure) - $($event.titre)" -ForegroundColor Red
        }
    }
    
    Write-Host ""
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "RESUME FINAL" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Evenements crees: $created" -ForegroundColor Green
Write-Host "  Erreurs: $errors" -ForegroundColor Red
Write-Host "  Jours couverts: 7 jours" -ForegroundColor White
Write-Host "  Evenements par jour: 9" -ForegroundColor White
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "ORGANISATION QUOTIDIENNE:" -ForegroundColor Yellow
Write-Host "  MATIN:" -ForegroundColor Cyan
Write-Host "    08:00 - Petit-dejeuner" -ForegroundColor White
Write-Host "    09:00 - Medicaments matin" -ForegroundColor White
Write-Host "    10:00 - Toilette" -ForegroundColor White
Write-Host ""
Write-Host "  MIDI:" -ForegroundColor Cyan
Write-Host "    12:30 - Dejeuner" -ForegroundColor White
Write-Host "    14:00 - Medicaments midi" -ForegroundColor White
Write-Host ""
Write-Host "  APRES-MIDI:" -ForegroundColor Cyan
Write-Host "    15:00 - Activite" -ForegroundColor White
Write-Host ""
Write-Host "  SOIR:" -ForegroundColor Cyan
Write-Host "    19:00 - Diner" -ForegroundColor White
Write-Host "    20:00 - Medicaments soir" -ForegroundColor White
Write-Host "    21:00 - Coucher" -ForegroundColor White
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Acces agenda: http://localhost:4200/soignant-agenda" -ForegroundColor Cyan
Write-Host ""
Write-Host "RAFRAICHISSEZ LA PAGE MAINTENANT!" -ForegroundColor Green
Write-Host ""
