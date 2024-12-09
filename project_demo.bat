@echo off

cd ./out/production/DAS
start cmd /k "java DAS 9998 0" REM Master
start cmd /c "java DAS 9998 10" REM Slave 1 (send 10)
start cmd /c "java DAS 9998 15" REM Slave 2 (send 15)
start cmd /c "java DAS 9998 0" REM Slave 3 (broadcast avg)
timeout /t 5 /nobreak > nul
start cmd /c "java DAS 9998 -1" REM Slave 4 (broadcast exit)
