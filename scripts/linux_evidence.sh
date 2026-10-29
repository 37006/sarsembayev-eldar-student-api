#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
mkdir -p scripts logs backups src evidence
touch logs/Sarsembayev_Eldar_system_report.log
printf 'Initial log size: '; wc -c < logs/Sarsembayev_Eldar_system_report.log
find . -maxdepth 2 -not -path './.git*' -not -path './.gradle*' -not -path './build*' | sort
for file in Dockerfile docker-compose.yml Jenkinsfile build.gradle README.md scripts/Sarsembayev_Eldar_system_report.sh; do test -f "$file"; done
chmod 744 scripts/Sarsembayev_Eldar_system_report.sh
./scripts/Sarsembayev_Eldar_system_report.sh
cp README.md backups/Sarsembayev_Eldar_README_backup.md
ls -l scripts/Sarsembayev_Eldar_system_report.sh backups/Sarsembayev_Eldar_README_backup.md
cmp README.md backups/Sarsembayev_Eldar_README_backup.md
printf 'PASS: README backup matches the source\n'
ps aux
printf '\nJava processes, if present\n'
pgrep -a java || true
tail -n 5 logs/Sarsembayev_Eldar_system_report.log
grep 'Sarsembayev' logs/Sarsembayev_Eldar_system_report.log | tee evidence/Sarsembayev_Eldar_log_analysis.txt
printf '\nRedirection: > replaces a file; >> appends to a file.\n'
