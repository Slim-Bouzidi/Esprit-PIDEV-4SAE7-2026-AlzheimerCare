# Script de test complet pour la création de rapport

Write-Host "`n╔════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║           TEST COMPLET - CRÉATION DE RAPPORT              ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════════════════╝`n" -ForegroundColor Cyan

# Test 1 : Vérifier que la table existe
Write-Host "TEST 1 : Vérification de l'existence de la table" -ForegroundColor Yellow
Write-Host "  Veuillez exécuter dans phpMyAdmin : DESCRIBE rapport;" -ForegroundColor White
Write-Host "  Appuyez sur Entrée une fois vérifié..." -ForegroundColor Gray
Read-Host

# Test 2 : Vérifier les services
Write-Host "`nTEST 2 : Vérification des services" -ForegroundColor Yellow
try {
    $patients = Invoke-RestMethod -Uri "http://localhost:8090/api/patients" -Method GET
    $validPatients = $patients | Where-Object { $_.nomComplet -and $_.nomComplet.Trim() -ne '' }
    Write-Host "  ✓ API Patients accessible - $($validPatients.Count) patients disponibles" -ForegroundColor Green
} catch {
    Write-Host "  ✗ Erreur d'accès à l'API Patients" -ForegroundColor Red
}

try {
    $user = Invoke-RestMethod -Uri "http://localhost:8090/api/users/8" -Method GET
    Write-Host "  ✓ User SOIGNANT trouvé : $($user.nom)" -ForegroundColor Green
} catch {
    Write-Host "  ✗ User SOIGNANT non trouvé" -ForegroundColor Red
}

# Test 3 : Création d'un rapport de test
Write-Host "`nTEST 3 : Création d'un rapport via l'API" -ForegroundColor Yellow

$rapportTest = @{
    patient = @{ id = 1 }
    soignant = @{ id = 8 }
    typeRapport = "HEBDOMADAIRE"
    periodeDebut = "2026-04-07"
    periodeFin = "2026-04-14"
    titre = "Rapport de test automatique"
    contenuTexte = "Medicament A (10mg) - matin : Suivi regulier`nMedicament B (20mg) - soir : Surveillance"
    directives = "Alimentation: Regime equilibre`nVie sociale: Activites en groupe"
    recommandations = "Suivi regulier recommande"
    statut = "GENERE"
} | ConvertTo-Json

try {
    $result = Invoke-RestMethod -Uri "http://localhost:8090/api/rapports" -Method POST -Body $rapportTest -ContentType "application/json; charset=utf-8"
    Write-Host "  ✓ RAPPORT CRÉÉ AVEC SUCCÈS !" -ForegroundColor Green
    Write-Host "    ID: $($result.id)" -ForegroundColor White
    Write-Host "    Titre: $($result.titre)" -ForegroundColor White
    Write-Host "    Type: $($result.typeRapport)" -ForegroundColor White
    Write-Host "    Statut: $($result.statut)" -ForegroundColor White
    
    $rapportId = $result.id
    
    # Test 4 : Vérifier que le rapport existe
    Write-Host "`nTEST 4 : Vérification du rapport créé" -ForegroundColor Yellow
    try {
        $rapport = Invoke-RestMethod -Uri "http://localhost:8090/api/rapports/$rapportId" -Method GET
        Write-Host "  ✓ Rapport récupéré avec succès" -ForegroundColor Green
        Write-Host "    Patient: $($rapport.patient.nomComplet)" -ForegroundColor White
        Write-Host "    Période: $($rapport.periodeDebut) à $($rapport.periodeFin)" -ForegroundColor White
    } catch {
        Write-Host "  ✗ Erreur lors de la récupération du rapport" -ForegroundColor Red
    }
    
    # Test 5 : Lister tous les rapports
    Write-Host "`nTEST 5 : Liste de tous les rapports" -ForegroundColor Yellow
    try {
        $rapports = Invoke-RestMethod -Uri "http://localhost:8090/api/rapports" -Method GET
        Write-Host "  ✓ $($rapports.Count) rapport(s) dans la base" -ForegroundColor Green
        $rapports | ForEach-Object {
            Write-Host "    - [$($_.id)] $($_.titre) ($($_.statut))" -ForegroundColor Gray
        }
    } catch {
        Write-Host "  ✗ Erreur lors de la récupération des rapports" -ForegroundColor Red
    }
    
} catch {
    Write-Host "  ✗ ERREUR LORS DE LA CRÉATION DU RAPPORT" -ForegroundColor Red
    Write-Host "    Message: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.ErrorDetails.Message) {
        Write-Host "    Détails: $($_.ErrorDetails.Message)" -ForegroundColor Red
    }
}

# Résumé
Write-Host "`n╔════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║                    RÉSUMÉ DES TESTS                        ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════════════════╝`n" -ForegroundColor Cyan

Write-Host "Si tous les tests sont verts (✓), l'application est prête !" -ForegroundColor Green
Write-Host "Vous pouvez maintenant créer des rapports via l'interface web." -ForegroundColor Green
Write-Host "`nURL de l'application : http://localhost:4200`n" -ForegroundColor Cyan
