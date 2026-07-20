@echo off
REM ============================================================
REM Script de sauvegarde PostgreSQL — NG-Fields
REM Usage: backup-db.bat [output_directory]
REM ============================================================

setlocal

set PGHOST=%DB_HOST%
if "%PGHOST%"=="" set PGHOST=localhost
set PGPORT=%DB_PORT%
if "%PGPORT%"=="" set PGPORT=5432
set PGUSER=%DB_USER%
if "%PGUSER%"=="" set PGUSER=ng_fields_user

set DB_NAME=ng_fields
set OUTPUT_DIR=%~1
if "%OUTPUT_DIR%"=="" set OUTPUT_DIR=.\backups

if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"

set TIMESTAMP=%DATE:~-4%%DATE:~3,2%%DATE:~0,2%_%TIME:~0,2%%TIME:~3,2%
set TIMESTAMP=%TIMESTAMP: =0%

echo ============================================================
echo Sauvegarde PostgreSQL — NG-Fields
echo Base: %DB_NAME%
echo Hote: %PGHOST%:%PGPORT%
echo Date: %TIMESTAMP%
echo ============================================================

REM Sauvegarde complète (schéma auth)
echo [1/3] Sauvegarde du schema auth...
pg_dump -h %PGHOST% -p %PGPORT% -U %PGUSER% -d %DB_NAME% -n auth -F c -f "%OUTPUT_DIR%\auth_%TIMESTAMP%.dump" 2>nul
if %errorlevel% neq 0 echo ERREUR: Sauvegarde auth echouee

REM Sauvegarde complète (schéma client)
echo [2/3] Sauvegarde du schema client...
pg_dump -h %PGHOST% -p %PGPORT% -U %PGUSER% -d %DB_NAME% -n client -F c -f "%OUTPUT_DIR%\client_%TIMESTAMP%.dump" 2>nul
if %errorlevel% neq 0 echo ERREUR: Sauvegarde client echouee

REM Sauvegarde complète (schéma intervention)
echo [3/3] Sauvegarde du schema intervention...
pg_dump -h %PGHOST% -p %PGPORT% -U %PGUSER% -d %DB_NAME% -n intervention -F c -f "%OUTPUT_DIR%\intervention_%TIMESTAMP%.dump" 2>nul
if %errorlevel% neq 0 echo ERREUR: Sauvegarde intervention echouee

REM Sauvegarde complète (toutes les données)
echo [Bonus] Sauvegarde complete de la base...
pg_dump -h %PGHOST% -p %PGPORT% -U %PGUSER% -d %DB_NAME% -F c -f "%OUTPUT_DIR%\ng_fields_full_%TIMESTAMP%.dump" 2>nul
if %errorlevel% neq 0 echo ERREUR: Sauvegarde complete echouee

echo ============================================================
echo Sauvegarde terminee : %OUTPUT_DIR%
echo ============================================================

endlocal
