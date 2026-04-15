# Script PowerShell pour exécuter le fix SQL via XAMPP MySQL

$mysqlPath = "C:\xampp\mysql\bin\mysql.exe"
$sqlFile = "fix_and_test_rapports.sql"

if (Test-Path $mysqlPath) {
    Write-Host "Exécution du script SQL..." -ForegroundColor Green
    & $mysqlPath -u root -proot -h localhost -P 3306 < $sqlFile
    Write-Host "Script SQL exécuté avec succès!" -ForegroundColor Green
} else {
    Write-Host "MySQL non trouvé à $mysqlPath" -ForegroundColor Red
    Write-Host "Veuillez exécuter manuellement dans phpMyAdmin ou ajuster le chemin" -ForegroundColor Yellow
}
