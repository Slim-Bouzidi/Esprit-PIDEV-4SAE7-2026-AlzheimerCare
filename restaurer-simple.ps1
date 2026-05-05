# Restaurer les evenements via l'API REST

$baseUrl = "http://localhost:8098/api/agenda"

# Evenements quotidiens
$evenements = @(
    @{ titre = "Petit-dejeuner"; heure = "08:00"; type = "repas" },
    @{ titre = "Medicaments matin"; heure = "09:00"; type = "medicament" },
    @{ titre = "Toilette"; heure = "10:00"; type = "activite" },
    @{ titre = "Dejeuner"; heure = "12:30"; type = "repas" },
    @{ titre = "Medicaments midi"; heure = "14:00"; type = "medicament" },
    @{ titre = "Activite"; heure = "15:00"; type = "activite" },
    @{ titre = "Diner"; heure = "19:00"; type = "repas" },
    @{ titre = "Medicaments soir"; heure = "20:00"; type = "medicament" },
    @{ titre = "Coucher"; heure = "21:00"; type = "activite" }
)

# Dates
$dates = @("2026-04-27", "2026-04-28", "2026-04-29", "2026-04-30", "2026-05-01", "2026-05-02", "2026-05-03")

# Patients
$patients = @(
    @{ id = 1; nom = "Abdenour Foudaili" },
    @{ id = 2; nom = "Aziz Aziz" },
    @{ id = 3; nom = "Adem Adem" }
)

$total = 0
foreach ($patient in $patients) {
    Write-Host "Patient: $($patient.nom)" -ForegroundColor Cyan
    foreach ($date in $dates) {
        foreach ($evt in $evenements) {
            $body = @{
                titre = $evt.titre
                dateEvenement = $date
                heure = $evt.heure
                type = $evt.type
                detail = $patient.nom
                patientNom = $patient.nom
                patientId = $patient.id
                statut = "en_attente"
            } | ConvertTo-Json -Compress
            
            try {
                Invoke-WebRequest -Uri $baseUrl -Method Post -ContentType "application/json; charset=utf-8" -Body $body -UseBasicParsing | Out-Null
                $total++
                Write-Host "." -NoNewline -ForegroundColor Green
            } catch {
                Write-Host "x" -NoNewline -ForegroundColor Red
            }
        }
    }
    Write-Host ""
}

Write-Host "`nTotal cree: $total evenements pour $($patients.Count) patients" -ForegroundColor Green
