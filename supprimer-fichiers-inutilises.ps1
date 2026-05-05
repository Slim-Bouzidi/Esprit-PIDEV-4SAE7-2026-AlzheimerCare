# Script pour supprimer les fichiers Java inutilises

$basePath = "alzheimer-system-main/backend/assistance quotidienne/src/main/java/assistancequotidienne2/assistancequotidienne2"

Write-Host "=== Suppression des fichiers inutilises ===" -ForegroundColor Cyan
Write-Host ""

# Liste des fichiers a supprimer
$filesToDelete = @(
    # EmergencyContact
    "$basePath/Entities/EmergencyContact.java",
    "$basePath/Controllers/EmergencyContactController.java",
    "$basePath/DTOs/EmergencyContactDTO.java",
    "$basePath/Repositories/EmergencyContactRepository.java",
    
    # MedicalRecord
    "$basePath/Entities/MedicalRecord.java",
    "$basePath/Controllers/MedicalRecordController.java",
    "$basePath/DTOs/MedicalRecordDTO.java",
    "$basePath/Repositories/MedicalRecordRepository.java",
    
    # Treatment
    "$basePath/Entities/Treatment.java",
    "$basePath/Controllers/TreatmentController.java",
    "$basePath/DTOs/TreatmentDTO.java",
    "$basePath/Repositories/TreatmentRepository.java"
)

$deleted = 0
$notFound = 0

foreach ($file in $filesToDelete) {
    if (Test-Path $file) {
        Remove-Item $file -Force
        Write-Host "  SUPPRIME: $file" -ForegroundColor Green
        $deleted++
    } else {
        Write-Host "  NON TROUVE: $file" -ForegroundColor Yellow
        $notFound++
    }
}

Write-Host ""
Write-Host "=== RESUME ===" -ForegroundColor Cyan
Write-Host "Fichiers supprimes: $deleted" -ForegroundColor Green
Write-Host "Fichiers non trouves: $notFound" -ForegroundColor Yellow
Write-Host ""
Write-Host "IMPORTANT: Recompilez le backend apres cette suppression!" -ForegroundColor Red
