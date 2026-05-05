# Script pour mettre a jour les patients via l'API

Write-Host "Mise a jour des patients..." -ForegroundColor Cyan
Write-Host ""

$patients = @(
    @{
        id = 1
        nom = "Foudaili"
        prenom = "Abdenour"
        nomComplet = "Abdenour Foudaili"
        actif = $true
    },
    @{
        id = 2
        nom = "Aziz"
        prenom = "Aziz"
        nomComplet = "Aziz Aziz"
        actif = $true
    },
    @{
        id = 3
        nom = "Adem"
        prenom = "Adem"
        nomComplet = "Adem Adem"
        actif = $true
    }
)

$updated = 0
$errors = 0

foreach ($patient in $patients) {
    try {
        $json = $patient | ConvertTo-Json -Depth 10
        $response = Invoke-RestMethod -Uri "http://localhost:8098/api/patients/$($patient.id)" `
            -Method Put `
            -ContentType "application/json; charset=utf-8" `
            -Body $json
        
        Write-Host "Patient $($patient.id) mis a jour: $($patient.nomComplet)" -ForegroundColor Green
        $updated++
    } catch {
        Write-Host "Erreur patient $($patient.id): $($_.Exception.Message)" -ForegroundColor Red
        $errors++
    }
}

Write-Host ""
Write-Host "Resume:" -ForegroundColor Yellow
Write-Host "  Patients mis a jour: $updated" -ForegroundColor Green
Write-Host "  Erreurs: $errors" -ForegroundColor Red
Write-Host ""
Write-Host "Rafraichissez la page de l'agenda!" -ForegroundColor Cyan
