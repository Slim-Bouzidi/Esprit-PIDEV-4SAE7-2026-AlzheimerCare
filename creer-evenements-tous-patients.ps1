# Script pour creer des evenements pour TOUS les patients

$baseUrl = "http://localhost:8098/api/agenda"

# Liste des patients
$patients = @(
    @{ id = 1; nom = "Abdenour Foudaili" },
    @{ id = 2; nom = "Aziz Aziz" },
    @{ id = 3; nom = "Adem Adem" }
)

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

# Dates de la semaine (27 avril au 3 mai 2026)
$dates = @(
    "2026-04-27",
    "2026-04-28",
    "2026-04-29",
    "2026-04-30",
    "2026-05-01",
    "2026-05-02",
    "2026-05-03"
)

$total = 0

foreach ($patient in $patients) {
    Write-Host ""
    Write-Host "=== Creation des evenements pour $($patient.nom) ===" -ForegroundColor Cyan
    
    foreach ($date in $dates) {
        Write-Host "  Date: $date" -ForegroundColor Yellow
        
        foreach ($evt in $evenements) {
            $body = @{
                titre = $evt.titre
                dateEvenement = $date
                heure = $evt.heure
                type = $evt.type
                detail = "$($patient.nom) - $($evt.titre)"
                patientId = $patient.id
                statut = "en_attente"
            } | ConvertTo-Json
            
            try {
                $response = Invoke-RestMethod -Uri $baseUrl -Method Post -ContentType "application/json" -Body $body
                Write-Host "    OK $($evt.titre) a $($evt.heure)" -ForegroundColor Green
                $total++
            }
            catch {
                Write-Host "    ERREUR: $($evt.titre)" -ForegroundColor Red
            }
        }
    }
}

Write-Host ""
Write-Host "=== RESUME ===" -ForegroundColor Cyan
Write-Host "Total evenements crees: $total" -ForegroundColor Green
Write-Host "Evenements par patient: $($total / 3)" -ForegroundColor Yellow
