# Script pour executer la restauration des evenements via XAMPP MySQL

$mysqlPath = "C:\xampp\mysql\bin\mysql.exe"
$sqlFile = "restaurer-evenements.sql"

if (Test-Path $mysqlPath) {
    Write-Host "Execution du script SQL..." -ForegroundColor Cyan
    Get-Content $sqlFile | & $mysqlPath -u root -h localhost -P 3306
    Write-Host "Script execute avec succes!" -ForegroundColor Green
} else {
    Write-Host "MySQL non trouve. Chemin: $mysqlPath" -ForegroundColor Red
    Write-Host "Veuillez executer manuellement le fichier restaurer-evenements.sql dans phpMyAdmin" -ForegroundColor Yellow
}
