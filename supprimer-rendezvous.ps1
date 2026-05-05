# Script pour supprimer les fichiers RendezVous inutilises

$basePath = "alzheimer-system-main/backend/assistance quotidienne/src/main/java/assistancequotidienne2/assistancequotidienne2"

Write-Host "=== Suppression des fichiers RendezVous ===" -ForegroundColor Cyan
Write-Host ""

$filesToDelete = @(
    "$basePath/Entities/RendezVous.java",
    "$basePath/Entities/StatutRendezVous.java",
    "$basePath/Entities/TypeRendezVous.java",
    "$basePath/Controllers/RendezVousController.java",
    "$basePath/Repositories/RendezVousRepository.java",
    "$basePath/DTOs/RendezVousDTO.java"
)

$deleted = 0
$notFound = 0

foreach ($file in $filesToDelete) {
    if (Test-Path $file) {
        Remove-Item $file -Force
        Write-Host "  SUPPRIME: $(Split-Path $file -Leaf)" -ForegroundColor Green
        $deleted++
    } else {
        Write-Host "  NON TROUVE: $(Split-Path $file -Leaf)" -ForegroundColor Yellow
        $notFound++
    }
}

Write-Host ""
Write-Host "=== RESUME ===" -ForegroundColor Cyan
Write-Host "Fichiers supprimes: $deleted" -ForegroundColor Green
Write-Host "Fichiers non trouves: $notFound" -ForegroundColor Yellow
