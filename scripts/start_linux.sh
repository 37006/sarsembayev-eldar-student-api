#!/usr/bin/env bash
set -euo pipefail
systemctl start docker jenkins
project=/home/eldar/devops/sarsembayev-eldar-student-api
cd "$project"
runuser -u eldar -- bash scripts/configure_env.sh
runuser -u eldar -- docker compose up -d
bash scripts/wait_for_api.sh
printf '\nStudent API: http://127.0.0.1:8083/\nJenkins: http://127.0.0.1:8080/\nKeep this window open while using the lab. Ctrl+C closes this launcher.\n'
# A foreground WSL process keeps the distribution available between commands.
exec tail -f /dev/null
