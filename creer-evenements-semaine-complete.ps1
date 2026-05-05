# Script pour creer des evenements pour la semaine complete affichee dans l'agenda

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  CREATION EVENEMENTS SEMAINE COMPLETE  " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Calculer le lundi de la semaine actuelle
$today = Get-Date
$dayOfWeek = [int]$today.DayOfWeek
if ($dayOfWeek -eq 0) { $dayOfWeek = 7 } # Dimanche = 7
$monday = $today.AddDays(1 - $dayOfWeek)

Write-Host "Semaine du: $($monday.ToString('dd/MM/yyyy'))" -ForegroundColor Yellow
Write-Host ""

$created = 0
$errors = 0

# Creer des evenements pour 7 jours a partir du lundi
for ($i = 0; $i -lt 7; $i++) {
    $date = $monday.AddDays($i).ToString("yyyy-MM-dd")
    $jourNom = $monday.AddDays($i).ToString("dddd dd/MM/yyyy")
    
    Write-Host "Creation pour: $jourNom" -ForegroundColor Yellow
    
    # Evenements quotidiens
    $evenements = @(
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
        } catch {
            $errors++
        }
    }
    
    Write-Host "  9 evenements crees" -ForegroundColor Green
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "RESUME" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Evenements crees: $created" -ForegroundColor Green
Write-Host "  Erreurs: $errors" -ForegroundColor Red
Write-Host "  Periode: $($monday.ToString('dd/MM')) au $($monday.AddDays(6).ToString('dd/MM/yyyy'))" -ForegroundColor White
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "RAFRAICHISSEZ LA PAGE: http://localhost:4200/soignant-agenda" -ForegroundColor Green
Write-Host ""
