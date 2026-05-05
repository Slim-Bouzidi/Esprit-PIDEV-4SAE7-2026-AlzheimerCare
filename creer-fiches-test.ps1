# Script pour creer des fiches de transmission de test

$baseUrl = "http://localhost:8098/api/fiches"

# Dates de la semaine
$dates = @("2026-04-27", "2026-04-28", "2026-04-29", "2026-04-30", "2026-05-01", "2026-05-02", "2026-05-03")

# Patients
$patients = @(1, 2, 3)

$total = 0

foreach ($patientId in $patients) {
    Write-Host "Creation fiches pour patient $patientId..." -ForegroundColor Cyan
    
    foreach ($date in $dates) {
        $body = @{
            patient = @{ id = $patientId }
            dateFiche = $date
            patientInfoJson = "{`"nom`":`"Patient$patientId`",`"prenom`":`"Test`",`"age`":75}"
            soignantInfoJson = "{`"nom`":`"Dubois`",`"prenom`":`"Sophie`",`"role`":`"Aide-Soignante`"}"
            observanceMedicamentsJson = "{`"listeMedicaments`":[],`"totalPris`":8,`"totalPrevus`":9}"
            alimentationJson = "{`"appetit`":`"bon`",`"hydratation`":`"suffisante`",`"repasPris`":3,`"repasPrevus`":3}"
            vieSocialeJson = "{`"activitesRealisees`":[`"Promenade`"],`"interaction`":`"normale`",`"hygiene`":`"autonome`",`"sommeil`":`"calme`"}"
            suiviDirectivesJson = "[]"
            signatureSoignant = $true
            commentaireLibre = "Fiche de test pour $date"
            statut = "brouillon"
        } | ConvertTo-Json -Compress
        
        try {
            $response = Invoke-RestMethod -Uri $baseUrl -Method Post -ContentType "application/json; charset=utf-8" -Body $body
            Write-Host "  OK: $date (ID: $($response.id))" -ForegroundColor Green
            $total++
        } catch {
            Write-Host "  ERREUR: $date" -ForegroundColor Red
        }
    }
}

Write-Host ""
Write-Host "Total fiches creees: $total" -ForegroundColor Green
Write-Host ""
Write-Host "Maintenant, allez sur la page 'Rapports Hebdomadaires' et cliquez sur 'Generer Rapports'" -ForegroundColor Yellow
