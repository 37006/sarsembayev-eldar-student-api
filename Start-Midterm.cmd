@echo off
wsl -d DevOps-Backup-20261011 -u root -- bash /home/eldar/devops/sarsembayev-eldar-student-api/scripts/start_linux.sh
if errorlevel 1 pause
